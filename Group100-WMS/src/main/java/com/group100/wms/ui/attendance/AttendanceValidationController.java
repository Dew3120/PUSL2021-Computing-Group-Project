package com.group100.wms.ui.attendance;

import com.group100.wms.core.SessionManager;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.AttendanceRecord;
import com.group100.wms.repository.AttendanceRepository;
import com.group100.wms.service.AttendanceService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;

public class AttendanceValidationController {

    @FXML private TableView<AttendanceRecord> validationTable;
    @FXML private TableColumn<AttendanceRecord, Integer> colId;
    @FXML private TableColumn<AttendanceRecord, Integer> colEmployeeId;
    @FXML private TableColumn<AttendanceRecord, String> colName;
    @FXML private TableColumn<AttendanceRecord, String> colDate;
    @FXML private TableColumn<AttendanceRecord, String> colCheckIn;
    @FXML private TableColumn<AttendanceRecord, String> colCheckOut;
    @FXML private TableColumn<AttendanceRecord, String> colHours;
    @FXML private TableColumn<AttendanceRecord, String> colStatus;
    @FXML private ComboBox<Integer> monthCombo;
    @FXML private ComboBox<Integer> yearCombo;
    @FXML private Label statusLabel;

    private final AttendanceService attendanceService =
            new AttendanceService(new AttendanceRepository());

    @FXML
    public void initialize() {
        colId.setCellValueFactory(d ->
                new SimpleIntegerProperty(d.getValue().getId()).asObject());
        colEmployeeId.setCellValueFactory(d ->
                new SimpleIntegerProperty(d.getValue().getEmployeeId()).asObject());
        colName.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getEmployeeName() != null
                                ? d.getValue().getEmployeeName() : "—"));
        colDate.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getDate() != null
                                ? d.getValue().getDate().toString() : ""));
        colCheckIn.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getClockIn() != null
                                ? d.getValue().getClockIn().toString() : ""));
        colCheckOut.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getClockOut() != null
                                ? d.getValue().getClockOut().toString() : ""));
        colHours.setCellValueFactory(d ->
                new SimpleStringProperty(
                        String.format("%.2f", d.getValue().getHoursWorked())));
        colStatus.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getStatus()));

        // Highlight rows by status
        validationTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(AttendanceRecord item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else {
                    switch (item.getStatus()) {
                        case "ABSENT" -> setStyle("-fx-background-color: #ffcccc;");
                        case "HALF_DAY" -> setStyle("-fx-background-color: #fff3cd;");
                        default -> setStyle("");
                    }
                }
            }
        });

        monthCombo.setItems(FXCollections.observableArrayList(
                1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
        yearCombo.setItems(FXCollections.observableArrayList(
                2023, 2024, 2025, 2026));

        LocalDate now = LocalDate.now();
        monthCombo.setValue(now.getMonthValue());
        yearCombo.setValue(now.getYear());
        loadRecords();
    }

    private void loadRecords() {
        try {
            int month = monthCombo.getValue();
            int year = yearCombo.getValue();
            List<AttendanceRecord> records =
                    attendanceService.getAttendanceByMonthYear(month, year);
            validationTable.setItems(FXCollections.observableArrayList(records));
            statusLabel.setText("Loaded " + records.size() + " records.");
        } catch (DatabaseException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleApprovePresent() {
        updateSelectedStatus("PRESENT");
    }

    @FXML
    private void handleApproveHalfDay() {
        updateSelectedStatus("HALF_DAY");
    }

    @FXML
    private void handleMarkAbsent() {
        updateSelectedStatus("ABSENT");
    }

    private void updateSelectedStatus(String newStatus) {
        AttendanceRecord selected = validationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            statusLabel.setText("Select a record first.");
            return;
        }
        try {
            selected.setStatus(newStatus);
            selected.setApprovedBy(SessionManager.getCurrentUser().getId());
            attendanceService.updateAttendance(selected);
            statusLabel.setText("Record #" + selected.getId() + " updated to " + newStatus
                    + " (Employee: " + selected.getEmployeeName() + ")");
            loadRecords();
        } catch (DatabaseException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleFilter() { loadRecords(); }

    @FXML
    private void handleRefresh() { loadRecords(); }
}