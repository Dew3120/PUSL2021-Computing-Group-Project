package com.group100.wms.ui.dashboard;

import com.group100.wms.exception.DatabaseException;
import com.group100.wms.repository.*;
import com.group100.wms.service.DashboardService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.Map;

// OOP Concepts Used:
// Encapsulation - Variables and methods are encapsulated within this controller class.
// Abstraction - DashboardService hides the business logic and data retrieval details.
// Association - This controller interacts with DashboardService and JavaFX Label objects.

public class WarehouseManagerDashboardController {

    // Label used to display total number of items in warehouse
    @FXML private Label totalItemsLabel;

    // Label used to display count of low stock items
    @FXML private Label lowStockLabel;

    // Label used to display total number of GRNs
    @FXML private Label totalGrnsLabel;

    // Label used to display total number of GINs
    @FXML private Label totalGinsLabel;

    // Label used to display status or error messages
    @FXML private Label statusLabel;

    // Service object used to retrieve dashboard KPI data from repositories
    private final DashboardService dashboardService = new DashboardService(
            new ItemRepository(), new BatchRepository(), new GrnRepository(),
            new GinRepository(), new AttendanceRepository(), new PayrollRepository());

    // Automatically called when the FXML controller is loaded
    @FXML
    public void initialize() { loadKpis(); }

    // Loads KPI values and updates dashboard labels
    private void loadKpis() {
        try {
            Map<String, Object> kpis = dashboardService.getAdminKpis();
            totalItemsLabel.setText(String.valueOf(kpis.get("totalItems")));
            totalGrnsLabel.setText(String.valueOf(kpis.get("totalGrns")));
            totalGinsLabel.setText(String.valueOf(kpis.get("totalGins")));

            // Show actual low stock count if available, otherwise set to 0
            if (kpis.containsKey("lowStockCount")) {
                lowStockLabel.setText(String.valueOf(kpis.get("lowStockCount")));
            } else {
                lowStockLabel.setText("0");
            }

            statusLabel.setText("");
        } catch (DatabaseException e) {
            statusLabel.setText("Error loading KPIs: " + e.getMessage());
        }
    }

    // Refresh action to reload KPI data
    @FXML private void handleRefresh() { loadKpis(); }
}
