package com.group100.wms.ui.supervisor;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.util.PdfExporter;
import com.group100.wms.util.ExcelExporter;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class EmployeeDirectoryController implements Initializable {

    @FXML private Label lblTotalWorkers;
    @FXML private Label lblActiveWorkers;
    @FXML private Label lblResignedWorkers;
    @FXML private Label lblMaleCount;
    @FXML private Label lblFemaleCount;
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbSection;
    @FXML private ComboBox<String> cmbGender;
    @FXML private ComboBox<String> cmbStatus;
    @FXML private TableView<EmpRow> tblEmployees;
    @FXML private TableColumn<EmpRow, String> colId;
    @FXML private TableColumn<EmpRow, String> colName;
    @FXML private TableColumn<EmpRow, String> colDesignation;
    @FXML private TableColumn<EmpRow, String> colSection;
    @FXML private TableColumn<EmpRow, String> colGender;
    @FXML private TableColumn<EmpRow, String> colAge;
    @FXML private TableColumn<EmpRow, String> colPhone;
    @FXML private TableColumn<EmpRow, String> colBloodGroup;
    @FXML private TableColumn<EmpRow, String> colStatus;
    @FXML private TableColumn<EmpRow, String> colActions;

    private final ObservableList<EmpRow> allRows   = FXCollections.observableArrayList();
    private final ObservableList<EmpRow> shownRows = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        setupFilters();
        loadData();
    }

    private void setupTable() {
        colId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().empId)));
        colName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().fullName));
        colDesignation.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().designation));
        colSection.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().section));
        colGender.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().gender));
        colAge.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().age > 0 ? String.valueOf(c.getValue().age) : "-"));
        colPhone.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().phone));
        colBloodGroup.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().bloodGroup));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().status));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle(item.equals("RESIGNED") ? "-fx-text-fill:#e74c3c;-fx-font-weight:bold;" : "-fx-text-fill:#27ae60;-fx-font-weight:bold;");
            }
        });
        colBloodGroup.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isBlank()) { setText("-"); setStyle(""); return; }
                setText(item);
                setStyle("-fx-text-fill:#c0392b;-fx-font-weight:bold;");
            }
        });
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("View Profile");
            { btn.getStyleClass().add("btn-primary"); btn.setOnAction(e -> openProfile(getTableView().getItems().get(getIndex()))); }
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
        tblEmployees.setItems(shownRows);
        tblEmployees.setRowFactory(tv -> {
            TableRow<EmpRow> row = new TableRow<>();
            row.itemProperty().addListener((obs, old, emp) -> {
                if (emp != null && emp.status.equals("RESIGNED")) row.setStyle("-fx-background-color:#fff0f0;");
                else row.setStyle("");
            });
            return row;
        });
    }

    private void setupFilters() {
        cmbSection.setItems(FXCollections.observableArrayList("All Sections","WMS-1","WMS-2","WMS-3","WMS-4"));
        cmbSection.setValue("All Sections");
        cmbGender.setItems(FXCollections.observableArrayList("All","Male","Female"));
        cmbGender.setValue("All");
        cmbStatus.setItems(FXCollections.observableArrayList("All","Active","Resigned"));
        cmbStatus.setValue("All");
        txtSearch.textProperty().addListener((obs, o, n) -> applyFilters());
        cmbSection.setOnAction(e -> applyFilters());
        cmbGender.setOnAction(e -> applyFilters());
        cmbStatus.setOnAction(e -> applyFilters());
    }

    private void loadData() {
        new Thread(() -> {
            List<EmpRow> rows = new ArrayList<>();
            String sql = "SELECT e.employee_id, e.full_name, e.designation, e.section, e.gender, e.age, e.date_of_birth, e.marital_status, e.address, e.city, e.phone, e.email, e.emergency_contact_name, e.emergency_contact_phone, e.blood_group, e.joined_date, e.resignation_date, e.nic, e.daily_rate, e.is_active, e.bank_name, e.bank_branch, e.account_number FROM employees e ORDER BY e.section, e.full_name";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EmpRow r = new EmpRow();
                    r.empId           = rs.getInt("employee_id");
                    r.fullName        = rs.getString("full_name");
                    r.designation     = rs.getString("designation");
                    r.section         = rs.getString("section");
                    r.gender          = rs.getString("gender")         != null ? rs.getString("gender")         : "-";
                    r.age             = rs.getInt("age");
                    r.dob             = rs.getDate("date_of_birth")    != null ? rs.getDate("date_of_birth").toString()    : "-";
                    r.maritalStatus   = rs.getString("marital_status") != null ? rs.getString("marital_status") : "-";
                    r.address         = rs.getString("address")        != null ? rs.getString("address")        : "-";
                    r.city            = rs.getString("city")           != null ? rs.getString("city")           : "-";
                    r.phone           = rs.getString("phone")          != null ? rs.getString("phone")          : "-";
                    r.email           = rs.getString("email")          != null ? rs.getString("email")          : "-";
                    r.emergencyName   = rs.getString("emergency_contact_name")  != null ? rs.getString("emergency_contact_name")  : "-";
                    r.emergencyPhone  = rs.getString("emergency_contact_phone") != null ? rs.getString("emergency_contact_phone") : "-";
                    r.bloodGroup      = rs.getString("blood_group")    != null ? rs.getString("blood_group")    : "-";
                    r.joinedDate      = rs.getDate("joined_date")      != null ? rs.getDate("joined_date").toString()      : "-";
                    r.resignationDate = rs.getDate("resignation_date") != null ? rs.getDate("resignation_date").toString() : "-";
                    r.nic             = rs.getString("nic")            != null ? rs.getString("nic")            : "-";
                    r.dailyRate       = rs.getDouble("daily_rate");
                    r.bankName        = rs.getString("bank_name")      != null ? rs.getString("bank_name")      : "-";
                    r.bankBranch      = rs.getString("bank_branch")    != null ? rs.getString("bank_branch")    : "-";
                    r.accountNumber   = rs.getString("account_number") != null ? rs.getString("account_number") : "-";
                    r.status          = rs.getBoolean("is_active") ? "Active" : "RESIGNED";
                    rows.add(r);
                }
            } catch (SQLException ex) { ex.printStackTrace(); }
            Platform.runLater(() -> { allRows.setAll(rows); shownRows.setAll(rows); updateKpis(rows); });
        }).start();
    }

    private void applyFilters() {
        String search  = txtSearch.getText().toLowerCase().trim();
        String section = cmbSection.getValue();
        String gender  = cmbGender.getValue();
        String status  = cmbStatus.getValue();
        shownRows.setAll(allRows.stream().filter(r -> {
            boolean ms  = search.isEmpty() || r.fullName.toLowerCase().contains(search) || r.nic.toLowerCase().contains(search) || r.phone.contains(search);
            boolean msc = section.equals("All Sections") || r.section.equals(section);
            boolean mg  = gender.equals("All") || r.gender.equals(gender);
            boolean mst = status.equals("All") || (status.equals("Active") && r.status.equals("Active")) || (status.equals("Resigned") && r.status.equals("RESIGNED"));
            return ms && msc && mg && mst;
        }).toList());
    }

    private void updateKpis(List<EmpRow> rows) {
        lblTotalWorkers.setText(String.valueOf(rows.size()));
        lblActiveWorkers.setText(String.valueOf(rows.stream().filter(r -> r.status.equals("Active")).count()));
        lblResignedWorkers.setText(String.valueOf(rows.stream().filter(r -> r.status.equals("RESIGNED")).count()));
        lblMaleCount.setText(String.valueOf(rows.stream().filter(r -> r.gender.equals("Male")).count()));
        lblFemaleCount.setText(String.valueOf(rows.stream().filter(r -> r.gender.equals("Female")).count()));
    }

    private void openProfile(EmpRow row) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/supervisor/EmployeeProfile.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Employee Profile - " + row.fullName);
            stage.setScene(new Scene(loader.load()));
            stage.initModality(Modality.APPLICATION_MODAL);
            EmployeeProfileController ctrl = loader.getController();
            ctrl.setEmployee(row);
            stage.showAndWait();
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    @FXML private void onExportPdf() {
        String[] h = {"ID","Name","Designation","Section","Gender","Age","Phone","City","Blood Grp","Status"};
        List<String[]> d = shownRows.stream().map(r -> new String[]{String.valueOf(r.empId),r.fullName,r.designation,r.section,r.gender,String.valueOf(r.age),r.phone,r.city,r.bloodGroup,r.status}).toList();
        PdfExporter.export("Employee Directory", h, d, tblEmployees.getScene().getWindow());
    }

    @FXML private void onExportExcel() {
        String[] h = {"ID","Name","Designation","Section","Gender","Age","Phone","Email","City","Blood Grp","NIC","Joined Date","Status"};
        List<String[]> d = shownRows.stream().map(r -> new String[]{String.valueOf(r.empId),r.fullName,r.designation,r.section,r.gender,String.valueOf(r.age),r.phone,r.email,r.city,r.bloodGroup,r.nic,r.joinedDate,r.status}).toList();
        ExcelExporter.export("Employee Directory", h, d, tblEmployees.getScene().getWindow());
    }

    @FXML private void onRefresh()      { loadData(); }
    @FXML private void onClearFilters() { txtSearch.clear(); cmbSection.setValue("All Sections"); cmbGender.setValue("All"); cmbStatus.setValue("All"); shownRows.setAll(allRows); }

    public static class EmpRow {
        public int empId, age;
        public String fullName, designation, section, gender, dob, maritalStatus;
        public String address, city, phone, email;
        public String emergencyName, emergencyPhone;
        public String bloodGroup, joinedDate, resignationDate;
        public String nic, bankName, bankBranch, accountNumber;
        public double dailyRate;
        public String status;
    }
}
