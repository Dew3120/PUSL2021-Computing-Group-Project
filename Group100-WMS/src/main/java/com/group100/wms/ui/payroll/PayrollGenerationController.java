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

    @FXML private ComboBox<Integer> monthCombo;
    @FXML private ComboBox<Integer> yearCombo;
    @FXML private TableView<Payroll> payrollTable;
    @FXML private TableColumn<Payroll, Integer> colEmpId;
    @FXML private TableColumn<Payroll, Double> colBasic;
    @FXML private TableColumn<Payroll, Double> colOT;
    @FXML private TableColumn<Payroll, Double> colEpf;
    @FXML private TableColumn<Payroll, Double> colNet;
    @FXML private TableColumn<Payroll, String> colStatus;
    @FXML private Label statusLabel;

    private final PayrollService payrollService = new PayrollService(
            new PayrollRepository(), new EmployeeRepository(), new AttendanceRepository());

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