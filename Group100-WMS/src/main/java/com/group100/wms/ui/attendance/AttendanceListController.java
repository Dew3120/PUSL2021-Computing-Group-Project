// =============================================================================
// AttendanceListController.java
// Part of: Centralized Apparel Warehouse Management System (WMS)
// Module: Attendance UI — List & Submission View
//
// OOP CONCEPTS USED IN THIS CLASS:
// - ENCAPSULATION: All @FXML fields are private. Internal state (allRows,
//   DT_FMT) is private to this class. AttRow and SubRow bundle related fields
//   into cohesive data objects, hiding raw SQL result data behind clean fields.
// - ABSTRACTION: DatabaseConnection abstracts JDBC setup. PdfExporter and
//   ExcelExporter abstract all file-writing logic. This controller only deals
//   with what data to show and when — not how it is stored or exported.
// - INHERITANCE: TableCell is anonymously subclassed inside setCellFactory()
//   for both colStatus and colSubType, overriding updateItem() to apply custom
//   color styling. TableRow is anonymously subclassed inside setRowFactory()
//   to apply per-row background colors for Daily Submission rows.
// - POLYMORPHISM: updateItem() is overridden in multiple anonymous TableCell
//   and TableRow subclasses. JavaFX calls the correct overridden version at
//   runtime depending on which cell/row is being rendered — runtime polymorphism.
// =============================================================================

package com.group100.wms.ui.attendance;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.util.PdfExporter;
import com.group100.wms.util.ExcelExporter;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AttendanceListController {

    // KPI summary labels showing today's total, present, absent, and half-day counts
    @FXML private Label lblTotal, lblPresent, lblAbsent, lblHalf;

    // Dropdowns for filtering attendance by warehouse section and attendance status
    @FXML private ComboBox<String> cmbSection, cmbStatus;

    // Date pickers for selecting a custom date range filter (from/to)
    @FXML private DatePicker dpFrom, dpTo;

    // Main attendance table displaying AttRow records
    @FXML private TableView<AttRow> attTable;

    // Numeric column for employee ID
    @FXML private TableColumn<AttRow, Number> colEmpId;

    // String columns for employee name, section, date, clock-in/out times, and status
    @FXML private TableColumn<AttRow, String> colName, colSection, colDate, colClockIn, colClockOut, colStatus;

    // Secondary table showing recent attendance submission audit log entries
    @FXML private TableView<SubRow> tblSubmissions;

    // Columns for the submissions table: log ID, submission type, summary details, and timestamp
    @FXML private TableColumn<SubRow, String> colSubId, colSubType, colSubDetails, colSubTime;

    // Master list of all attendance rows loaded from the database for today.
    // Kept in memory so in-memory filters can run without re-querying the DB.
    private ObservableList<AttRow> allRows;

    // Formatter used to display audit log timestamps in a human-readable format (e.g. "Jan 05, 2025 08:30")
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

    // Called automatically by JavaFX after all @FXML fields are injected.
    // Wires up both tables, initialises filter dropdowns to their default values,
    // then triggers the initial data load for attendance records and submissions.
    @FXML
    public void initialize() {
        setupAttTable();
        setupSubmissionsTable();
        cmbSection.setItems(FXCollections.observableArrayList("All","WMS-1","WMS-2","WMS-3","WMS-4"));
        cmbSection.setValue("All");
        cmbStatus.setItems(FXCollections.observableArrayList("All","PRESENT","ABSENT","HALF_DAY"));
        cmbStatus.setValue("All");
        loadData();
        loadSubmissions();
    }

    // Binds each column of the attendance table to its corresponding AttRow field.
    // Also sets a custom cell factory on colStatus to colour-code each status value:
    // green for PRESENT, red for ABSENT, and orange for HALF_DAY.
    private void setupAttTable() {
        colEmpId.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().empId));
        colName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().empName));
        colSection.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().section));
        colDate.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().date));
        colClockIn.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().clockIn != null ? cd.getValue().clockIn : "-"));
        colClockOut.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().clockOut != null ? cd.getValue().clockOut : "-"));
        colStatus.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().status));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle(switch (item) {
                    case "PRESENT"  -> "-fx-text-fill:#27ae60;-fx-font-weight:bold;";
                    case "ABSENT"   -> "-fx-text-fill:#e74c3c;-fx-font-weight:bold;";
                    case "HALF_DAY" -> "-fx-text-fill:#f39c12;-fx-font-weight:bold;";
                    default -> "";
                });
            }
        });
    }

    // Binds each column of the submissions table to its corresponding SubRow field.
    // Applies a purple colour for "Daily Submission" type and green for monthly.
    // Rows for daily submissions get a light purple background via the row factory.
    // Double-clicking a row opens the full submission detail dialog.
    private void setupSubmissionsTable() {
        colSubId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().id)));
        colSubType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().type));
        colSubDetails.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().details));
        colSubTime.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().submittedAt));

        colSubType.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle(item.contains("Daily")
                        ? "-fx-text-fill:#8e44ad;-fx-font-weight:bold;"
                        : "-fx-text-fill:#27ae60;-fx-font-weight:bold;");
            }
        });

        tblSubmissions.setRowFactory(tv -> {
            TableRow<SubRow> row = new TableRow<>();
            row.itemProperty().addListener((obs, old, sub) -> {
                if (sub != null && sub.type.contains("Daily"))
                    row.setStyle("-fx-background-color:#f9f0ff;");
                else
                    row.setStyle("");
            });
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) showSubmissionDetail(row.getItem());
            });
            return row;
        });
    }

    // Queries today's attendance records from the database (limited to 5000 rows),
    // populates allRows and the table, then refreshes the KPI summary labels.
    private void loadData() {
        allRows = FXCollections.observableArrayList();
        String sql = "SELECT a.*, e.full_name, e.section FROM attendance_records a " +
                "JOIN employees e ON a.employee_id = e.employee_id " +
                "WHERE a.date = CURDATE() " +
                "ORDER BY e.section ASC, e.employee_id ASC LIMIT 5000";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                AttRow r = new AttRow();
                r.empId   = rs.getInt("employee_id");
                r.empName = rs.getString("full_name");
                r.section = rs.getString("section");
                r.date    = rs.getString("date");
                r.clockIn  = rs.getString("clock_in");
                r.clockOut = rs.getString("clock_out");
                r.status  = rs.getString("status");
                allRows.add(r);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        attTable.setItems(allRows);
        updateKPIs(allRows);
    }

    // Loads the most recent 200 attendance submission audit log entries on a
    // background thread to avoid blocking the UI. Parses the raw details string
    // into a human-readable summary and updates the submissions table on the
    // JavaFX Application Thread via Platform.runLater().
    @FXML
    public void loadSubmissions() {
        new Thread(() -> {
            List<SubRow> rows = new ArrayList<>();
            String sql = """
                    SELECT al.log_id, al.action, al.details, al.created_at, u.username
                    FROM audit_logs al
                    JOIN users u ON al.user_id = u.user_id
                    WHERE al.action IN ('ATTENDANCE_SUBMISSION','DAILY_ATTENDANCE_SUBMISSION')
                    ORDER BY al.created_at DESC
                    LIMIT 200
                    """;
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SubRow r = new SubRow();
                    r.id         = rs.getInt("log_id");
                    r.username   = rs.getString("username");
                    r.type       = rs.getString("action").equals("DAILY_ATTENDANCE_SUBMISSION")
                            ? "Daily Submission" : "Monthly Submission";
                    r.rawDetails = rs.getString("details");
                    // Show short summary in table
                    String raw   = r.rawDetails;
                    if (raw.contains("DATE=")) {
                        String date    = raw.contains("DATE=")    ? raw.replaceAll(".*DATE=([^|]+).*","$1")    : "-";
                        String total   = raw.contains("TOTAL=")   ? raw.replaceAll(".*TOTAL=([^|]+).*","$1")   : "-";
                        String present = raw.contains("PRESENT=") ? raw.replaceAll(".*PRESENT=([^|]+).*","$1") : "-";
                        String absent  = raw.contains("ABSENT=")  ? raw.replaceAll(".*ABSENT=([^|]+).*","$1")  : "-";
                        String half    = raw.contains("HALFDAY=") ? raw.replaceAll(".*HALFDAY=([^|]+).*","$1") : "-";
                        String section = raw.contains("SECTION=") ? raw.replaceAll(".*SECTION=([^|]+).*","$1") : "-";
                        r.details = "[" + r.username + "] Date: " + date + " | Section: " + section +
                                " | Total: " + total + " | Present: " + present +
                                " | Absent: " + absent + " | Half Day: " + half +
                                "  (double-click for full list)";
                    } else {
                        r.details = "[" + r.username + "] " + raw;
                    }
                    Timestamp ts  = rs.getTimestamp("created_at");
                    r.submittedAt = ts != null ? ts.toLocalDateTime().format(DT_FMT) : "-";
                    rows.add(r);
                }
            } catch (SQLException e) { e.printStackTrace(); }
            Platform.runLater(() -> tblSubmissions.setItems(FXCollections.observableArrayList(rows)));
        }).start();
    }

    // Recalculates and updates the four KPI labels based on a given list of rows.
    // Counts total records and breaks down by PRESENT, ABSENT, and HALF_DAY status.
    private void updateKPIs(List<AttRow> rows) {
        lblTotal.setText(String.valueOf(rows.size()));
        lblPresent.setText(String.valueOf(rows.stream().filter(r -> "PRESENT".equals(r.status)).count()));
        lblAbsent.setText(String.valueOf(rows.stream().filter(r -> "ABSENT".equals(r.status)).count()));
        lblHalf.setText(String.valueOf(rows.stream().filter(r -> "HALF_DAY".equals(r.status)).count()));
    }

    // Triggered when the user clicks the "Filter" button.
    // If a date range is selected, delegates to loadFilteredFromDB() for a fresh DB query.
    // Otherwise, applies section and status filters to allRows in memory using streams.
    @FXML private void handleFilter() {
        String sec  = cmbSection.getValue();
        String st   = cmbStatus.getValue();
        LocalDate from = dpFrom.getValue();
        LocalDate to   = dpTo.getValue();
        if (from != null || to != null) {
            loadFilteredFromDB(sec, st, from, to);
        } else {
            List<AttRow> f = allRows.stream()
                    .filter(r -> "All".equals(sec) || sec.equals(r.section))
                    .filter(r -> "All".equals(st)  || st.equals(r.status))
                    .collect(Collectors.toList());
            attTable.setItems(FXCollections.observableArrayList(f));
            updateKPIs(f);
        }
    }

    // Executes a dynamic SQL query against the database to fetch attendance records
    // matching the supplied date range, section, and status filters.
    // Builds the WHERE clause conditionally based on which filters are active,
    // then updates the table and KPI labels with the returned results (up to 10,000 rows).
    private void loadFilteredFromDB(String sec, String st, LocalDate from, LocalDate to) {
        ObservableList<AttRow> rows = FXCollections.observableArrayList();
        StringBuilder sql = new StringBuilder(
                "SELECT a.*, e.full_name, e.section FROM attendance_records a " +
                        "JOIN employees e ON a.employee_id = e.employee_id WHERE 1=1 ");
        if (from != null) sql.append("AND a.date >= '").append(from).append("' ");
        if (to   != null) sql.append("AND a.date <= '").append(to).append("' ");
        if (!"All".equals(sec)) sql.append("AND e.section = '").append(sec).append("' ");
        if (!"All".equals(st))  sql.append("AND a.status = '").append(st).append("' ");
        sql.append("ORDER BY a.date DESC, e.section, e.employee_id LIMIT 10000");
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString());
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                AttRow r = new AttRow();
                r.empId   = rs.getInt("employee_id");
                r.empName = rs.getString("full_name");
                r.section = rs.getString("section");
                r.date    = rs.getString("date");
                r.clockIn  = rs.getString("clock_in");
                r.clockOut = rs.getString("clock_out");
                r.status  = rs.getString("status");
                rows.add(r);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        attTable.setItems(rows);
        updateKPIs(rows);
    }

    // Triggered when the user clicks the "Reset" button.
    // Clears all filter controls back to their defaults and restores
    // the full unfiltered attendance dataset in the table.
    @FXML private void handleReset() {
        cmbSection.setValue("All");
        cmbStatus.setValue("All");
        dpFrom.setValue(null);
        dpTo.setValue(null);
        attTable.setItems(allRows);
        updateKPIs(allRows);
    }

    // Triggered when the user clicks "Export PDF".
    // Converts the currently visible attendance table rows into a string array list
    // and delegates to PdfExporter to generate and save the PDF file.
    @FXML private void handleExportPdf() {
        String[] h = {"Emp#","Name","Section","Date","Clock In","Clock Out","Status"};
        List<String[]> d = new ArrayList<>();
        for (AttRow r : attTable.getItems())
            d.add(new String[]{String.valueOf(r.empId),r.empName,r.section,r.date,
                    r.clockIn!=null?r.clockIn:"-", r.clockOut!=null?r.clockOut:"-", r.status});
        PdfExporter.export("Attendance Report", h, d, attTable.getScene().getWindow());
    }

    // Triggered when the user clicks "Export Excel".
    // Converts the currently visible attendance table rows into a string array list
    // and delegates to ExcelExporter to generate and save the Excel file.
    @FXML private void handleExportExcel() {
        String[] h = {"Emp#","Name","Section","Date","Clock In","Clock Out","Status"};
        List<String[]> d = new ArrayList<>();
        for (AttRow r : attTable.getItems())
            d.add(new String[]{String.valueOf(r.empId),r.empName,r.section,r.date,
                    r.clockIn!=null?r.clockIn:"-", r.clockOut!=null?r.clockOut:"-", r.status});
        ExcelExporter.export("Attendance", h, d, attTable.getScene().getWindow());
    }

    // Opens a modal dialog showing the full parsed details of a submission audit log entry.
    // Parses the pipe-delimited raw details string into labelled fields and, where present,
    // formats the individual employee RECORDS= entries into an aligned monospace list.
    private void showSubmissionDetail(SubRow sub) {
        // Parse the details string
        String raw = sub.rawDetails;
        StringBuilder sb = new StringBuilder();
        sb.append("Submission: ").append(sub.type).append("\n");
        sb.append("Submitted: ").append(sub.submittedAt).append("\n");
        sb.append("By: ").append(sub.username).append("\n\n");

        if (raw.contains("DATE=")) {
            String[] parts = raw.split("\\|");
            for (String p : parts) {
                if (p.startsWith("DATE="))    sb.append("Date:      ").append(p.replace("DATE=","")).append("\n");
                if (p.startsWith("TOTAL="))   sb.append("Total:     ").append(p.replace("TOTAL=","")).append("\n");
                if (p.startsWith("PRESENT=")) sb.append("Present:   ").append(p.replace("PRESENT=","")).append("\n");
                if (p.startsWith("ABSENT="))  sb.append("Absent:    ").append(p.replace("ABSENT=","")).append("\n");
                if (p.startsWith("HALFDAY=")) sb.append("Half Day:  ").append(p.replace("HALFDAY=","")).append("\n");
                if (p.startsWith("SECTION=")) sb.append("Section:   ").append(p.replace("SECTION=","")).append("\n");
                if (p.startsWith("RECORDS=")) {
                    sb.append("\nEmployee Records:\n");
                    sb.append("-".repeat(40)).append("\n");
                    String records = p.replace("RECORDS=","");
                    for (String rec : records.split(",")) {
                        if (rec.isBlank()) continue;
                        String[] parts2 = rec.split(":");
                        if (parts2.length >= 3) {
                            sb.append(String.format("  %-4s %-30s %s\n", parts2[0], parts2[1], parts2[2]));
                        }
                    }
                }
            }
        } else {
            sb.append(raw);
        }

        TextArea ta = new TextArea(sb.toString());
        ta.setEditable(false);
        ta.setWrapText(true);
        ta.setPrefHeight(500);
        ta.setPrefWidth(500);
        ta.setStyle("-fx-font-family: monospace; -fx-font-size: 12px;");

        javafx.scene.control.Dialog<Void> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Submission Detail — " + sub.submittedAt);
        dialog.setHeaderText(sub.type + " from " + sub.username);
        dialog.getDialogPane().setContent(ta);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.showAndWait();
    }

    // Inner data model class representing a single row in the attendance table.
    // Groups all fields for one employee's attendance record on a given date.
    // clockIn and clockOut may be null if the employee did not clock in/out.
    public static class AttRow {
        // Unique identifier for the employee
        public int empId;
        // Employee's full name, assigned section, attendance date,
        // clock-in time, clock-out time, and attendance status (PRESENT/ABSENT/HALF_DAY)
        public String empName, section, date, clockIn, clockOut, status;
    }

    // Inner data model class representing a single row in the submissions audit log table.
    // Stores both a short summary (details) for table display and the full raw string
    // (rawDetails) for the detail dialog, along with metadata about who submitted and when.
    public static class SubRow {
        // Unique audit log ID for this submission entry
        public int id;
        // Submission type label ("Daily Submission" or "Monthly Submission"),
        // formatted summary string for table display, raw unparsed details string,
        // formatted timestamp of submission, and the username of the submitter
        public String type, details, rawDetails, submittedAt, username;
    }
}