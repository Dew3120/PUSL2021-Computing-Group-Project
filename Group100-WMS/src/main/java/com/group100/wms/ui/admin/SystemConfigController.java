package com.group100.wms.ui.admin;

import com.group100.wms.core.AppConfig;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

// OOP Concepts Used:
// Encapsulation - UI components and logic are encapsulated within this controller class.
// Abstraction - Uses AppConfig class to abstract application configuration details.
// Inheritance - Extends functionality of JavaFX Label components indirectly via composition.
// Polymorphism - Uses method overriding in the JavaFX lifecycle (initialize method is called by the framework).

public class SystemConfigController {

    // Label to display the application name
    @FXML private Label appNameLabel;

    // Label to display the application version
    @FXML private Label appVersionLabel;

    // Label to display the database host and port
    @FXML private Label dbHostLabel;

    // Label to display the database name
    @FXML private Label dbNameLabel;

    // Label to display session timeout in minutes
    @FXML private Label sessionTimeoutLabel;

    // Label to display the low stock threshold for inventory
    @FXML private Label lowStockLabel;

    // Label to display the employee EPF contribution rate
    @FXML private Label epfEmployeeLabel;

    // Label to display the employer EPF contribution rate
    @FXML private Label epfEmployerLabel;

    // Label to display the ETF contribution rate
    @FXML private Label etfLabel;

    // Label to display the overtime multiplier rate
    @FXML private Label overtimeLabel;

    // Initializes the system configuration labels with values from AppConfig
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