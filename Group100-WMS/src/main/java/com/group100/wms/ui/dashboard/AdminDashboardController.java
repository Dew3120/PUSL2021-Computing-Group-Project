package com.group100.wms.ui.dashboard;

import com.group100.wms.exception.DatabaseException;
import com.group100.wms.repository.*;
import com.group100.wms.service.DashboardService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.util.Map;

// OOP Concepts Used:
// Encapsulation - Variables and methods are organized inside this controller class.
// Abstraction - DashboardService hides data processing and business logic.
// Association - This controller interacts with DashboardService and JavaFX Label components.

public class AdminDashboardController {

    // Label used to display total number of items
    @FXML private Label totalItemsLabel;

    // Label used to display total number of GRNs
    @FXML private Label totalGrnsLabel;

    // Label used to display total number of GINs
    @FXML private Label totalGinsLabel;

    // Label used to display payroll amount for the current month
    @FXML private Label payrollThisMonthLabel;

    // Label used to display status or error messages
    @FXML private Label statusLabel;

    // Service object used to retrieve dashboard KPI data
    private final DashboardService dashboardService = new DashboardService(
            new ItemRepository(), new BatchRepository(), new GrnRepository(),
            new GinRepository(), new AttendanceRepository(), new PayrollRepository());

    // Automatically called when the FXML view is loaded
    @FXML
    public void initialize() {
        loadKpis();
    }

    // Loads KPI values and updates dashboard labels
    private void loadKpis() {
        try {
            Map<String, Object> kpis = dashboardService.getAdminKpis();
            totalItemsLabel.setText(String.valueOf(kpis.get("totalItems")));
            totalGrnsLabel.setText(String.valueOf(kpis.get("totalGrns")));
            totalGinsLabel.setText(String.valueOf(kpis.get("totalGins")));
            payrollThisMonthLabel.setText(String.valueOf(kpis.get("payrollThisMonth")));
        } catch (DatabaseException e) {
            statusLabel.setText("Error loading KPIs: " + e.getMessage());
        }
    }

    // Refresh action to reload KPI values
    @FXML
    private void handleRefresh() {
        loadKpis();
    }
}
