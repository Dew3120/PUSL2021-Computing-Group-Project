package com.group100.wms.util;

import com.group100.wms.model.AttendanceRecord;
import com.group100.wms.model.Payroll;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

// OOP Concepts Used:
// Encapsulation - All Excel generation logic and styles are contained within this class
// Abstraction - Complex Excel creation is simplified through methods like export(), exportPayroll(), exportAttendance()
// Polymorphism - Methods behave differently depending on the data provided (generic, payroll, attendance)
// Inheritance - Uses Apache POI classes (Workbook, Sheet, Cell, etc.) which extend base library classes

public final class ExcelExporter {

    // Private constructor to prevent instantiation (utility class)
    private ExcelExporter() {}

    // RGB color for primary theme (dark blue)
    private static final byte[] COLOR_PRIMARY_RGB = {44, 62, 80};

    // RGB color for muted text (gray)
    private static final byte[] COLOR_MUTED_RGB   = {108, 117, 125};

    // RGB color for alternate row background
    private static final byte[] COLOR_ROW_ALT_RGB = {(byte)248, (byte)249, (byte)250};

    // Formatter for date/time display in reports
    private static final DateTimeFormatter DT_FMT =
            DateTimeFormatter.ofPattern("dd MMMM yyyy, hh:mm a");

    // ═══════════════════════════════════════════════════════════════
    // STYLE FACTORIES
    // ═══════════════════════════════════════════════════════════════

    // Creates style for company title
    private static CellStyle createCompanyStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        font.setFontName("Segoe UI");
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(new XSSFColor(COLOR_PRIMARY_RGB, null));
        style.setFont(font);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    // Creates style for report title
    private static CellStyle createTitleStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        font.setFontName("Segoe UI");
        font.setBold(true);
        font.setFontHeightInPoints((short) 13);
        font.setColor(new XSSFColor(COLOR_PRIMARY_RGB, null));
        style.setFont(font);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    // Creates style for subtitle (generated timestamp)
    private static CellStyle createSubtitleStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        font.setFontName("Segoe UI");
        font.setFontHeightInPoints((short) 9);
        font.setColor(new XSSFColor(COLOR_MUTED_RGB, null));
        style.setFont(font);
        return style;
    }

    // Creates style for table header cells
    private static CellStyle createHeaderStyle(XSSFWorkbook wb) {
        CellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        font.setFontName("Segoe UI");
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(new XSSFColor(COLOR_PRIMARY_RGB, null));
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }

    // Creates style for table data cells with optional alternate row color
    private static CellStyle createDataStyle(XSSFWorkbook wb, boolean altRow) {
        CellStyle style = wb.createCellStyle();
        XSSFFont font = wb.createFont();
        font.setFontName("Segoe UI");
        font.setFontHeightInPoints((short) 10);
        font.setColor(new XSSFColor(COLOR_PRIMARY_RGB, null));
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        if (altRow) {
            style.setFillForegroundColor(new XSSFColor(COLOR_ROW_ALT_RGB, null));
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        }
        return style;
    }

    // Creates style for currency values with formatting
    private static CellStyle createCurrencyStyle(XSSFWorkbook wb, boolean altRow) {
        CellStyle style = createDataStyle(wb, altRow);
        DataFormat format = wb.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0.00"));
        return style;
    }

    // ═══════════════════════════════════════════════════════════════
    // TITLE ROWS (shared)
    // ═══════════════════════════════════════════════════════════════

    // Adds company name, report title, and generated timestamp rows
    private static void addTitleRows(Sheet sheet, XSSFWorkbook wb, String reportTitle,
                                     int month, int year, int columnCount) {
        Row companyRow = sheet.createRow(0);
        companyRow.setHeightInPoints(28);
        Cell companyCell = companyRow.createCell(0);
        companyCell.setCellValue("Group 100 \u2014 Centralized Apparel WMS");
        companyCell.setCellStyle(createCompanyStyle(wb));
        if (columnCount > 1) {
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, columnCount - 1));
        }

        Row titleRow = sheet.createRow(1);
        titleRow.setHeightInPoints(22);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(reportTitle + " \u2014 " + getMonthName(month) + " " + year);
        titleCell.setCellStyle(createTitleStyle(wb));
        if (columnCount > 1) {
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, columnCount - 1));
        }

        Row subRow = sheet.createRow(2);
        Cell subCell = subRow.createCell(0);
        subCell.setCellValue("Generated: " + LocalDateTime.now().format(DT_FMT));
        subCell.setCellStyle(createSubtitleStyle(wb));
        if (columnCount > 1) {
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, columnCount - 1));
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // GENERIC EXPORT — used by all new controllers
    // ═══════════════════════════════════════════════════════════════

    // Exports generic tabular data into an Excel file
    public static void export(String sheetName, String[] headers, List<String[]> data,
                              javafx.stage.Window owner) {
        javafx.stage.FileChooser fc = new javafx.stage.FileChooser();
        fc.setTitle("Save Excel");
        fc.setInitialFileName(sheetName.replaceAll("\\s+", "_") + ".xlsx");
        fc.getExtensionFilters().add(
                new javafx.stage.FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        java.io.File file = fc.showSaveDialog(owner);
        if (file == null) return;

        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet(sheetName);

            // Row 0: Company title
            Row companyRow = sheet.createRow(0);
            companyRow.setHeightInPoints(28);
            Cell companyCell = companyRow.createCell(0);
            companyCell.setCellValue("Group 100 \u2014 Centralized Apparel WMS");
            companyCell.setCellStyle(createCompanyStyle(wb));
            if (headers.length > 1) {
                sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, headers.length - 1));
            }

            // Row 1: Report title
            Row titleRow = sheet.createRow(1);
            titleRow.setHeightInPoints(22);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue(sheetName);
            titleCell.setCellStyle(createTitleStyle(wb));
            if (headers.length > 1) {
                sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, headers.length - 1));
            }

            // Row 2: Generated timestamp
            Row subRow = sheet.createRow(2);
            Cell subCell = subRow.createCell(0);
            subCell.setCellValue("Generated: " + LocalDateTime.now().format(DT_FMT));
            subCell.setCellStyle(createSubtitleStyle(wb));
            if (headers.length > 1) {
                sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, headers.length - 1));
            }

            // Row 4: Headers
            CellStyle headerStyle = createHeaderStyle(wb);
            Row hRow = sheet.createRow(4);
            hRow.setHeightInPoints(24);
            for (int i = 0; i < headers.length; i++) {
                Cell c = hRow.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }

            // Data rows
            for (int r = 0; r < data.size(); r++) {
                boolean alt = r % 2 == 1;
                CellStyle dataStyle = createDataStyle(wb, alt);
                Row row = sheet.createRow(r + 5);
                String[] rowData = data.get(r);
                for (int c = 0; c < rowData.length; c++) {
                    Cell cell = row.createCell(c);
                    cell.setCellValue(rowData[c] != null ? rowData[c] : "");
                    cell.setCellStyle(dataStyle);
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                int w = sheet.getColumnWidth(i);
                sheet.setColumnWidth(i, Math.min(w + 600, 15000));
            }

            sheet.createFreezePane(0, 5);
            sheet.getPrintSetup().setLandscape(true);
            sheet.getPrintSetup().setFitWidth((short) 1);
            sheet.getPrintSetup().setFitHeight((short) 0);
            sheet.setAutobreaks(true);

            try (FileOutputStream fos = new FileOutputStream(file.getAbsolutePath())) {
                wb.write(fos);
            }
        } catch (Exception e) {
            System.err.println("[EXCEL] Export failed: " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // LEGACY: EXPORT PAYROLL
    // ═══════════════════════════════════════════════════════════════

    // Exports payroll data into an Excel report
    public static void exportPayroll(List<Payroll> payrolls, int month, int year,
                                     String outputPath) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Payroll " + month + "-" + year);

            String[] headers = {"ID", "Employee ID", "Month", "Year", "Base Salary",
                    "Overtime Pay", "EPF Employee", "EPF Employer", "ETF", "Net Salary"};

            addTitleRows(sheet, wb, "Payroll Report", month, year, headers.length);

            CellStyle headerStyle = createHeaderStyle(wb);
            Row headerRow = sheet.createRow(4);
            headerRow.setHeightInPoints(24);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 5;
            for (Payroll p : payrolls) {
                boolean alt = (rowNum - 5) % 2 == 1;
                CellStyle dataStyle = createDataStyle(wb, alt);
                CellStyle currStyle = createCurrencyStyle(wb, alt);
                Row row = sheet.createRow(rowNum++);

                Cell c0 = row.createCell(0); c0.setCellValue(p.getId()); c0.setCellStyle(dataStyle);
                Cell c1 = row.createCell(1); c1.setCellValue(p.getEmployeeId()); c1.setCellStyle(dataStyle);
                Cell c2 = row.createCell(2); c2.setCellValue(p.getMonth()); c2.setCellStyle(dataStyle);
                Cell c3 = row.createCell(3); c3.setCellValue(p.getYear()); c3.setCellStyle(dataStyle);
                Cell c4 = row.createCell(4); c4.setCellValue(p.getBaseSalary()); c4.setCellStyle(currStyle);
                Cell c5 = row.createCell(5); c5.setCellValue(p.getOvertimePay()); c5.setCellStyle(currStyle);
                Cell c6 = row.createCell(6); c6.setCellValue(p.getEpfEmployee()); c6.setCellStyle(currStyle);
                Cell c7 = row.createCell(7); c7.setCellValue(p.getEpfEmployer()); c7.setCellStyle(currStyle);
                Cell c8 = row.createCell(8); c8.setCellValue(p.getEtf()); c8.setCellStyle(currStyle);
                Cell c9 = row.createCell(9); c9.setCellValue(p.getNetSalary()); c9.setCellStyle(currStyle);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                int w = sheet.getColumnWidth(i);
                sheet.setColumnWidth(i, Math.min(w + 600, 15000));
            }

            sheet.createFreezePane(0, 5);
            sheet.getPrintSetup().setLandscape(true);
            sheet.getPrintSetup().setFitWidth((short) 1);
            sheet.getPrintSetup().setFitHeight((short) 0);
            sheet.setAutobreaks(true);

            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                wb.write(fos);
            }
        } catch (IOException e) {
            System.err.println("[EXCEL] Failed to export payroll: " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // LEGACY: EXPORT ATTENDANCE
    // ═══════════════════════════════════════════════════════════════

    // Exports attendance records into an Excel report
    public static void exportAttendance(List<AttendanceRecord> records, int month, int year,
                                        String outputPath) {
        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Attendance " + month + "-" + year);

            String[] headers = {"ID", "Employee ID", "Date", "Clock In",
                    "Clock Out", "Status", "Overtime Hours"};

            addTitleRows(sheet, wb, "Attendance Report", month, year, headers.length);

            CellStyle headerStyle = createHeaderStyle(wb);
            Row headerRow = sheet.createRow(4);
            headerRow.setHeightInPoints(24);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowNum = 5;
            for (AttendanceRecord r : records) {
                boolean alt = (rowNum - 5) % 2 == 1;
                CellStyle dataStyle = createDataStyle(wb, alt);
                Row row = sheet.createRow(rowNum++);

                Cell c0 = row.createCell(0); c0.setCellValue(r.getId()); c0.setCellStyle(dataStyle);
                Cell c1 = row.createCell(1); c1.setCellValue(r.getEmployeeId()); c1.setCellStyle(dataStyle);
                Cell c2 = row.createCell(2); c2.setCellValue(r.getDate() != null ? r.getDate().toString() : ""); c2.setCellStyle(dataStyle);
                Cell c3 = row.createCell(3); c3.setCellValue(r.getClockIn() != null ? r.getClockIn().toString() : ""); c3.setCellStyle(dataStyle);
                Cell c4 = row.createCell(4); c4.setCellValue(r.getClockOut() != null ? r.getClockOut().toString() : ""); c4.setCellStyle(dataStyle);
                Cell c5 = row.createCell(5); c5.setCellValue(r.getStatus()); c5.setCellStyle(dataStyle);
                Cell c6 = row.createCell(6); c6.setCellValue(r.getOvertimeHours()); c6.setCellStyle(dataStyle);
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                int w = sheet.getColumnWidth(i);
                sheet.setColumnWidth(i, Math.min(w + 600, 15000));
            }

            sheet.createFreezePane(0, 5);
            sheet.getPrintSetup().setLandscape(false);
            sheet.getPrintSetup().setFitWidth((short) 1);
            sheet.getPrintSetup().setFitHeight((short) 0);
            sheet.setAutobreaks(true);

            try (FileOutputStream fos = new FileOutputStream(outputPath)) {
                wb.write(fos);
            }
        } catch (IOException e) {
            System.err.println("[EXCEL] Failed to export attendance: " + e.getMessage());
        }
    }

    // ═══════════════════════════════════════════════════════════════
    // UTILITY
    // ═══════════════════════════════════════════════════════════════

    // Returns the month name based on integer value (1–12)
    private static String getMonthName(int month) {
        String[] months = {"", "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"};
        return (month >= 1 && month <= 12) ? months[month] : String.valueOf(month);
    }
}
