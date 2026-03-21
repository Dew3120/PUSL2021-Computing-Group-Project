package com.group100.wms.ui.supervisor;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.core.SessionManager;
import com.group100.wms.model.LeaveRequest;
import com.group100.wms.repository.LeaveRequestRepository;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LeaveRequestController implements Initializable {

    @FXML private Label lblTotalRequests;
    @FXML private Label lblPendingCount;
    @FXML private Label lblApprovedCount;
    @FXML private Label lblRejectedCount;
    @FXML private ComboBox<String> cmbStatusFilter;
    @FXML private ComboBox<String> cmbSectionFilter;
    @FXML private TableView<LeaveRequest> tblRequests;
    @FXML private TableColumn<LeaveRequest, String> colReqId;
    @FXML private TableColumn<LeaveRequest, String> colEmpName;
    @FXML private TableColumn<LeaveRequest, String> colSection;
    @FXML private TableColumn<LeaveRequest, String> colDate;
    @FXML private TableColumn<LeaveRequest, String> colType;
    @FXML private TableColumn<LeaveRequest, String> colReason;
    @FXML private TableColumn<LeaveRequest, String> colStatus;
    @FXML private TableColumn<LeaveRequest, String> colCreatedAt;
    @FXML private TableColumn<LeaveRequest, String> colActions;
    @FXML private ComboBox<String> cmbEmployee;
    @FXML private TextField txtEmpSearch;
    @FXML private ComboBox<String> cmbEmpSection;
    @FXML private DatePicker dpRequestDate;
    @FXML private ComboBox<String> cmbLeaveType;
    @FXML private TextArea txtReason;

    private final LeaveRequestRepository repo = new LeaveRequestRepository();
    private final ObservableList<LeaveRequest> allRows   = FXCollections.observableArrayList();
    private final ObservableList<LeaveRequest> shownRows = FXCollections.observableArrayList();
    private final java.util.Map<String, Integer> employeeMap = new java.util.LinkedHashMap<>();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();
        setupFilters();
        setupForm();
        loadData();
    }

    private void setupTable() {
        colReqId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getRequestId())));
        colEmpName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmployeeName()));
        colSection.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getSection()));
        colDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getRequestDate() != null ? c.getValue().getRequestDate().format(FMT) : "-"));
        colType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLeaveType()));
        colReason.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getReason() != null ? c.getValue().getReason() : "-"));
        colStatus.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getStatus()));
        colCreatedAt.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCreatedAt() != null ? c.getValue().getCreatedAt().format(DateTimeFormatter.ofPattern("MMM dd, HH:mm")) : "-"));
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle(switch (item) {
                    case "APPROVED" -> "-fx-text-fill:#27ae60;-fx-font-weight:bold;";
                    case "REJECTED" -> "-fx-text-fill:#e74c3c;-fx-font-weight:bold;";
                    default         -> "-fx-text-fill:#f39c12;-fx-font-weight:bold;";
                });
            }
        });
        colType.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle(item.equals("HALF_DAY") ? "-fx-text-fill:#8e44ad;-fx-font-weight:bold;" : "-fx-text-fill:#2980b9;-fx-font-weight:bold;");
            }
        });
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btnApprove = new Button("Approve");
            private final Button btnReject  = new Button("Reject");
            private final javafx.scene.layout.HBox box = new javafx.scene.layout.HBox(6, btnApprove, btnReject);
            {
                btnApprove.getStyleClass().add("btn-success");
                btnReject.setStyle("-fx-background-color:#e74c3c;-fx-text-fill:white;-fx-background-radius:4;-fx-padding:4 10;");
                btnApprove.setOnAction(e -> handleApprove(getTableView().getItems().get(getIndex())));
                btnReject.setOnAction(e -> handleReject(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); return; }
                boolean pending = "PENDING".equals(getTableView().getItems().get(getIndex()).getStatus());
                btnApprove.setDisable(!pending);
                btnReject.setDisable(!pending);
                setGraphic(box);
            }
        });
        tblRequests.setItems(shownRows);
    }

    private void setupFilters() {
        cmbStatusFilter.setItems(FXCollections.observableArrayList("All","PENDING","APPROVED","REJECTED"));
        cmbStatusFilter.setValue("All");
        cmbSectionFilter.setItems(FXCollections.observableArrayList("All Sections","WMS-1","WMS-2","WMS-3","WMS-4"));
        cmbSectionFilter.setValue("All Sections");
        cmbStatusFilter.setOnAction(e -> applyFilters());
        cmbSectionFilter.setOnAction(e -> applyFilters());
    }

    private void applyFilters() {
        String status  = cmbStatusFilter.getValue();
        String section = cmbSectionFilter.getValue();
        shownRows.setAll(allRows.stream().filter(r ->
                (status.equals("All") || r.getStatus().equals(status)) &&
                (section.equals("All Sections") || r.getSection().equals(section))
        ).toList());
    }

    private void setupForm() {
        cmbLeaveType.setItems(FXCollections.observableArrayList("HALF_DAY","FULL_DAY"));
        cmbLeaveType.setValue("HALF_DAY");
        dpRequestDate.setValue(LocalDate.now());
        cmbEmpSection.setItems(FXCollections.observableArrayList("All Sections","WMS-1","WMS-2","WMS-3","WMS-4"));
        cmbEmpSection.setValue("All Sections");
        cmbEmpSection.setOnAction(e -> filterEmployees());
        txtEmpSearch.textProperty().addListener((obs,o,n) -> filterEmployees());
        new Thread(() -> {
            String sql = "SELECT employee_id, full_name, section FROM employees WHERE is_active=1 ORDER BY section, full_name";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                List<String> names = new ArrayList<>();
                while (rs.next()) {
                    String d = rs.getString("full_name") + " [" + rs.getString("section") + "]";
                    employeeMap.put(d, rs.getInt("employee_id"));
                    names.add(d);
                }
                Platform.runLater(() -> {
                    cmbEmployee.setItems(FXCollections.observableArrayList(names));
                    if (!names.isEmpty()) cmbEmployee.setValue(names.get(0));
                });
            } catch (SQLException ex) { ex.printStackTrace(); }
        }).start();
    }

    private void loadData() {
        new Thread(() -> {
            try {
                List<LeaveRequest> rows = repo.findAll();
                Platform.runLater(() -> { allRows.setAll(rows); shownRows.setAll(rows); updateKpis(rows); });
            } catch (SQLException ex) { ex.printStackTrace(); }
        }).start();
    }

    private void updateKpis(List<LeaveRequest> rows) {
        lblTotalRequests.setText(String.valueOf(rows.size()));
        lblPendingCount.setText(String.valueOf(rows.stream().filter(r -> r.getStatus().equals("PENDING")).count()));
        lblApprovedCount.setText(String.valueOf(rows.stream().filter(r -> r.getStatus().equals("APPROVED")).count()));
        lblRejectedCount.setText(String.valueOf(rows.stream().filter(r -> r.getStatus().equals("REJECTED")).count()));
    }

    @FXML private void onSubmitRequest() {
        String empDisplay = cmbEmployee.getValue();
        if (empDisplay == null || dpRequestDate.getValue() == null) {
            new Alert(Alert.AlertType.WARNING, "Please select an employee and a date.", ButtonType.OK).showAndWait();
            return;
        }
        Integer empId = employeeMap.get(empDisplay);
        if (empId == null) return;
        LeaveRequest req = new LeaveRequest();
        req.setEmployeeId(empId);
        req.setRequestDate(dpRequestDate.getValue());
        req.setLeaveType(cmbLeaveType.getValue());
        req.setReason(txtReason.getText().trim());
        req.setCreatedBy(SessionManager.getCurrentUser().getId());
        try {
            repo.save(req);
            txtReason.clear();
            dpRequestDate.setValue(LocalDate.now());
            loadData();
            new Alert(Alert.AlertType.INFORMATION, "Request submitted successfully.", ButtonType.OK).showAndWait();
        } catch (SQLException ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed: " + ex.getMessage(), ButtonType.OK).showAndWait();
        }
    }

    private void handleApprove(LeaveRequest req) {
        new Alert(Alert.AlertType.CONFIRMATION, "Approve " + req.getLeaveType() + " for " + req.getEmployeeName() + "?", ButtonType.YES, ButtonType.NO).showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try { repo.approve(req.getRequestId()); loadData();
                    new Alert(Alert.AlertType.INFORMATION, "Approved. Attendance updated.", ButtonType.OK).showAndWait();
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
        });
    }

    private void handleReject(LeaveRequest req) {
        new Alert(Alert.AlertType.CONFIRMATION, "Reject request for " + req.getEmployeeName() + "?", ButtonType.YES, ButtonType.NO).showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) {
                try { repo.reject(req.getRequestId()); loadData();
                    new Alert(Alert.AlertType.INFORMATION, "Request rejected.", ButtonType.OK).showAndWait();
                } catch (SQLException ex) { ex.printStackTrace(); }
            }
        });
    }

    @FXML private void onExportPdf() {
        String[] h = {"#","Employee","Section","Date","Type","Reason","Status"};
        List<String[]> d = shownRows.stream().map(r -> new String[]{String.valueOf(r.getRequestId()),r.getEmployeeName(),r.getSection(),r.getRequestDate()!=null?r.getRequestDate().toString():"-",r.getLeaveType(),r.getReason()!=null?r.getReason():"-",r.getStatus()}).toList();
        PdfExporter.export("Leave Requests", h, d, tblRequests.getScene().getWindow());
    }

    @FXML private void onExportExcel() {
        String[] h = {"#","Employee","Section","Date","Type","Reason","Status","Created At"};
        List<String[]> d = shownRows.stream().map(r -> new String[]{String.valueOf(r.getRequestId()),r.getEmployeeName(),r.getSection(),r.getRequestDate()!=null?r.getRequestDate().toString():"-",r.getLeaveType(),r.getReason()!=null?r.getReason():"-",r.getStatus(),r.getCreatedAt()!=null?r.getCreatedAt().toString():"-"}).toList();
        ExcelExporter.export("Leave Requests", h, d, tblRequests.getScene().getWindow());
    }

    @FXML private void onRefresh() { loadData(); }

    private void filterEmployees() {
        String search  = txtEmpSearch.getText().toLowerCase().trim();
        String section = cmbEmpSection.getValue();
        List<String> filtered = employeeMap.keySet().stream().filter(name -> {
            boolean ms = search.isEmpty() || name.toLowerCase().contains(search);
            boolean msc = section == null || section.equals("All Sections") || name.contains("[" + section + "]");
            return ms && msc;
        }).toList();
        cmbEmployee.setItems(FXCollections.observableArrayList(filtered));
        if (!filtered.isEmpty()) cmbEmployee.setValue(filtered.get(0));
    }
}

