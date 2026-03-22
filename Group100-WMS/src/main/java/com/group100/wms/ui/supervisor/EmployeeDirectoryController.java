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

// OOP Concepts used in this class:
// 1. Encapsulation: Uses a nested static class (EmpRow) to bundle employee data fields, and private methods to manage UI state.
// 2. Abstraction: The controller abstracts complex SQL join-like data retrieval into a simple "loadData" call for the UI.
// 3. Separation of Concerns: It delegates the heavy lifting of file generation to specialized Utility classes (PdfExporter, ExcelExporter).
public class EmployeeDirectoryController implements Initializable {

    // UI components for displaying Key Performance Indicators (KPIs)
    @FXML private Label lblTotalWorkers, lblActiveWorkers, lblResignedWorkers, lblMaleCount, lblFemaleCount;
    
    // UI components for searching and filtering the directory
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbSection, cmbGender, cmbStatus;
    
    // The main table and its data columns
    @FXML private TableView<EmpRow> tblEmployees;
    @FXML private TableColumn<EmpRow, String> colId, colName, colDesignation, colSection, colGender, colAge, colPhone, colBloodGroup, colStatus, colActions;

    // ObservableLists to manage the data displayed in the TableView
    private final ObservableList<EmpRow> allRows   = FXCollections.observableArrayList();
    private final ObservableList<EmpRow> shownRows = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTable();   // Configures column rendering and styling
        setupFilters(); // Sets up listeners for search and dropdowns
        loadData();     // Fetches data from MySQL in a background thread
    }

    // Configures how each column maps to data and applies conditional styling (e.g., Red for RESIGNED status)
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
        
        // Custom Cell Factory for the Status column to show "RESIGNED" in red
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle(item.equals("RESIGNED") ? "-fx-text-fill:#e74c3c;-fx-font-weight:bold;" : "-fx-text-fill:#27ae60;-fx-font-weight:bold;");
            }
        });

        // Action column to open detailed profile modal
        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button btn = new Button("View Profile");
            { btn.getStyleClass().add("btn-primary"); btn.setOnAction(e -> openProfile(getTableView().getItems().get(getIndex()))); }
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        tblEmployees.setItems(shownRows);
    }

    // Loads employee data from the database using a background Thread to keep the UI responsive
    private void loadData() {
        new Thread(() -> {
            List<EmpRow> rows = new ArrayList<>();
            String sql = "SELECT * FROM employees ORDER BY section, full_name";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    EmpRow r = new EmpRow();
                    r.empId = rs.getInt("employee_id");
                    r.fullName = rs.getString("full_name");
                    r.status = rs.getBoolean("is_active") ? "Active" : "RESIGNED";
                    // ... (mapping other fields)
                    rows.add(r);
                }
            } catch (SQLException ex) { ex.printStackTrace(); }
            // Updates the UI on the JavaFX Application Thread once data is fetched
            Platform.runLater(() -> {
                allRows.setAll(rows);
                shownRows.setAll(rows);
                updateKpis(rows);
            });
        }).start();
    }

    // Filters the visible rows based on search text and ComboBox selections
    private void applyFilters() {
        String search = txtSearch.getText().toLowerCase().trim();
        shownRows.setAll(allRows.stream().filter(r -> {
            boolean matchesSearch = search.isEmpty() || r.fullName.toLowerCase().contains(search) || r.nic.toLowerCase().contains(search);
            boolean matchesSection = cmbSection.getValue().equals("All Sections") || r.section.equals(cmbSection.getValue());
            return matchesSearch && matchesSection;
        }).toList());
    }

    // Static nested class acting as a Data Transfer Object (DTO) for the table rows
    public static class EmpRow {
        public int empId, age;
        public String fullName, designation, section, gender, status, nic, phone, email, city, bloodGroup, joinedDate;
        // ... (other fields)
    }
}
