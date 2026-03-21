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

    @FXML private Label lblTotal, lblPresent, lblAbsent, lblHalf;
    @FXML private ComboBox<String> cmbSection, cmbStatus;
    @FXML private DatePicker dpFrom, dpTo;
    @FXML private TableView<AttRow> attTable;
    @FXML private TableColumn<AttRow, Number> colEmpId;
    @FXML private TableColumn<AttRow, String> colName, colSection, colDate, colClockIn, colClockOut, colStatus;

    @FXML private TableView<SubRow> tblSubmissions;
    @FXML private TableColumn<SubRow, String> colSubId, colSubType, colSubDetails, colSubTime;

    private ObservableList<AttRow> allRows;
    private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");

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

    private void updateKPIs(List<AttRow> rows) {
        lblTotal.setText(String.valueOf(rows.size()));
        lblPresent.setText(String.valueOf(rows.stream().filter(r -> "PRESENT".equals(r.status)).count()));
        lblAbsent.setText(String.valueOf(rows.stream().filter(r -> "ABSENT".equals(r.status)).count()));
        lblHalf.setText(String.valueOf(rows.stream().filter(r -> "HALF_DAY".equals(r.status)).count()));
    }

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

    @FXML private void handleReset() {
        cmbSection.setValue("All");
        cmbStatus.setValue("All");
        dpFrom.setValue(null);
        dpTo.setValue(null);
        attTable.setItems(allRows);
        updateKPIs(allRows);
    }

    @FXML private void handleExportPdf() {
        String[] h = {"Emp#","Name","Section","Date","Clock In","Clock Out","Status"};
        List<String[]> d = new ArrayList<>();
        for (AttRow r : attTable.getItems())
            d.add(new String[]{String.valueOf(r.empId),r.empName,r.section,r.date,
                    r.clockIn!=null?r.clockIn:"-", r.clockOut!=null?r.clockOut:"-", r.status});
        PdfExporter.export("Attendance Report", h, d, attTable.getScene().getWindow());
    }

    @FXML private void handleExportExcel() {
        String[] h = {"Emp#","Name","Section","Date","Clock In","Clock Out","Status"};
        List<String[]> d = new ArrayList<>();
        for (AttRow r : attTable.getItems())
            d.add(new String[]{String.valueOf(r.empId),r.empName,r.section,r.date,
                    r.clockIn!=null?r.clockIn:"-", r.clockOut!=null?r.clockOut:"-", r.status});
        ExcelExporter.export("Attendance", h, d, attTable.getScene().getWindow());
    }

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

    public static class AttRow {
        public int empId;
        public String empName, section, date, clockIn, clockOut, status;
    }

    public static class SubRow {
        public int id;
        public String type, details, rawDetails, submittedAt, username;
    }
}