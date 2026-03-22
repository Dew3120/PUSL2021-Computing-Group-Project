// =============================================================================
// PayrollReportController.java
// Part of: Centralized Apparel Warehouse Management System (WMS)
// Module: Payroll UI — Report View
//
// OOP CONCEPTS USED IN THIS CLASS:
// - ENCAPSULATION: All @FXML fields are private; allRows and helper methods
//   are private to this class. The inner PayRow class groups related payroll
//   fields into a single data object, bundling data together cleanly.
// - ABSTRACTION: PdfExporter and ExcelExporter abstract away all file-writing
//   logic. DatabaseConnection abstracts the JDBC connection setup. This
//   controller only concerns itself with UI and data wiring.
// - INHERITANCE: TableRow and TableCell are subclassed anonymously inside
//   setRowFactory() and setCellFactory() to override updateItem() — a classic
//   use of inheritance to customize JavaFX rendering behavior.
// - POLYMORPHISM: updateItem() is overridden in both the anonymous TableRow
//   and TableCell subclasses, demonstrating runtime polymorphism where the
//   JavaFX framework calls the overridden version at runtime.
// =============================================================================

package com.group100.wms.ui.payroll;

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

public class PayrollReportController {

    // Labels that display KPI summary values at the top of the report:
    // total number of records, total net salary payout, and total EPF employer contribution
    @FXML private Label lblTotal, lblNetTotal, lblEpfTotal;

    // Dropdowns for filtering the payroll table by department section, month, and year
    @FXML private ComboBox<String> cmbSection, cmbMonth, cmbYear;

    // The main table that displays all payroll records as PayRow objects
    @FXML private TableView<PayRow> payrollTable;

    // Numeric columns: Employee ID, Month, Year, Base Salary, Overtime,
    // Total Deductions, EPF Employer contribution, ETF, and Net Salary
    @FXML private TableColumn<PayRow, Number> colEmpId, colMonth, colYear, colBase, colOT, colDeductions, colEpf, colEtf, colNet;

    // String columns: Employee full name and their warehouse section
    @FXML private TableColumn<PayRow, String> colName, colSection;

    // Master list of all payroll rows loaded from the database.
    // Kept separate so filters can always operate on the complete dataset.
    private ObservableList<PayRow> allRows;

    // Called automatically by JavaFX after all @FXML fields are injected.
    // Binds each table column to its corresponding PayRow field,
    // sets up custom row/cell rendering to highlight resigned employees,
    // and triggers the initial database load.
    @FXML
    public void initialize() {
        colEmpId.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().empId));
        colName.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().empName));
        colSection.setCellValueFactory(cd -> new SimpleStringProperty(cd.getValue().section));
        colMonth.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().month));
        colYear.setCellValueFactory(cd -> new SimpleIntegerProperty(cd.getValue().year));
        colBase.setCellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().baseSalary));
        colOT.setCellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().overtime));
        colDeductions.setCellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().deductions));
        colEpf.setCellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().epfEmployer));
        colEtf.setCellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().etf));
        colNet.setCellValueFactory(cd -> new SimpleDoubleProperty(cd.getValue().netSalary));

        payrollTable.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(PayRow item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setStyle(""); return; }
                if (item.resigned) {
                    setStyle("-fx-background-color: #fce4e4;");
                } else {
                    setStyle("");
                }
            }
        });

        colName.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                PayRow row = getTableView().getItems().get(getIndex());
                if (row.resigned) {
                    setText(item + " [RESIGNED]");
                    setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;");
                } else {
                    setText(item);
                    setStyle("");
                }
            }
        });

        loadData();
    }

    // Fetches all payroll records from the database by joining the payroll
    // and employees tables. Populates allRows and the table, then initializes
    // the filter dropdowns and updates the KPI summary labels.
    private void loadData() {
        allRows = FXCollections.observableArrayList();
        String sql = "SELECT p.*, e.full_name, e.section, e.resignation_date " +
                "FROM payroll p JOIN employees e ON p.employee_id = e.employee_id " +
                "ORDER BY p.year DESC, p.month DESC, e.full_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                PayRow r = new PayRow();
                r.empId = rs.getInt("employee_id");
                r.empName = rs.getString("full_name");
                r.section = rs.getString("section");
                r.month = rs.getInt("month");
                r.year = rs.getInt("year");
                r.baseSalary = rs.getDouble("base_salary");
                r.overtime = rs.getDouble("overtime");
                r.deductions = rs.getDouble("deductions");
                r.epfEmployer = rs.getDouble("epf_employer");
                r.etf = rs.getDouble("etf");
                r.netSalary = rs.getDouble("net_salary");
                r.resigned = rs.getDate("resignation_date") != null;
                allRows.add(r);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        payrollTable.setItems(allRows);
        updateKPIs(allRows);

        cmbSection.setItems(FXCollections.observableArrayList("All", "WMS-1", "WMS-2", "WMS-3", "WMS-4"));
        cmbSection.setValue("All");
        cmbMonth.setItems(FXCollections.observableArrayList("All","1","2","3","4","5","6","7","8","9","10","11","12"));
        cmbMonth.setValue("All");
        cmbYear.setItems(FXCollections.observableArrayList("All","2025","2026"));
        cmbYear.setValue("All");
    }

    // Recalculates and updates the three KPI labels based on the currently visible rows.
    // Shows: total record count, sum of net salaries, and sum of EPF employer contributions.
    private void updateKPIs(List<PayRow> rows) {
        lblTotal.setText(String.valueOf(rows.size()));
        double netSum = rows.stream().mapToDouble(r -> r.netSalary).sum();
        double epfSum = rows.stream().mapToDouble(r -> r.epfEmployer).sum();
        lblNetTotal.setText(formatAmount(netSum));
        lblEpfTotal.setText(formatAmount(epfSum));
    }

    // Converts a raw double amount into a human-readable currency string.
    // Formats as millions (M) or thousands (K) where appropriate, otherwise plain rupees.
    private String formatAmount(double amount) {
        if (amount >= 1_000_000) {
            return String.format("Rs. %.2fM", amount / 1_000_000);
        } else if (amount >= 1_000) {
            return String.format("Rs. %.1fK", amount / 1_000);
        }
        return String.format("Rs. %.0f", amount);
    }

    // Triggered when the user clicks the "Filter" button.
    // Applies the selected section, month, and year filters to allRows using streams,
    // then updates the table and KPI labels with only the matching records.
    @FXML private void handleFilter() {
        String sec = cmbSection.getValue(); String mo = cmbMonth.getValue(); String yr = cmbYear.getValue();
        List<PayRow> f = allRows.stream()
                .filter(r -> "All".equals(sec) || sec.equals(r.section))
                .filter(r -> "All".equals(mo) || r.month == Integer.parseInt(mo))
                .filter(r -> "All".equals(yr) || r.year == Integer.parseInt(yr))
                .collect(Collectors.toList());
        payrollTable.setItems(FXCollections.observableArrayList(f));
        updateKPIs(f);
    }

    // Triggered when the user clicks the "Reset" button.
    // Resets all filter dropdowns to "All" and restores the full unfiltered dataset.
    @FXML private void handleReset() { cmbSection.setValue("All"); cmbMonth.setValue("All"); cmbYear.setValue("All"); payrollTable.setItems(allRows); updateKPIs(allRows); }

    // Triggered when the user clicks "Export PDF".
    // Builds a header array and a list of string arrays from the currently visible
    // table rows, then delegates to PdfExporter to write and save the PDF file.
    @FXML private void handleExportPdf() {
        String[] h = {"Emp#","Name","Section","Month","Year","Base","OT","Deductions","EPF","ETF","Net"};
        List<String[]> d = new ArrayList<>();
        for (PayRow r : payrollTable.getItems()) d.add(new String[]{String.valueOf(r.empId),r.empName+(r.resigned?" [RESIGNED]":""),r.section,String.valueOf(r.month),String.valueOf(r.year),
                String.format("%.0f",r.baseSalary),String.format("%.0f",r.overtime),String.format("%.0f",r.deductions),String.format("%.0f",r.epfEmployer),String.format("%.0f",r.etf),String.format("%.0f",r.netSalary)});
        PdfExporter.export("Payroll Report", h, d, payrollTable.getScene().getWindow());
    }

    // Triggered when the user clicks "Export Excel".
    // Builds a header array and a list of string arrays from the currently visible
    // table rows (with 2 decimal places for salary values), then delegates to ExcelExporter.
    @FXML private void handleExportExcel() {
        String[] h = {"Emp#","Name","Section","Month","Year","Base","OT","Deductions","EPF","ETF","Net"};
        List<String[]> d = new ArrayList<>();
        for (PayRow r : payrollTable.getItems()) d.add(new String[]{String.valueOf(r.empId),r.empName,r.section,String.valueOf(r.month),String.valueOf(r.year),
                String.format("%.2f",r.baseSalary),String.format("%.2f",r.overtime),String.format("%.2f",r.deductions),String.format("%.2f",r.epfEmployer),String.format("%.2f",r.etf),String.format("%.2f",r.netSalary)});
        ExcelExporter.export("Payroll", h, d, payrollTable.getScene().getWindow());
    }

    // Inner data model class representing a single row in the payroll report table.
    // Groups all fields for one employee's payroll record into a single object.
    // The 'resigned' flag is derived from whether the employee has a resignation_date in the DB.
    public static class PayRow {
        // Employee ID and the month/year this payroll record belongs to
        public int empId, month, year;

        // Employee's full name and their assigned warehouse section
        public String empName, section;

        // Salary breakdown: base pay, overtime earned, total deductions,
        // employer EPF contribution, ETF contribution, and final net salary
        public double baseSalary, overtime, deductions, epfEmployer, etf, netSalary;

        // True if the employee has a resignation date on record — used to visually
        // flag their rows in red and append "[RESIGNED]" to their name in the table
        public boolean resigned;
    }
}