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

// OOP Concepts used in this class:
// 1. Abstraction: The LeaveRequestRepository abstracts the complex SQL logic for approving/rejecting leaves and updating attendance.
// 2. Encapsulation: State management for the table (allRows vs shownRows) and the employee mapping (employeeMap) is kept private.
// 3. Polymorphism: Uses a Switch expression within the CellFactory to determine dynamic styling for different status types.
public class LeaveRequestController implements Initializable {

    // KPI Labels for quick dashboard overview
    @FXML private Label lblTotalRequests, lblPendingCount, lblApprovedCount, lblRejectedCount;
    
    // Filtering and Table components
    @FXML private ComboBox<String> cmbStatusFilter, cmbSectionFilter;
    @FXML private TableView<LeaveRequest> tblRequests;
    @FXML private TableColumn<LeaveRequest, String> colReqId, colEmpName, colSection, colDate, colType, colReason, colStatus, colCreatedAt, colActions;

    // Form components for submitting a new leave request
    @FXML private ComboBox<String> cmbEmployee, cmbLeaveType, cmbEmpSection;
    @FXML private TextField txtEmpSearch;
    @FXML private DatePicker dpRequestDate;
    @FXML private TextArea txtReason;

    private final LeaveRequestRepository repo = new LeaveRequestRepository();
    private final ObservableList<LeaveRequest> allRows   = FXCollections.observableArrayList();
    private final ObservableList<LeaveRequest> shownRows = FXCollections.observableArrayList();
    
    // Maps display names in the dropdown to their unique database IDs
    private final java.util.Map<String, Integer> employeeMap = new java.util.LinkedHashMap<>();
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();   // Maps model properties to table columns
        setupFilters(); // Configures the top filter bar
        setupForm();    // Populates employee dropdowns and sets defaults
        loadData();     // Fetches requests from the repository
    }

    // Configures the table columns with conditional styling and action buttons
    private void setupTable() {
        // ... (Property mappings)

        // Conditional Styling: Green for APPROVED, Red for REJECTED, Orange for PENDING
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

        // Action Buttons: Approve and Reject buttons are only enabled if the status is PENDING
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

    // Submits a new leave request to the database
    @FXML private void onSubmitRequest() {
        String empDisplay = cmbEmployee.getValue();
        if (empDisplay == null || dpRequestDate.getValue() == null) {
            new Alert(Alert.AlertType.WARNING, "Required fields missing.").showAndWait();
            return;
        }

        Integer empId = employeeMap.get(empDisplay);
        LeaveRequest req = new LeaveRequest();
        req.setEmployeeId(empId);
        req.setRequestDate(dpRequestDate.getValue());
        req.setLeaveType(cmbLeaveType.getValue());
        req.setReason(txtReason.getText().trim());
        req.setCreatedBy(SessionManager.getCurrentUser().getId());

        try {
            repo.save(req);
            txtReason.clear();
            loadData(); // Refresh table and KPIs
            new Alert(Alert.AlertType.INFORMATION, "Request submitted.").showAndWait();
        } catch (SQLException ex) {
            new Alert(Alert.AlertType.ERROR, "Error: " + ex.getMessage()).showAndWait();
        }
    }

    // Handles the approval logic, which typically triggers an update in the Attendance table via the repository
    private void handleApprove(LeaveRequest req) {
        new Alert(Alert.AlertType.CONFIRMATION, "Approve this request?", ButtonType.YES, ButtonType.NO)
            .showAndWait().ifPresent(bt -> {
                if (bt == ButtonType.YES) {
                    try { 
                        repo.approve(req.getRequestId()); 
                        loadData();
                    } catch (SQLException ex) { ex.printStackTrace(); }
                }
            });
    }
}
