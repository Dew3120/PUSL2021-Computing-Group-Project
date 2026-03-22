// =============================================================================
// PayrollGenerationController.java
// Part of: Centralized Apparel Warehouse Management System (WMS)
// Module: Payroll UI
//
// OOP CONCEPTS USED IN THIS CLASS:
// - ENCAPSULATION: All @FXML fields are private, and internal state (payrollService)
//   is kept private. Data is accessed only through controlled methods.
// - ABSTRACTION: PayrollService abstracts away all business logic (payroll
//   calculation, DB queries), so this controller only handles UI concerns.
// - POLYMORPHISM: Exception handling in handleGenerate() catches multiple exception
//   types (DatabaseException | PayrollCalculationException) under a shared catch block.
// - INHERITANCE: This controller follows the JavaFX controller pattern; FXML
//   annotation-based injection is a framework contract this class implicitly adheres to.
// =============================================================================

package com.group100.wms.ui.payroll;

import com.group100.wms.exception.DatabaseException;
import com.group100.wms.exception.PayrollCalculationException;
import com.group100.wms.model.Payroll;
import com.group100.wms.repository.AttendanceRepository;
import com.group100.wms.repository.EmployeeRepository;
import com.group100.wms.repository.PayrollRepository;
import com.group100.wms.service.PayrollService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;

public class PayrollGenerationController {

    // Dropdown for selecting the payroll month (1–12)
    @FXML private ComboBox<Integer> monthCombo;

    // Dropdown for selecting the payroll year
    @FXML private ComboBox<Integer> yearCombo;

    // Table that displays the generated or loaded payroll records
    @FXML private TableView<Payroll> payrollTable;

    // Column displaying the Employee ID for each payroll record
    @FXML private TableColumn<Payroll, Integer> colEmpId;

    // Column displaying the base/basic salary for each employee
    @FXML private TableColumn<Payroll, Double> colBasic;

    // Column displaying the overtime pay earned by the employee
    @FXML private TableColumn<Payroll, Double> colOT;

    // Column displaying the EPF (Employees' Provident Fund) deduction amount
    @FXML private TableColumn<Payroll, Double> colEpf;

    // Column displaying the net salary after all deductions and additions
    @FXML private TableColumn<Payroll, Double> colNet;

    // Column displaying the payroll status or generation timestamp
    @FXML private TableColumn<Payroll, String> colStatus;

    // Label used to show feedback messages (success/error) to the user
    @FXML private Label statusLabel;

    // Service layer object that handles all payroll business logic and database operations
    // Constructed with the three required repositories: Payroll, Employee, and Attendance
    private final PayrollService payrollService = new PayrollService(
            new PayrollRepository(), new EmployeeRepository(), new AttendanceRepository());

    // Called automatically by JavaFX after all @FXML fields are injected.
    // Sets up month/year dropdowns with default values and configures table column bindings.
    @FXML
    public void initialize() {
        monthCombo.getItems().addAll(1,2,3,4,5,6,7,8,9,10,11,12);
        yearCombo.getItems().addAll(2023, 2024, 2025, 2026);
        LocalDate now = LocalDate.now();
        monthCombo.setValue(now.getMonthValue());
        yearCombo.setValue(now.getYear());

        colEmpId.setCellValueFactory(d ->
                new SimpleIntegerProperty(d.getValue().getEmployeeId()).asObject());
        colBasic.setCellValueFactory(d ->
                new SimpleDoubleProperty(d.getValue().getBaseSalary()).asObject());
        colOT.setCellValueFactory(d ->
                new SimpleDoubleProperty(d.getValue().getOvertimePay()).asObject());
        colEpf.setCellValueFactory(d ->
                new SimpleDoubleProperty(d.getValue().getEpfEmployee()).asObject());
        colNet.setCellValueFactory(d ->
                new SimpleDoubleProperty(d.getValue().getNetSalary()).asObject());
        colStatus.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getGeneratedAt() != null
                                ? d.getValue().getGeneratedAt().toString() : "GENERATED"));
    }

    // Triggered when the user clicks the "Generate" button.
    // Calls the payroll service to calculate and save payroll for the selected month/year,
    // then populates the table with the results. Displays an error message if generation fails.
    @FXML
    private void handleGenerate() {
        try {
            List<Payroll> payrolls = payrollService.generatePayroll(
                    monthCombo.getValue(), yearCombo.getValue());
            payrollTable.setItems(FXCollections.observableArrayList(payrolls));
            statusLabel.setText("Generated payroll for " + payrolls.size() + " employees.");
        } catch (DatabaseException | PayrollCalculationException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    // Triggered when the user clicks the "Load" button.
    // Fetches existing payroll records for the selected month/year from the database
    // and displays them in the table. Displays an error message if the query fails.
    @FXML
    private void handleLoad() {
        try {
            List<Payroll> payrolls = payrollService.getPayrollByMonthYear(
                    monthCombo.getValue(), yearCombo.getValue());
            payrollTable.setItems(FXCollections.observableArrayList(payrolls));
            statusLabel.setText("Loaded " + payrolls.size() + " payroll records.");
        } catch (DatabaseException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }
}