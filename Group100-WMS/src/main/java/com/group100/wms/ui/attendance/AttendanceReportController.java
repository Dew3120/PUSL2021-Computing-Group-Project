// =============================================================================
// AttendanceReportController.java
// Part of: Centralized Apparel Warehouse Management System (WMS)
// Module: Attendance UI — Report Export View
//
// OOP CONCEPTS USED IN THIS CLASS:
// - ENCAPSULATION: All @FXML fields are private, and reportService is a
//   private final field. The file chooser logic is encapsulated in the
//   private chooseFile() helper method, keeping both export handlers clean
//   and preventing direct access to file selection internals from outside.
// - ABSTRACTION: ReportService abstracts all report generation logic (data
//   querying, formatting, and file writing) behind two simple method calls.
//   FileChooser abstracts the OS-level save dialog. This controller only
//   decides which format to export and where to show the result.
// - POLYMORPHISM: Both handleExportPdf() and handleExportExcel() call the
//   shared chooseFile() helper with different arguments, producing different
//   behaviour from the same method — a simple form of parametric polymorphism.
// - INHERITANCE: This controller follows the JavaFX controller pattern where
//   the @FXML annotation-based injection framework implicitly governs the
//   lifecycle contract this class must honour (initialize() called post-inject).
// =============================================================================

package com.group100.wms.ui.attendance;

import com.group100.wms.exception.DatabaseException;
import com.group100.wms.repository.AttendanceRepository;
import com.group100.wms.repository.PayrollRepository;
import com.group100.wms.service.ReportService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.time.LocalDate;

public class AttendanceReportController {

    // Dropdown for selecting the month of the attendance report (1–12)
    @FXML private ComboBox<Integer> monthCombo;

    // Dropdown for selecting the year of the attendance report
    @FXML private ComboBox<Integer> yearCombo;

    // Label used to display export success messages or error feedback to the user
    @FXML private Label statusLabel;

    // Service layer object responsible for generating and exporting attendance reports.
    // Constructed with both PayrollRepository and AttendanceRepository as dependencies.
    private final ReportService reportService =
            new ReportService(new PayrollRepository(), new AttendanceRepository());

    // Called automatically by JavaFX after all @FXML fields are injected.
    // Populates the month and year dropdowns and defaults both to the current date.
    @FXML
    public void initialize() {
        monthCombo.getItems().addAll(1,2,3,4,5,6,7,8,9,10,11,12);
        yearCombo.getItems().addAll(2023, 2024, 2025, 2026);
        LocalDate now = LocalDate.now();
        monthCombo.setValue(now.getMonthValue());
        yearCombo.setValue(now.getYear());
    }

    // Triggered when the user clicks the "Export PDF" button.
    // Opens a save dialog filtered to .pdf files. If the user selects a location,
    // delegates to ReportService to generate and write the PDF attendance report.
    // Updates the status label with the file name on success, or an error message on failure.
    @FXML
    private void handleExportPdf() {
        File file = chooseFile("PDF Files", "*.pdf");
        if (file == null) return;
        try {
            reportService.exportAttendancePdf(
                    monthCombo.getValue(), yearCombo.getValue(), file.getAbsolutePath());
            statusLabel.setText("PDF exported: " + file.getName());
        } catch (DatabaseException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    // Triggered when the user clicks the "Export Excel" button.
    // Opens a save dialog filtered to .xlsx files. If the user selects a location,
    // delegates to ReportService to generate and write the Excel attendance report.
    // Updates the status label with the file name on success, or an error message on failure.
    @FXML
    private void handleExportExcel() {
        File file = chooseFile("Excel Files", "*.xlsx");
        if (file == null) return;
        try {
            reportService.exportAttendanceExcel(
                    monthCombo.getValue(), yearCombo.getValue(), file.getAbsolutePath());
            statusLabel.setText("Excel exported: " + file.getName());
        } catch (DatabaseException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    // Shared helper that opens an OS-level save dialog with a specific file type filter.
    // Returns the File chosen by the user, or null if the dialog was cancelled.
    // Used by both handleExportPdf() and handleExportExcel() to avoid duplicating
    // file chooser setup logic.
    private File chooseFile(String description, String extension) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(description, extension));
        return chooser.showSaveDialog(statusLabel.getScene().getWindow());
    }
}