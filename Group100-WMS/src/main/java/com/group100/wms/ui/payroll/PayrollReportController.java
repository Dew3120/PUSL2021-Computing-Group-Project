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

    @FXML private Label lblTotal, lblNetTotal, lblEpfTotal;
    @FXML private ComboBox<String> cmbSection, cmbMonth, cmbYear;
    @FXML private TableView<PayRow> payrollTable;
    @FXML private TableColumn<PayRow, Number> colEmpId, colMonth, colYear, colBase, colOT, colDeductions, colEpf, colEtf, colNet;
    @FXML private TableColumn<PayRow, String> colName, colSection;

    private ObservableList<PayRow> allRows;

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

    private void updateKPIs(List<PayRow> rows) {
        lblTotal.setText(String.valueOf(rows.size()));
        double netSum = rows.stream().mapToDouble(r -> r.netSalary).sum();
        double epfSum = rows.stream().mapToDouble(r -> r.epfEmployer).sum();
        lblNetTotal.setText(formatAmount(netSum));
        lblEpfTotal.setText(formatAmount(epfSum));
    }

    private String formatAmount(double amount) {
        if (amount >= 1_000_000) {
            return String.format("Rs. %.2fM", amount / 1_000_000);
        } else if (amount >= 1_000) {
            return String.format("Rs. %.1fK", amount / 1_000);
        }
        return String.format("Rs. %.0f", amount);
    }

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
    @FXML private void handleReset() { cmbSection.setValue("All"); cmbMonth.setValue("All"); cmbYear.setValue("All"); payrollTable.setItems(allRows); updateKPIs(allRows); }

    @FXML private void handleExportPdf() {
        String[] h = {"Emp#","Name","Section","Month","Year","Base","OT","Deductions","EPF","ETF","Net"};
        List<String[]> d = new ArrayList<>();
        for (PayRow r : payrollTable.getItems()) d.add(new String[]{String.valueOf(r.empId),r.empName+(r.resigned?" [RESIGNED]":""),r.section,String.valueOf(r.month),String.valueOf(r.year),
                String.format("%.0f",r.baseSalary),String.format("%.0f",r.overtime),String.format("%.0f",r.deductions),String.format("%.0f",r.epfEmployer),String.format("%.0f",r.etf),String.format("%.0f",r.netSalary)});
        PdfExporter.export("Payroll Report", h, d, payrollTable.getScene().getWindow());
    }
    @FXML private void handleExportExcel() {
        String[] h = {"Emp#","Name","Section","Month","Year","Base","OT","Deductions","EPF","ETF","Net"};
        List<String[]> d = new ArrayList<>();
        for (PayRow r : payrollTable.getItems()) d.add(new String[]{String.valueOf(r.empId),r.empName,r.section,String.valueOf(r.month),String.valueOf(r.year),
                String.format("%.2f",r.baseSalary),String.format("%.2f",r.overtime),String.format("%.2f",r.deductions),String.format("%.2f",r.epfEmployer),String.format("%.2f",r.etf),String.format("%.2f",r.netSalary)});
        ExcelExporter.export("Payroll", h, d, payrollTable.getScene().getWindow());
    }

    public static class PayRow {
        public int empId, month, year;
        public String empName, section;
        public double baseSalary, overtime, deductions, epfEmployer, etf, netSalary;
        public boolean resigned;
    }
}