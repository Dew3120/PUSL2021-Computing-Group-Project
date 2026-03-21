package com.group100.wms.ui.admin;

import com.group100.wms.exception.DatabaseException;
import com.group100.wms.exception.UnauthorizedAccessException;
import com.group100.wms.repository.UserRepository;
import com.group100.wms.service.UserService;
import com.group100.wms.ui.shared.MainLayoutController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class UserFormController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private TextField employeeIdField;
    @FXML private Label statusLabel;

    private final UserService userService = new UserService(new UserRepository());

    private static final String[] ROLE_NAMES = {
            "1 - ADMIN",
            "2 - WAREHOUSE_MANAGER",
            "3 - SUPERVISOR",
            "4 - ACCOUNTANT",
            "5 - SENIOR_MANAGER"
    };

    @FXML
    public void initialize() {
        roleCombo.getItems().addAll(ROLE_NAMES);
        roleCombo.setValue(ROLE_NAMES[2]); // Default to Supervisor
    }

    @FXML
    private void handleSave() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String roleSelection = roleCombo.getValue();
        String empIdText = employeeIdField.getText().trim();

        if (username.isBlank() || password.isBlank()
                || roleSelection == null || empIdText.isBlank()) {
            statusLabel.setText("Please fill all fields.");
            statusLabel.setStyle("-fx-text-fill: red;");
            return;
        }
        try {
            int roleId = Integer.parseInt(roleSelection.substring(0, 1));
            int empId = Integer.parseInt(empIdText);
            userService.createUser(username, password, roleId, empId);
            statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            statusLabel.setText("User created successfully: " + username);
            handleClear();
        } catch (NumberFormatException e) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Invalid employee ID.");
        } catch (DatabaseException | UnauthorizedAccessException e) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        usernameField.clear();
        passwordField.clear();
        roleCombo.setValue(ROLE_NAMES[2]);
        employeeIdField.clear();
    }

    @FXML
    private void handleBack() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            MainLayoutController controller =
                    (MainLayoutController) stage.getScene().getUserData();
            if (controller != null) {
                controller.loadView("/fxml/admin/UserManagement.fxml");
            }
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }
}