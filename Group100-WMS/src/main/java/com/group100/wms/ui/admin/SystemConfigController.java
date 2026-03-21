package com.group100.wms.ui.admin;

import com.group100.wms.core.AppConfig;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class SystemConfigController {

    @FXML private Label appNameLabel;
    @FXML private Label appVersionLabel;
    @FXML private Label dbHostLabel;
    @FXML private Label dbNameLabel;
    @FXML private Label sessionTimeoutLabel;
    @FXML private Label lowStockLabel;
    @FXML private Label epfEmployeeLabel;
    @FXML private Label epfEmployerLabel;
    @FXML private Label etfLabel;
    @FXML private Label overtimeLabel;

    @FXML
    public void initialize() {
        appNameLabel.setText(AppConfig.APP_NAME);
        appVersionLabel.setText(AppConfig.APP_VERSION);
        dbHostLabel.setText(AppConfig.DB_HOST + ":" + AppConfig.DB_PORT);
        dbNameLabel.setText(AppConfig.DB_NAME);
        sessionTimeoutLabel.setText(AppConfig.SESSION_TIMEOUT_MINUTES + " minutes");
        lowStockLabel.setText(String.valueOf(AppConfig.LOW_STOCK_THRESHOLD));
        epfEmployeeLabel.setText((AppConfig.EPF_EMPLOYEE_RATE * 100) + "%");
        epfEmployerLabel.setText((AppConfig.EPF_EMPLOYER_RATE * 100) + "%");
        etfLabel.setText((AppConfig.ETF_RATE * 100) + "%");
        overtimeLabel.setText(AppConfig.OVERTIME_RATE + "x");
    }
}