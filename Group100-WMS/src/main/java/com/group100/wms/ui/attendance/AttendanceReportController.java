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

    @FXML private ComboBox<Integer> monthCombo;
    @FXML private ComboBox<Integer> yearCombo;
    @FXML private Label statusLabel;

    private final ReportService reportService =
            new ReportService(new PayrollRepository(), new AttendanceRepository());

    @FXML
    public void initialize() {
        monthCombo.getItems().addAll(1,2,3,4,5,6,7,8,9,10,11,12);
        yearCombo.getItems().addAll(2023, 2024, 2025, 2026);
        LocalDate now = LocalDate.now();
        monthCombo.setValue(now.getMonthValue());
        yearCombo.setValue(now.getYear());
    }

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

    private File chooseFile(String description, String extension) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(description, extension));
        return chooser.showSaveDialog(statusLabel.getScene().getWindow());
    }
}