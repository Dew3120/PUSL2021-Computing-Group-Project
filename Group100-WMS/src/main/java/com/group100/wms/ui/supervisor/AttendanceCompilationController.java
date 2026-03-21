package com.group100.wms.ui.supervisor;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.core.SessionManager;
import com.group100.wms.util.PdfExporter;
import com.group100.wms.util.ExcelExporter;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class AttendanceCompilationController implements Initializable {

    @FXML private ComboBox<String> cmbSection;
    @FXML private ComboBox<String> cmbMonth;
    @FXML private ComboBox<String> cmbYear;
    @FXML private DatePicker dpDate;
    @FXML private TextField txtSearch;
    @FXML private Label lblTotalDays;
    @FXML private Label lblPresent;
    @FXML private Label lblAbsent;
    @FXML private Label lblHalfDay;
    @FXML private Label lblAttendanceRate;
    @FXML private TableView<AttRow> tblAttendance;
    @FXML private TableColumn<AttRow, String> colEmpId;
    @FXML private TableColumn<AttRow, String> colName;
    @FXML private TableColumn<AttRow, String> colSection;
    @FXML private TableColumn<AttRow, String> colDate;
    @FXML private TableColumn<AttRow, String> colClockIn;
    @FXML private TableColumn<AttRow, String> colClockOut;
    @FXML private TableColumn<AttRow, String> colStatus;
    @FXML private TableColumn<AttRow, String> colNotes;
    @FXML private TableColumn<AttRow, String> colActions;
    @FXML private TableView<SummaryRow> tblSummary;
    @FXML private TableColumn<SummaryRow, String> colSumName;
    @FXML private TableColumn<SummaryRow, String> colSumSection;
    @FXML private TableColumn<SummaryRow, String> colSumPresent;
    @FXML private TableColumn<SummaryRow, String> colSumAbsent;
    @FXML private TableColumn<SummaryRow, String> colSumHalfDay;
    @FXML private TableColumn<SummaryRow, String> colSumRate;

    private final ObservableList<AttRow>     allAttRows  = FXCollections.observableArrayList();
    private final ObservableList<AttRow>     shownRows   = FXCollections.observableArrayList();
    private final ObservableList<SummaryRow> summaryRows = FXCollections.observableArrayList();
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupFilters();
        setupDailyTable();
        setupSummaryTable();
        dpDate.setValue(LocalDate.now());
        loadDailyData();
        loadMonthlySummary();
    }

    private void setupFilters() {
        cmbSection.setItems(FXCollections.observableArrayList("All Sections","WMS-1","WMS-2","WMS-3","WMS-4"));
        cmbSection.setValue("All Sections");
        List<String> months = new ArrayList<>();
        for (Month m : Month.values()) months.add(m.getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        cmbMonth.setItems(FXCollections.observableArrayList(months));
        cmbMonth.setValue(LocalDate.now().getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        cmbYear.setItems(FXCollections.observableArrayList("2024","2025","2026"));
        cmbYear.setValue(String.valueOf(LocalDate.now().getYear()));
        cmbSection.setOnAction(e -> { loadDailyData(); loadMonthlySummary(); });
        dpDate.setOnAction(e -> loadDailyData());
        cmbMonth.setOnAction(e -> loadMonthlySummary());
        cmbYear.setOnAction(e -> loadMonthlySummary());
        txtSearch.textProperty().addListener((obs, o, n) -> applySearch());
    }

    private void applySearch() {
        String q = txtSearch.getText().toLowerCase().trim();
        if (q.isEmpty()) { shownRows.setAll(allAttRows); return; }
        shownRows.setAll(allAttRows.stream().filter(r -> r.fullName.toLowerCase().contains(q)).toList());
    }

    private void setupDailyTable() {
        colEmpId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().empId)));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().fullName));
        colSection.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().section));
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().date));
        colClockIn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().clockIn));
        colClockOut.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().clockOut));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().status));
        colNotes.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().notes));
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
        colActions.setCellFactory(col -> new TableCell<>() {
            private final ComboBox<String> cmb = new ComboBox<>(FXCollections.observableArrayList("PRESENT","ABSENT","HALF_DAY"));
            private final Button btnSave = new Button("Save");
            private final javafx.scene.layout.HBox box = new javafx.scene.layout.HBox(4, cmb, btnSave);
            {
                btnSave.setStyle("-fx-background-color:#27ae60;-fx-text-fill:white;-fx-background-radius:4;-fx-padding:4 10;-fx-font-weight:bold;");
                btnSave.setOnAction(e -> updateAttendanceStatus(getTableView().getItems().get(getIndex()), cmb.getValue()));
            }
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                cmb.setValue(getTableView().getItems().get(getIndex()).status);
                setGraphic(box);
            }
        });
        tblAttendance.setItems(shownRows);
    }

    private void setupSummaryTable() {
        colSumName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().fullName));
        colSumSection.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().section));
        colSumPresent.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().presentDays)));
        colSumAbsent.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().absentDays)));
        colSumHalfDay.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().halfDays)));
        colSumRate.setCellValueFactory(c -> new SimpleStringProperty(String.format("%.1f%%", c.getValue().attendanceRate)));
        colSumRate.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                double rate = Double.parseDouble(item.replace("%",""));
                setStyle(rate >= 90 ? "-fx-text-fill:#27ae60;-fx-font-weight:bold;" : rate >= 75 ? "-fx-text-fill:#f39c12;-fx-font-weight:bold;" : "-fx-text-fill:#e74c3c;-fx-font-weight:bold;");
            }
        });
        tblSummary.setItems(summaryRows);
    }

    private void loadDailyData() {
        LocalDate date = dpDate.getValue();
        if (date == null) return;
        String section = cmbSection.getValue();
        new Thread(() -> {
            List<AttRow> rows = new ArrayList<>();
            StringBuilder sql = new StringBuilder("SELECT e.employee_id, e.full_name, e.section, a.attendance_id, a.date, a.clock_in, a.clock_out, a.status, a.notes FROM employees e LEFT JOIN attendance_records a ON e.employee_id = a.employee_id AND a.date = ? WHERE e.is_active = 1");
            if (!section.equals("All Sections")) sql.append(" AND e.section = ?");
            sql.append(" ORDER BY e.section ASC, e.employee_id ASC");
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                ps.setDate(1, java.sql.Date.valueOf(date));
                if (!section.equals("All Sections")) ps.setString(2, section);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        AttRow r = new AttRow();
                        r.empId    = rs.getInt("employee_id");
                        r.fullName = rs.getString("full_name");
                        r.section  = rs.getString("section");
                        r.date     = date.format(DATE_FMT);
                        r.clockIn  = rs.getTime("clock_in")  != null ? rs.getTime("clock_in").toLocalTime().format(TIME_FMT)  : "08:00";
                        r.clockOut = rs.getTime("clock_out") != null ? rs.getTime("clock_out").toLocalTime().format(TIME_FMT) : "17:00";
                        r.status   = rs.getString("status") != null ? rs.getString("status") : "PRESENT";
                        r.notes    = rs.getString("notes")  != null ? rs.getString("notes")  : "";
                        r.attId    = rs.getInt("attendance_id");
                        rows.add(r);
                    }
                }
            } catch (SQLException ex) { ex.printStackTrace(); }
            Platform.runLater(() -> {
                allAttRows.setAll(rows);
                shownRows.setAll(rows);
                long present = rows.stream().filter(r -> r.status.equals("PRESENT")).count();
                long absent  = rows.stream().filter(r -> r.status.equals("ABSENT")).count();
                long half    = rows.stream().filter(r -> r.status.equals("HALF_DAY")).count();
                double rate  = rows.isEmpty() ? 0.0 : (present + half * 0.5) / rows.size() * 100.0;
                lblTotalDays.setText(String.valueOf(rows.size()));
                lblPresent.setText(String.valueOf(present));
                lblAbsent.setText(String.valueOf(absent));
                lblHalfDay.setText(String.valueOf(half));
                lblAttendanceRate.setText(String.format("%.1f%%", rate));
            });
        }).start();
    }

    private void loadMonthlySummary() {
        int monthIdx = cmbMonth.getSelectionModel().getSelectedIndex() + 1;
        int year;
        try { year = Integer.parseInt(cmbYear.getValue()); } catch (NumberFormatException e) { return; }
        String section = cmbSection.getValue();
        new Thread(() -> {
            List<SummaryRow> rows = new ArrayList<>();
            StringBuilder sql = new StringBuilder("SELECT e.employee_id, e.full_name, e.section, SUM(CASE WHEN a.status='PRESENT' THEN 1 ELSE 0 END) AS present_days, SUM(CASE WHEN a.status='ABSENT' THEN 1 ELSE 0 END) AS absent_days, SUM(CASE WHEN a.status='HALF_DAY' THEN 1 ELSE 0 END) AS half_days, COUNT(a.attendance_id) AS total_records FROM employees e LEFT JOIN attendance_records a ON e.employee_id = a.employee_id AND MONTH(a.date)=? AND YEAR(a.date)=? WHERE e.is_active=1");
            if (!section.equals("All Sections")) sql.append(" AND e.section=?");
            sql.append(" GROUP BY e.employee_id, e.full_name, e.section ORDER BY e.section ASC, e.employee_id ASC");
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                ps.setInt(1, monthIdx);
                ps.setInt(2, year);
                if (!section.equals("All Sections")) ps.setString(3, section);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        SummaryRow r = new SummaryRow();
                        r.empId       = rs.getInt("employee_id");
                        r.fullName    = rs.getString("full_name");
                        r.section     = rs.getString("section");
                        r.presentDays = rs.getInt("present_days");
                        r.absentDays  = rs.getInt("absent_days");
                        r.halfDays    = rs.getInt("half_days");
                        int total     = rs.getInt("total_records");
                        r.attendanceRate = total == 0 ? 0.0 : (r.presentDays + r.halfDays * 0.5) / total * 100.0;
                        rows.add(r);
                    }
                }
            } catch (SQLException ex) { ex.printStackTrace(); }
            Platform.runLater(() -> summaryRows.setAll(rows));
        }).start();
    }

    private void updateAttendanceStatus(AttRow row, String newStatus) {
        if (newStatus == null) return;
        new Thread(() -> {
            try (Connection conn = DatabaseConnection.getConnection()) {
                if (row.attId > 0) {
                    try (PreparedStatement ps = conn.prepareStatement("UPDATE attendance_records SET status=?, notes=? WHERE attendance_id=?")) {
                        ps.setString(1, newStatus); ps.setString(2, "Updated by supervisor"); ps.setInt(3, row.attId); ps.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ps = conn.prepareStatement("INSERT INTO attendance_records (employee_id, date, status, notes) VALUES (?,?,?,'Marked by supervisor')")) {
                        ps.setInt(1, row.empId); ps.setDate(2, java.sql.Date.valueOf(dpDate.getValue())); ps.setString(3, newStatus); ps.executeUpdate();
                    }
                }
                Platform.runLater(() -> { row.status = newStatus; tblAttendance.refresh(); loadDailyData();
                    new Alert(Alert.AlertType.INFORMATION, row.fullName + " marked as " + newStatus, ButtonType.OK).showAndWait();
                });
            } catch (SQLException ex) {
                ex.printStackTrace();
                Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Update failed: " + ex.getMessage(), ButtonType.OK).showAndWait());
            }
        }).start();
    }

    @FXML private void onSendTodayToAdmin() {
        LocalDate today = dpDate.getValue() != null ? dpDate.getValue() : LocalDate.now();
        long present  = shownRows.stream().filter(r -> r.status.equals("PRESENT")).count();
        long absent   = shownRows.stream().filter(r -> r.status.equals("ABSENT")).count();
        long halfDay  = shownRows.stream().filter(r -> r.status.equals("HALF_DAY")).count();
        new Alert(Alert.AlertType.CONFIRMATION,
                "Send attendance for " + today + " to Admin?\n" +
                "Total: " + shownRows.size() + " | Present: " + present + " | Absent: " + absent + " | Half Day: " + halfDay,
                ButtonType.YES, ButtonType.NO).showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                StringBuilder details = new StringBuilder();
                details.append("DATE=").append(today).append("|");
                details.append("TOTAL=").append(shownRows.size()).append("|");
                details.append("PRESENT=").append(present).append("|");
                details.append("ABSENT=").append(absent).append("|");
                details.append("HALFDAY=").append(halfDay).append("|");
                details.append("SECTION=").append(cmbSection.getValue()).append("|");
                details.append("RECORDS=");
                shownRows.forEach(r -> details.append(r.empId).append(":").append(r.fullName).append(":").append(r.status).append(","));
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement("INSERT INTO audit_logs (user_id,action,table_name,record_id,timestamp,details,created_at) VALUES (?,?,?,?,NOW(),?,NOW())")) {
                    ps.setInt(1, SessionManager.getCurrentUser().getId());
                    ps.setString(2, "DAILY_ATTENDANCE_SUBMISSION");
                    ps.setString(3, "attendance_records");
                    ps.setInt(4, today.getDayOfMonth());
                    ps.setString(5, details.toString());
                    ps.executeUpdate();
                    Platform.runLater(() -> new Alert(Alert.AlertType.INFORMATION,
                            "Attendance for " + today + " sent to Admin!", ButtonType.OK).showAndWait());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Failed: " + ex.getMessage(), ButtonType.OK).showAndWait());
                }
            }
        });
    }

    @FXML private void onSubmitToAdmin() {
        int monthIdx = cmbMonth.getSelectionModel().getSelectedIndex() + 1;
        String mName = cmbMonth.getValue();
        String year  = cmbYear.getValue();
        new Alert(Alert.AlertType.CONFIRMATION, "Submit attendance for " + mName + " " + year + " to Admin?", ButtonType.YES, ButtonType.NO).showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement ps = conn.prepareStatement("INSERT INTO audit_logs (user_id,action,table_name,record_id,timestamp,details,created_at) VALUES (?,?,?,?,NOW(),?,NOW())")) {
                    ps.setInt(1, SessionManager.getCurrentUser().getId());
                    ps.setString(2, "ATTENDANCE_SUBMISSION");
                    ps.setString(3, "attendance_records");
                    ps.setInt(4, monthIdx);
                    ps.setString(5, "Supervisor submitted attendance for " + mName + " " + year + ". Employees: " + summaryRows.size());
                    ps.executeUpdate();
                    Platform.runLater(() -> new Alert(Alert.AlertType.INFORMATION, "Attendance for " + mName + " " + year + " submitted to Admin.", ButtonType.OK).showAndWait());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, "Submission failed: " + ex.getMessage(), ButtonType.OK).showAndWait());
                }
            }
        });
    }

    @FXML private void onExportDailyPdf() {
        String[] h = {"ID","Name","Section","Date","Clock In","Clock Out","Status"};
        List<String[]> d = shownRows.stream().map(r -> new String[]{String.valueOf(r.empId),r.fullName,r.section,r.date,r.clockIn,r.clockOut,r.status}).toList();
        PdfExporter.export("Daily Attendance - " + dpDate.getValue(), h, d, tblAttendance.getScene().getWindow());
    }

    @FXML private void onExportMonthlyExcel() {
        String[] h = {"ID","Name","Section","Present","Absent","Half Day","Rate %"};
        List<String[]> d = summaryRows.stream().map(r -> new String[]{String.valueOf(r.empId),r.fullName,r.section,String.valueOf(r.presentDays),String.valueOf(r.absentDays),String.valueOf(r.halfDays),String.format("%.1f",r.attendanceRate)}).toList();
        ExcelExporter.export("Monthly Attendance", h, d, tblSummary.getScene().getWindow());
    }

    @FXML private void onRefresh() { loadDailyData(); loadMonthlySummary(); }

    public static class AttRow {
        public int attId, empId;
        public String fullName, section, date, clockIn, clockOut, status, notes;
    }

    public static class SummaryRow {
        public int empId, presentDays, absentDays, halfDays;
        public String fullName, section;
        public double attendanceRate;
    }
}