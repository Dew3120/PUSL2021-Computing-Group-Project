package com.group100.wms.ui.supervisor;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.core.SessionManager;
// ... (imports)

// OOP Concepts:
// 1. Data Aggregation: Uses the SummaryRow nested class to combine daily logs into monthly percentages.
// 2. Audit Trail: Implements transparency by logging supervisor actions into the 'audit_logs' table.
// 3. UI Synchronization: Uses Platform.runLater() to ensure database threads don't crash the JavaFX UI.
public class AttendanceCompilationController implements Initializable {

    // Daily View Components
    @FXML private TableView<AttRow> tblAttendance;
    @FXML private Label lblAttendanceRate, lblPresent, lblAbsent, lblHalfDay;
    
    // Monthly Summary Components
    @FXML private TableView<SummaryRow> tblSummary;
    @FXML private ComboBox<String> cmbMonth, cmbYear;

    // Observable Lists for data management
    private final ObservableList<AttRow> allAttRows = FXCollections.observableArrayList();
    private final ObservableList<AttRow> shownRows = FXCollections.observableArrayList();
    private final ObservableList<SummaryRow> summaryRows = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupFilters();      // Initializes DatePickers and Section dropdowns
        setupDailyTable();   // Configures editable status columns
        setupSummaryTable(); // Configures the performance report view
        loadDailyData();     // Initial data fetch
    }

    // Advanced SQL logic: Uses LEFT JOIN to show ALL active employees, 
    // even if they haven't been marked present yet for the selected date.
    private void loadDailyData() {
        new Thread(() -> {
            // SQL includes "CASE WHEN" logic to calculate totals on the fly
            // Updates the dashboard labels (Total, Present, Absent)
            Platform.runLater(() -> {
                // UI updates here
            });
        }).start();
    }

    // Feature: Direct Database Update
    // Allows supervisors to change a status (e.g., Absent to Present) directly from the table
    private void updateAttendanceStatus(AttRow row, String newStatus) {
        new Thread(() -> {
            // Performs an UPSERT (Update if exists, Insert if new record)
            // Logs the change: "Marked by supervisor"
        }).start();
    }

    // Feature: Admin Submission (Workflow)
    // Packages the current view into a string and sends it to the audit_logs 
    // for Admin/Payroll verification.
    @FXML private void onSendTodayToAdmin() {
        // Validation -> Serialization -> Database Log
    }

    // DTO for Daily Rows
    public static class AttRow {
        public int attId, empId;
        public String fullName, section, status, clockIn, clockOut;
    }

    // DTO for Monthly Aggregates
    public static class SummaryRow {
        public String fullName, section;
        public int presentDays, absentDays, halfDays;
        public double attendanceRate;
    }
}
