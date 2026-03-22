package com.group100.wms.ui.reports;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.util.PdfExporter;
import com.group100.wms.util.ExcelExporter;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

// OOP Concepts Used:
// Encapsulation - All UI components and related logic are encapsulated in this controller.
// Abstraction - Database operations abstracted via DatabaseConnection; export logic abstracted via PdfExporter and ExcelExporter.
// Inheritance - JavaFX TableView, TableColumn, and TableCell classes are used with overridden methods (updateItem).
// Polymorphism - Overriding of TableCell updateItem methods to provide customized display behavior.

public class ReportCentreController {

    // Labels to display total employees, active employees, and resigned employees
    @FXML private Label lblTotal, lblActive, lblResigned;

    // ComboBoxes for selecting employee section and filtering active/resigned employees
    @FXML private ComboBox<String> cmbSection, cmbActiveFilter;

    // TableView to display employee data
    @FXML private TableView<EmpRow> empTable;

    // TableColumns for employee details
    @FXML private TableColumn<EmpRow, Number> colId, colRate;
    @FXML private TableColumn<EmpRow, String> colName, colDesig, colSection, colBank, colBankAcc, colNic, colStatus;

    // ObservableList to store all employee rows for filtering and display
    private ObservableList<EmpRow> allRows;

    // Initializes the TableView columns, row styles, cell styles, and ComboBox values
    // Also loads all employee data from the database
    @FXML
    public void initialize() {
        // Map TableColumns to EmpRow properties
        colId.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().empId));
        colName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().fullName));
        colDesig.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().designation));
        colSection.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().section));
        colRate.setCellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().dailyRate));
        colBank.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().bankName));
        colBankAcc.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().accountNumber));
        colNic.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().nic));
        colStatus.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().resigned ? "RESIGNED" : "Active"));

        // Customize row colors for resigned employees
        empTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(EmpRow item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setStyle(""); return; }
                setStyle(item.resigned ? "-fx-background-color: #fce4e4;" : "");
            }
        });

        // Customize Status column text and color
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                if ("RESIGNED".equals(item)) {
                    setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                } else {
                    setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                }
            }
        });

        // Customize Name column to append "[RESIGNED]" for resigned employees
        colName.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                EmpRow row = getTableView().getItems().get(getIndex());
                setText(row.resigned ? item + " [RESIGNED]" : item);
                setStyle(row.resigned ? "-fx-text-fill: #e74c3c; -fx-font-weight: bold;" : "");
            }
        });

        // Initialize ComboBox options for section and active/resigned filters
        cmbSection.setItems(FXCollections.observableArrayList("All", "WMS-1", "WMS-2", "WMS-3", "WMS-4"));
        cmbSection.setValue("All");
        cmbActiveFilter.setItems(FXCollections.observableArrayList("All", "Active Only", "Resigned Only"));
        cmbActiveFilter.setValue("All");

        // Load employee data from the database
        loadData();
    }

    // Loads all employee records from the database and updates the TableView and summary labels
    private void loadData() {
        allRows = FXCollections.observableArrayList();
        String sql = "SELECT * FROM employees ORDER BY employee_id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                EmpRow r = new EmpRow();
                r.empId = rs.getInt("employee_id");
                r.fullName = rs.getString("full_name");
                r.designation = rs.getString("designation");
                r.section = rs.getString("section");
                r.dailyRate = rs.getDouble("daily_rate");
                r.bankName = rs.getString("bank_name");
                r.accountNumber = rs.getString("account_number");
                r.nic = rs.getString("nic");
                r.resigned = rs.getDate("resignation_date") != null;
                r.resignDate = rs.getString("resignation_date");
                allRows.add(r);
            }
        } catch (SQLException e) { e.printStackTrace(); }

        // Set allRows into TableView
        empTable.setItems(allRows);

        // Update summary labels
        lblTotal.setText(String.valueOf(allRows.size()));
        lblActive.setText(String.valueOf(allRows.stream().filter(r -> !r.resigned).count()));
        lblResigned.setText(String.valueOf(allRows.stream().filter(r -> r.resigned).count()));
    }

    // Filters employee rows based on selected section and active/resigned status
    @FXML private void handleFilter() {
        String sec = cmbSection.getValue();
        String act = cmbActiveFilter.getValue();
        List<EmpRow> f = allRows.stream()
                .filter(r -> "All".equals(sec) || sec.equals(r.section))
                .filter(r -> "All".equals(act)
                        || ("Active Only".equals(act) && !r.resigned)
                        || ("Resigned Only".equals(act) && r.resigned))
                .collect(Collectors.toList());
        empTable.setItems(FXCollections.observableArrayList(f));
    }

    // Resets filters and restores all employee rows in the TableView
    @FXML private void handleReset() {
        cmbSection.setValue("All");
        cmbActiveFilter.setValue("All");
        empTable.setItems(allRows);
    }

    // Exports the currently displayed employee rows to a PDF file
    @FXML private void handleExportPdf() {
        String[] h = {"ID","Name","Designation","Section","Daily Rate","Bank","Account No","NIC","Status"};
        List<String[]> d = new ArrayList<>();
        for (EmpRow r : empTable.getItems()) d.add(new String[]{
                String.valueOf(r.empId),
                r.fullName+(r.resigned?" [RESIGNED]":""),r.designation,r.section,
                String.format("%.0f",r.dailyRate),
                r.bankName!=null?r.bankName:"",
                r.accountNumber!=null?r.accountNumber:"",
                r.nic!=null?r.nic:"",r.resigned?"RESIGNED":"Active"
        });
        PdfExporter.export("Employee Report", h, d, empTable.getScene().getWindow());
    }

    // Exports the currently displayed employee rows to an Excel file
    @FXML private void handleExportExcel() {
        String[] h = {"ID","Name","Designation","Section","Daily Rate","Bank","Account No","NIC","Status","Resign Date"};
        List<String[]> d = new ArrayList<>();
        for (EmpRow r : empTable.getItems()) d.add(new String[]{
                String.valueOf(r.empId),
                r.fullName,r.designation,r.section,String.format("%.2f",r.dailyRate),
                r.bankName!=null?r.bankName:"",r.accountNumber!=null?r.accountNumber:"",
                r.nic!=null?r.nic:"",
                r.resigned?"RESIGNED":"Active",r.resignDate!=null?r.resignDate:""
        });
        ExcelExporter.export("Employees", h, d, empTable.getScene().getWindow());
    }

    // Inner class representing a single row of employee data
    public static class EmpRow {
        public int empId;
        public String fullName, designation, section, bankName, accountNumber, nic, resignDate;
        public double dailyRate;
        public boolean resigned;
    }
}