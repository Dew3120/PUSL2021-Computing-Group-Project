package com.group100.wms.ui.dashboard;

import com.group100.wms.exception.DatabaseException;
import com.group100.wms.repository.*;
import com.group100.wms.service.DashboardService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.util.Map;

// OOP Concepts Used:
// Encapsulation - Variables and methods are contained inside this controller class.
// Abstraction - DashboardService hides business logic details from the controller.
// Association - This controller uses DashboardService and Label objects.

public class AccountantDashboardController {

    // Label used to display payroll count value in dashboard
    @FXML private Label payrollCountLabel;

    // Label used to display attendance count value in dashboard
    @FXML private Label attendanceCountLabel;

    // Label used to display status or error messages
    @FXML private Label statusLabel;

    // Service object used to fetch dashboard KPI data from repositories
    private final DashboardService dashboardService = new DashboardService(
            new ItemRepository(), new BatchRepository(), new GrnRepository(),
            new GinRepository(), new AttendanceRepository(), new PayrollRepository());

    // Automatically called when the FXML controller is loaded
    @FXML
    public void initialize() {
        loadKpis();
    }

    // Loads KPI values and updates dashboard labels
    private void loadKpis() {
        try {
            LocalDate now = LocalDate.now();
            Map<String, Object> kpis =
                    dashboardService.getAccountantKpis(now.getMonthValue(), now.getYear());
            payrollCountLabel.setText(String.valueOf(kpis.get("payrollCount")));
            attendanceCountLabel.setText(String.valueOf(kpis.get("attendanceCount")));
        } catch (DatabaseException e) {
            statusLabel.setText("Error loading KPIs: " + e.getMessage());
        }
    }

    // Refresh button action to reload KPI data
    @FXML
    private void handleRefresh() {
        loadKpis();
    }
}
