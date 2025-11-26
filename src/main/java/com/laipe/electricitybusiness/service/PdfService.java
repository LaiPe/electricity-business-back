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
import com.laipe.electricitybusiness.model.Booking;
import com.laipe.electricitybusiness.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class PdfService {

    public byte[] generateBookingPdf(Booking booking, User vehicleOwner, User stationOwner) throws IOException {
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
