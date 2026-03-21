package com.group100.wms.ui.dashboard;

import com.group100.wms.exception.DatabaseException;
import com.group100.wms.repository.*;
import com.group100.wms.service.DashboardService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.util.Map;

public class AccountantDashboardController {

    @FXML private Label payrollCountLabel;
    @FXML private Label attendanceCountLabel;
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
            LocalDate now = LocalDate.now();
            Map<String, Object> kpis =
                    dashboardService.getAccountantKpis(now.getMonthValue(), now.getYear());
            payrollCountLabel.setText(String.valueOf(kpis.get("payrollCount")));
            attendanceCountLabel.setText(String.valueOf(kpis.get("attendanceCount")));
        } catch (DatabaseException e) {
            statusLabel.setText("Error loading KPIs: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        loadKpis();
    }
}