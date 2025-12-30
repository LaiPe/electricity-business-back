package com.laipe.electricitybusiness.service;

import com.laipe.electricitybusiness.controller.handler.InvalidBookingState;
import com.laipe.electricitybusiness.controller.handler.ResourceNotFoundException;
import com.laipe.electricitybusiness.model.Booking;
import com.laipe.electricitybusiness.model.BookingState;
import com.laipe.electricitybusiness.model.User;
import com.laipe.electricitybusiness.repository.BookingRepository;
import com.laipe.electricitybusiness.repository.ChargingStationRepository;
import com.laipe.electricitybusiness.repository.UserRepository;
import com.laipe.electricitybusiness.repository.VehicleRepository;
import com.laipe.electricitybusiness.service.generic.GenericJPAService;
import com.laipe.electricitybusiness.utils.DateUtil;
import com.laipe.electricitybusiness.utils.ModelUtil;
import com.laipe.electricitybusiness.utils.PowerCalculatorUtil;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BookingService extends GenericJPAService<Booking, Long> {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final PdfService pdfService;
    private final ExcelService excelService;
    private final PowerCalculatorUtil powerCalculatorUtil;
    private final DateUtil dateUtil;
    private final VehicleRepository vehicleRepository;
    private final ChargingStationRepository chargingStationRepository;

    public BookingService(
            BookingRepository bookingRepository,
            UserRepository userRepository,
            PdfService pdfService,
            ExcelService excelService,
            PowerCalculatorUtil powerCalculatorUtil,
            DateUtil dateUtil,
            VehicleRepository vehicleRepository, ChargingStationRepository chargingStationRepository) {
        super(bookingRepository);
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.pdfService = pdfService;
        this.excelService = excelService;
        this.powerCalculatorUtil = powerCalculatorUtil;
        this.dateUtil = dateUtil;
        this.vehicleRepository = vehicleRepository;
        this.chargingStationRepository = chargingStationRepository;
    }

    @Override
    public Booking create(Booking entity) {
        // Check if vehicle is deleted
        vehicleRepository.findById(entity.getVehicle().getId())
                .filter(e -> e.getDeletedAt() != null) // Filter undeleted vehicle
                .ifPresent(e -> {
                    throw new IllegalArgumentException("Cannot create a booking for a deleted vehicle.");
                });

        // Check if charging station is deleted
        chargingStationRepository.findById(entity.getStation().getId())
                    .filter(e -> e.getDeletedAt() != null)
                    .ifPresent(e -> {
                        throw new IllegalArgumentException("Cannot create a booking for a deleted charging station.");
                    });

        // Check for overlapping bookings
        bookingRepository.findAllByStationId(entity.getStation().getId())
                .forEach(existingBooking -> {
                    if (dateUtil.doOverlap(
                            existingBooking.getStartDate(),
                            existingBooking.getExpectedEndDate(),
                            entity.getStartDate(),
                            entity.getExpectedEndDate()
                    ) && existingBooking.isActive()) {
                        throw new InvalidBookingState("The station is already booked for the selected time interval.");
                    }
                });

        entity.setState(BookingState.PENDING_ACCEPT);
        return super.create(entity);
    }

    @Override
    public Optional<Booking> update(Booking entity, Long id) {
        // A booking cannot be soft-deleted, so we just get vehicle and station from existing booking
        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id, Booking.class));
        entity.setVehicle(existingBooking.getVehicle());
        entity.setStation(existingBooking.getStation());

        // Check if vehicle is deleted
        vehicleRepository.findById(entity.getVehicle().getId())
                .filter(e -> e.getDeletedAt() != null) // Filter undeleted vehicle
                .ifPresent(e -> {
                    throw new IllegalArgumentException("Cannot update a booking for a deleted vehicle.");
                });

        // Check if charging station is deleted
        chargingStationRepository.findById(entity.getVehicle().getId())
                    .filter(e -> e.getDeletedAt() != null)
                    .ifPresent(e -> {
                        throw new IllegalArgumentException("Cannot update a booking for a deleted charging station.");
                    });

        return super.update(entity, id);
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

        // Before accepting, check state
        Optional<Booking> savedBooking = bookingRepository.findById(id)
                .map(existingBooking -> {
                    if (existingBooking.getState() == BookingState.PENDING_ACCEPT) {
                        ModelUtil.copyFields(updatedBooking, existingBooking);
                        return bookingRepository.save(existingBooking);
                    } else {
                        throw new InvalidBookingState("Booking with id " + id + " is not in PENDING_ACCEPT state.");
                    }
                });

        // After accepting, check for overlapping bookings and reject them
        Booking acceptedBooking = savedBooking.orElseThrow(() -> new ResourceNotFoundException(id, Booking.class));
        bookingRepository.findAllByStationId(acceptedBooking.getStation().getId())
                .forEach(existingBooking -> {
                    if (dateUtil.doOverlap(
                            existingBooking.getStartDate(),
                            existingBooking.getExpectedEndDate(),
                            acceptedBooking.getStartDate(),
                            acceptedBooking.getExpectedEndDate()
                    ) && existingBooking.getState() == BookingState.PENDING_ACCEPT) {
                        Booking toReject = new Booking();
                        toReject.setState(BookingState.REJECTED);
                        ModelUtil.copyFields(toReject, existingBooking);
                        bookingRepository.save(existingBooking);
                    }
                });

        return savedBooking;
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
                    if (existingBooking.getState() == BookingState.ACCEPTED || existingBooking.getState() == BookingState.PENDING_ACCEPT) {
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
        if (booking.getState() != BookingState.ACCEPTED && booking.getState() != BookingState.ONGOING && booking.getState() != BookingState.COMPLETED) {
            throw new InvalidBookingState("Cannot generate PDF for this booking because it is not accepted yet or anymore");
        }

        User vehicleOwner = userRepository.findVehicleOwnerBookingById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id, Booking.class));

        User stationOwner = userRepository.findStationOwnerBookingById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id, Booking.class));

        try {
            return pdfService.generateBookingPdf(booking, vehicleOwner, stationOwner);
        } catch (IOException e) {
            throw new RuntimeException("Error generating PDF for booking with id " + id, e);
        }
    }

    public byte[] generateBookingsExcelForUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(userId, User.class));
        List<Booking> bookingsAsVehiculeOwner = bookingRepository.findAllByVehicleOwnerId(userId);
        List<Booking> bookingsAsStationOwner = bookingRepository.findAllByStationOwnerId(userId);
        try {
            return excelService.generateBookingsExcel(user, bookingsAsStationOwner, bookingsAsVehiculeOwner);
        } catch (Exception e) {
            throw new RuntimeException("Error generating Excel for bookings of user with id " + userId, e);
        }
    }
}
