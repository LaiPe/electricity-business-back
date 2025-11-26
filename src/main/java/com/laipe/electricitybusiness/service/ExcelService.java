package com.laipe.electricitybusiness.service;

import com.laipe.electricitybusiness.model.Booking;
import com.laipe.electricitybusiness.model.User;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelService {

    /**
     * Génère un fichier Excel avec la liste des réservations d'un utilisateur
     */
    public byte[] generateBookingsExcel(User user, List<Booking> bookingsAsStationOwner, List<Booking> bookingsAsVehiculeOwner) throws Exception {
        // Créer un nouveau workbook
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Réservations");

        // Styles
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dateStyle = createDateStyle(workbook);
        CellStyle normalStyle = createNormalStyle(workbook);
        CellStyle sectionTitleStyle = createSectionTitleStyle(workbook);

        // Ligne de titre principal
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Liste des réservations de " + (user != null ? user.getUsername() : ""));
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 16);
        titleStyle.setFont(titleFont);
        titleCell.setCellStyle(titleStyle);

        // Date de génération
        Row dateRow = sheet.createRow(1);
        Cell dateCell = dateRow.createCell(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        dateCell.setCellValue("Généré le : " + LocalDateTime.now().format(formatter));
        dateCell.setCellStyle(normalStyle);

        int rowNum = 2;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        String[] headers = {
                "N° Réservation",
                "Propriétaire de la borne",
                "Propriétaire du véhicule",
                "Date de début",
                "Date de fin attendue",
                "Date de fin réelle",
                "Durée (heures)",
                "Statut",
                "Localisation"
        };

        // SECTION 1 : Réservations en tant que propriétaire de borne
        if (bookingsAsStationOwner != null && !bookingsAsStationOwner.isEmpty()) {
            // Ligne vide
            sheet.createRow(rowNum++);

            // Titre de section
            Row sectionRow = sheet.createRow(rowNum++);
            Cell sectionCell = sectionRow.createCell(0);
            sectionCell.setCellValue("Réservations de mes bornes de recharge (" + bookingsAsStationOwner.size() + ")");
            sectionCell.setCellStyle(sectionTitleStyle);

            // En-têtes des colonnes
            Row headerRow = sheet.createRow(rowNum++);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Données des réservations
            rowNum = addBookingsData(sheet, bookingsAsStationOwner, rowNum, dateFormatter, normalStyle, dateStyle, workbook);
        }

        // SECTION 2 : Réservations en tant que propriétaire de véhicule
        if (bookingsAsVehiculeOwner != null && !bookingsAsVehiculeOwner.isEmpty()) {
            // Ligne vide
            sheet.createRow(rowNum++);

            // Titre de section
            Row sectionRow = sheet.createRow(rowNum++);
            Cell sectionCell = sectionRow.createCell(0);
            sectionCell.setCellValue("Réservations de mes véhicules (" + bookingsAsVehiculeOwner.size() + ")");
            sectionCell.setCellStyle(sectionTitleStyle);

            // En-têtes des colonnes
            Row headerRow = sheet.createRow(rowNum++);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Données des réservations
            rowNum = addBookingsData(sheet, bookingsAsVehiculeOwner, rowNum, dateFormatter, normalStyle, dateStyle, workbook);
        }

        // Ajuster automatiquement la largeur des colonnes
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            // Ajouter un peu d'espace supplémentaire
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
        }

        // Écrire dans un ByteArrayOutputStream
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        workbook.write(baos);
        workbook.close();

        return baos.toByteArray();
    }

    /**
     * Ajoute les données de réservation dans le sheet
     */
    private int addBookingsData(Sheet sheet, List<Booking> bookings, int startRowNum,
                                 DateTimeFormatter dateFormatter, CellStyle normalStyle,
                                 CellStyle dateStyle, Workbook workbook) {
        int rowNum = startRowNum;

        for (Booking booking : bookings) {
            Row row = sheet.createRow(rowNum++);

            // N° Réservation
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(booking != null && booking.getId() != null ? booking.getId().toString() : "");
            cell0.setCellStyle(normalStyle);

            // Propriétaire de la borne
            Cell cell1 = row.createCell(1);
            String stationOwnerName = "";
            if (booking != null && booking.getStation() != null
                && booking.getStation().getPlace() != null
                && booking.getStation().getPlace().getOwner() != null) {
                stationOwnerName = booking.getStation().getPlace().getOwner().getUsername();
            }
            cell1.setCellValue(stationOwnerName);
            cell1.setCellStyle(normalStyle);

            // Propriétaire du véhicule
            Cell cell2 = row.createCell(2);
            String vehicleOwnerName = "";
            if (booking != null && booking.getVehicle() != null && booking.getVehicle().getOwner() != null) {
                vehicleOwnerName = booking.getVehicle().getOwner().getUsername();
            }
            cell2.setCellValue(vehicleOwnerName);
            cell2.setCellStyle(normalStyle);

            // Date de début
            Cell cell3 = row.createCell(3);
            if (booking != null && booking.getStartDate() != null) {
                cell3.setCellValue(booking.getStartDate().format(dateFormatter));
            } else {
                cell3.setCellValue("");
            }
            cell3.setCellStyle(dateStyle);

            // Date de fin attendue
            Cell cell4 = row.createCell(4);
            if (booking != null && booking.getExpectedEndDate() != null) {
                cell4.setCellValue(booking.getExpectedEndDate().format(dateFormatter));
            } else {
                cell4.setCellValue("");
            }
            cell4.setCellStyle(dateStyle);

            // Date de fin réelle
            Cell cell5 = row.createCell(5);
            if (booking != null && booking.getActualEndDate() != null) {
                cell5.setCellValue(booking.getActualEndDate().format(dateFormatter));
            } else {
                cell5.setCellValue("");
            }
            cell5.setCellStyle(dateStyle);

            // Durée en heures
            Cell cell6 = row.createCell(6);
            long duration = 0;
            if (booking != null && booking.getStartDate() != null && booking.getExpectedEndDate() != null) {
                duration = java.time.Duration.between(
                        booking.getStartDate(),
                        booking.getExpectedEndDate()
                ).toHours();
            }
            cell6.setCellValue(duration);
            cell6.setCellStyle(normalStyle);

            // Statut
            Cell cell7 = row.createCell(7);
            String status = booking != null && booking.getState() != null ? booking.getState().name() : "";
            cell7.setCellValue(status);
            cell7.setCellStyle(getStatusCellStyle(workbook, status));

            // Localisation
            Cell cell8 = row.createCell(8);
            String location = "";
            if (booking != null && booking.getStation() != null && booking.getStation().getPlace() != null) {
                location = booking.getStation().getPlace().getName();
            }
            cell8.setCellValue(location);
            cell8.setCellStyle(normalStyle);
        }

        return rowNum;
    }

    // Styles
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createNormalStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = createNormalStyle(workbook);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createStatusStyle(Workbook workbook) {
        CellStyle style = createNormalStyle(workbook);
        style.setAlignment(HorizontalAlignment.CENTER);
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    private CellStyle createSectionTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 14);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    private CellStyle getStatusCellStyle(Workbook workbook, String status) {
        CellStyle style = createStatusStyle(workbook);

        if (status == null || status.isEmpty()) {
            style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            return style;
        }

        switch (status.toUpperCase()) {
            case "PENDING_ACCEPT":
                style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
                break;
            case "ACCEPTED":
                style.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
                break;
            case "REJECTED":
                style.setFillForegroundColor(IndexedColors.CORAL.getIndex());
                break;
            case "ONGOING":
                style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
                break;
            case "COMPLETED":
                style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
                break;
            case "CANCELLED":
                style.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
                break;
            default:
                style.setFillForegroundColor(IndexedColors.WHITE.getIndex());
        }

        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
}
