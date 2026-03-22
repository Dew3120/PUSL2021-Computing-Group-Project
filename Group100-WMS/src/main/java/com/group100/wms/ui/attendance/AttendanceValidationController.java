// =============================================================================
// AttendanceValidationController.java
// Part of: Centralized Apparel Warehouse Management System (WMS)
// Module: Attendance UI — Validation & Approval View
//
// OOP CONCEPTS USED IN THIS CLASS:
// - ENCAPSULATION: All @FXML fields are private, and attendanceService is a
//   private final field. Status update logic is encapsulated in the private
//   updateSelectedStatus() method, which the three public action handlers
//   delegate to — hiding the implementation details behind clean method calls.
// - ABSTRACTION: AttendanceService abstracts all business logic for fetching
//   and updating records. AttendanceRepository abstracts direct database
//   access. SessionManager abstracts user session state. This controller
//   only concerns itself with UI wiring and user interactions.
// - INHERITANCE: TableRow is anonymously subclassed inside setRowFactory()
//   to override updateItem(), applying colour-coded row backgrounds based
//   on each record's attendance status (ABSENT = red, HALF_DAY = yellow).
// - POLYMORPHISM: The overridden updateItem() inside the anonymous TableRow
//   subclass demonstrates runtime polymorphism — JavaFX calls the correct
//   overridden version at runtime when rendering each row.
// =============================================================================

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

    // Main table displaying attendance records for validation and approval
    @FXML private TableView<AttendanceRecord> validationTable;

    // Column for the unique attendance record ID
    @FXML private TableColumn<AttendanceRecord, Integer> colId;

    // Column for the employee's ID number
    @FXML private TableColumn<AttendanceRecord, Integer> colEmployeeId;

    // Column for the employee's full name
    @FXML private TableColumn<AttendanceRecord, String> colName;

    // Column for the attendance date
    @FXML private TableColumn<AttendanceRecord, String> colDate;

    // Column for the clock-in time (empty string if not recorded)
    @FXML private TableColumn<AttendanceRecord, String> colCheckIn;

    // Column for the clock-out time (empty string if not recorded)
    @FXML private TableColumn<AttendanceRecord, String> colCheckOut;

    // Column showing total hours worked, formatted to two decimal places
    @FXML private TableColumn<AttendanceRecord, String> colHours;

    // Column showing the current attendance status (PRESENT, ABSENT, HALF_DAY)
    @FXML private TableColumn<AttendanceRecord, String> colStatus;

    // Dropdown for selecting the month to filter/load records (1–12)
    @FXML private ComboBox<Integer> monthCombo;

    // Dropdown for selecting the year to filter/load records
    @FXML private ComboBox<Integer> yearCombo;

    // Label used to display feedback messages for load, update, and error events
    @FXML private Label statusLabel;

    // Service layer object responsible for all attendance business logic.
    // Constructed with an AttendanceRepository to handle database operations.
    private final AttendanceService attendanceService =
            new AttendanceService(new AttendanceRepository());

    // Called automatically by JavaFX after all @FXML fields are injected.
    // Binds each table column to its corresponding AttendanceRecord getter,
    // sets up colour-coded row highlighting by status, populates the month/year
    // dropdowns with the current date as default, then loads records.
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

    // Fetches attendance records for the currently selected month and year
    // from the service layer and populates the validation table.
    // Updates the status label with the record count, or an error message on failure.
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

    // Triggered when the user clicks the "Approve as Present" button.
    // Delegates to updateSelectedStatus() to set the selected record's status to PRESENT.
    @FXML
    private void handleApprovePresent() {
        updateSelectedStatus("PRESENT");
    }

    // Triggered when the user clicks the "Approve as Half Day" button.
    // Delegates to updateSelectedStatus() to set the selected record's status to HALF_DAY.
    @FXML
    private void handleApproveHalfDay() {
        updateSelectedStatus("HALF_DAY");
    }

    // Triggered when the user clicks the "Mark as Absent" button.
    // Delegates to updateSelectedStatus() to set the selected record's status to ABSENT.
    @FXML
    private void handleMarkAbsent() {
        updateSelectedStatus("ABSENT");
    }

    // Core status update method shared by all three approval action handlers.
    // Retrieves the currently selected record, applies the new status and the
    // current user's ID as the approver, then persists the change via the service layer.
    // Refreshes the table after a successful update, or shows an error on failure.
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

    // Triggered when the user clicks the "Filter" button.
    // Reloads records from the database using the currently selected month and year.
    @FXML
    private void handleFilter() { loadRecords(); }

    // Triggered when the user clicks the "Refresh" button.
    // Reloads the latest attendance records for the selected month and year.
    @FXML
    private void handleRefresh() { loadRecords(); }
}