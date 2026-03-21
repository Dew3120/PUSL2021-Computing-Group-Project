package com.group100.wms.ui.dashboard;

import com.group100.wms.exception.DatabaseException;
import com.group100.wms.repository.*;
import com.group100.wms.service.DashboardService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.Map;

public class SeniorManagerDashboardController {

    @FXML private Label totalItemsLabel;
    @FXML private Label totalGrnsLabel;
    @FXML private Label totalGinsLabel;
    @FXML private Label statusLabel;

    private final DashboardService dashboardService = new DashboardService(
            new ItemRepository(), new BatchRepository(), new GrnRepository(),
            new GinRepository(), new AttendanceRepository(), new PayrollRepository());

    @FXML
    public void initialize() {
        loadKpis();
    }

    private void loadKpis() {
        try {
            Map<String, Object> kpis = dashboardService.getAdminKpis();
            totalItemsLabel.setText(String.valueOf(kpis.get("totalItems")));
            totalGrnsLabel.setText(String.valueOf(kpis.get("totalGrns")));
            totalGinsLabel.setText(String.valueOf(kpis.get("totalGins")));
        } catch (DatabaseException e) {
            statusLabel.setText("Error loading KPIs: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        loadKpis();
    }
}