package com.laipe.electricitybusiness.service;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import com.laipe.electricitybusiness.controller.handler.InvalidBookingState;
import com.laipe.electricitybusiness.controller.handler.ResourceNotFoundException;
import com.laipe.electricitybusiness.model.Booking;
import com.laipe.electricitybusiness.model.BookingState;
import com.laipe.electricitybusiness.model.User;
import com.laipe.electricitybusiness.repository.BookingRepository;
import com.laipe.electricitybusiness.repository.UserRepository;
import com.laipe.electricitybusiness.service.generic.GenericJPAService;
import com.laipe.electricitybusiness.utils.DateUtil;
import com.laipe.electricitybusiness.utils.ModelUtil;
import com.laipe.electricitybusiness.utils.PowerCalculatorUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookingService extends GenericJPAService<Booking, Long> {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PowerCalculatorUtil powerCalculatorUtil;
    private final DateUtil dateUtil;

    public BookingService(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            PowerCalculatorUtil powerCalculatorUtil,
            DateUtil dateUtil
    ) {
        super(bookingRepository);
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.powerCalculatorUtil = powerCalculatorUtil;
        this.dateUtil = dateUtil;
    }

    @Override
    public Booking create(Booking entity) {
        bookingRepository.findAllByStationId(entity.getStation().getId())
                .forEach(existingBooking -> {
                    if (dateUtil.doOverlap(
                            existingBooking.getStartDate(),
                            existingBooking.getExpectedEndDate(),
                            entity.getStartDate(),
                            entity.getExpectedEndDate()
                    )) {
                        throw new InvalidBookingState("The station is already booked for the selected time interval.");
                    }
                });
        entity.setState(BookingState.PENDING_ACCEPT);
        return super.create(entity);
    }

    public List<Booking> getAllByVehicleOwnerId(Long ownerId) {
        return bookingRepository.findAllByVehicleOwnerId(ownerId);
    }

    public List<Booking> getAllByStationOwnerId(Long ownerId) {
        return bookingRepository.findAllByStationOwnerId(ownerId);
    }

    public Optional<Booking> acceptBooking(Long id) {
        Booking updatedBooking = new Booking();
        updatedBooking.setState(BookingState.ACCEPTED);

        return bookingRepository.findById(id)
                .map(existingBooking -> {
                    if (existingBooking.getState() == BookingState.PENDING_ACCEPT) {
                        ModelUtil.copyFields(updatedBooking, existingBooking);
                        return bookingRepository.save(existingBooking);
                    } else {
                        throw new InvalidBookingState("Booking with id " + id + " is not in PENDING_ACCEPT state.");
                    }
                });
    }

    public Optional<Booking> rejectBooking(Long id) {
        Booking updatedBooking = new Booking();
        updatedBooking.setState(BookingState.REJECTED);

        return bookingRepository.findById(id)
                .map(existingBooking -> {
                    if (existingBooking.getState() == BookingState.PENDING_ACCEPT) {
                        ModelUtil.copyFields(updatedBooking, existingBooking);
                        return bookingRepository.save(existingBooking);
                    } else {
                        throw new InvalidBookingState("Booking with id " + id + " is not in PENDING_ACCEPT state.");
                    }
                });
    }

    public Optional<Booking> cancelBooking(Long id) {
        Booking updatedBooking = new Booking();
        updatedBooking.setState(BookingState.CANCELLED);

        return bookingRepository.findById(id)
                .map(existingBooking -> {
                    if (existingBooking.getState() == BookingState.ACCEPTED) {
                        ModelUtil.copyFields(updatedBooking, existingBooking);
                        return bookingRepository.save(existingBooking);
                    } else {
                        throw new InvalidBookingState("Booking with id " + id + " is not in ACCEPTED state.");
                    }
                });
    }

    public Optional<Booking> startBooking(Long id) {
        Booking updatedBooking = new Booking();
        updatedBooking.setState(BookingState.ONGOING);

        return bookingRepository.findById(id)
                .map(existingBooking -> {
                    if (existingBooking.getState() == BookingState.ACCEPTED) {
                        ModelUtil.copyFields(updatedBooking, existingBooking);
                        return bookingRepository.save(existingBooking);
                    } else {
                        throw new InvalidBookingState("Booking with id " + id + " is not in ACCEPTED state.");
                    }
                });
    }

    public Optional<Booking> endBooking(Long id) {
        Booking updatedBooking = new Booking();
        updatedBooking.setState(BookingState.COMPLETED);
        updatedBooking.setActualEndDate(LocalDateTime.now());

        return bookingRepository.findById(id)
                .map(existingBooking -> {
                    if (existingBooking.getState() == BookingState.ONGOING) {
                        updatedBooking.setFinalConsumptionKwh(BigDecimal.valueOf(powerCalculatorUtil.calculateConsumedPower(
                                existingBooking.getStation().getPowerKw().doubleValue(),
                                existingBooking.getStartDate(),
                                updatedBooking.getActualEndDate()
                        )));
                        updatedBooking.setFinalPrice(BigDecimal.valueOf(powerCalculatorUtil.calculateCost(
                                updatedBooking.getFinalConsumptionKwh().doubleValue(),
                                existingBooking.getStation().getPricePerKwh().doubleValue()
                        )));
                        ModelUtil.copyFields(updatedBooking, existingBooking);
                        return bookingRepository.save(existingBooking);
                    } else {
                        throw new InvalidBookingState("Booking with id " + id + " is not in ONGOING state.");
                    }
                });
    }

    public Optional<Booking> reviewBooking(Long id, Integer grade, String comment) {
        return bookingRepository.findById(id)
                .map(existingBooking -> {
                    if (existingBooking.getState() == BookingState.COMPLETED) {
                        existingBooking.setReviewGrade(grade);
                        existingBooking.setReviewComment(comment);
                        return bookingRepository.save(existingBooking);
                    } else {
                        throw new InvalidBookingState("Booking with id " + id + " is not in COMPLETED state.");
                    }
                });
    }

    public byte[] generateBookingPdfById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id, Booking.class));

        User vehicleOwner = userRepository.findVehicleOwnerBookingById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id, User.class));

        User stationOwner = userRepository.findStationOwnerBookingById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id, User.class));

        try {
            return generateBookingPdf(booking, vehicleOwner, stationOwner);
        } catch (IOException e) {
            throw new RuntimeException("Error generating PDF for booking with id " + id, e);
        }
    }

    private byte[] generateBookingPdf(Booking booking, User vehicleOwner, User stationOwner) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);

        // Définir une police
        PdfFont boldFont = PdfFontFactory.createFont(
                com.itextpdf.io.font.constants.StandardFonts.HELVETICA_BOLD
        );
        PdfFont regularFont = PdfFontFactory.createFont(
                com.itextpdf.io.font.constants.StandardFonts.HELVETICA
        );

        // Formats de date
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH:mm");

        // En-tête - Titre
        Paragraph title = new Paragraph("Confirmation de réservation")
                .setFont(boldFont)
                .setFontSize(24)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(title);

        // Informations de la facture
        Paragraph invoiceInfo = new Paragraph()
                .setFont(regularFont)
                .setFontSize(10)
                .add("Numéro de réservation : " + booking.getId() + "\n")
                .add("Date d'édition : " + LocalDate.now().format(dateFormatter) + "\n")
                .setMarginBottom(20);
        document.add(invoiceInfo);

        // Paragraphe centré - Résumé de la réservation
        Paragraph summary = new Paragraph()
                .setFont(regularFont)
                .setFontSize(13)
                .setTextAlignment(TextAlignment.CENTER)
                .setFixedLeading(24)
                .add("Nous confirmons la réservation de la borne de recharge\n")
                .add("appartenant à ")
                .add(new Paragraph(stationOwner.getFirstName() + " " + stationOwner.getLastName())
                        .setFont(boldFont))
                .add("\n\npour le véhicule de ")
                .add(new Paragraph(vehicleOwner.getFirstName() + " " + vehicleOwner.getLastName())
                        .setFont(boldFont))
                .add(".\n\n")
                .add("La session de recharge débutera le\n")
                .add(new Paragraph(booking.getStartDate().format(dateTimeFormatter))
                        .setFont(boldFont)
                        .setFontSize(14))
                .add("\n\net se terminera le\n")
                .add(new Paragraph(booking.getExpectedEndDate().format(dateTimeFormatter))
                        .setFont(boldFont)
                        .setFontSize(14))
                .add(".")
                .setMarginTop(20)
                .setMarginBottom(40);
        document.add(summary);

        // Ligne de séparation (optionnel)
        document.add(new Paragraph("\n")
                .setMarginTop(30)
                .setBorderTop(new com.itextpdf.layout.borders.SolidBorder(
                        new DeviceRgb(200, 200, 200), 1)));

        // Message de rappel
        Paragraph reminder = new Paragraph(
                "Merci de vous assurer d'être présent à l'heure indiquée.\n" +
                        "En cas d'empêchement, veuillez annuler votre réservation dans les meilleurs délais.")
                .setFont(regularFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.DARK_GRAY)
                .setMarginTop(40);
        document.add(reminder);

        // Pied de page
        Paragraph footer = new Paragraph("Cette confirmation a été générée automatiquement.")
                .setFont(regularFont)
                .setFontSize(9)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.LIGHT_GRAY)
                .setMarginTop(50);
        document.add(footer);

        document.close();
        return baos.toByteArray();
    }
}
