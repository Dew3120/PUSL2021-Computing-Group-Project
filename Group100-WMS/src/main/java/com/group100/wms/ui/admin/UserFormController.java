package com.group100.wms.ui.admin;

import com.group100.wms.exception.DatabaseException;
import com.group100.wms.exception.UnauthorizedAccessException;
import com.group100.wms.repository.UserRepository;
import com.group100.wms.service.UserService;
import com.group100.wms.ui.shared.MainLayoutController;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

/*
 OOP Concepts Used:
 - Encapsulation: Data (fields like username, password, role, etc.) and methods are bundled inside this class.
 - Abstraction: Interaction with UserService hides complex business logic and database operations.
 - Inheritance: This controller indirectly uses inheritance through JavaFX classes (e.g., controls like TextField, Label).
 - Polymorphism: Exception handling (DatabaseException, UnauthorizedAccessException) demonstrates polymorphic behavior.
*/

public class UserFormController {

    // TextField to input the username
    @FXML private TextField usernameField;

    // PasswordField to input the user's password securely
    @FXML private PasswordField passwordField;

    // ComboBox to select the user role
    @FXML private ComboBox<String> roleCombo;

    // TextField to input employee ID
    @FXML private TextField employeeIdField;

    // Label to display status messages (success or error)
    @FXML private Label statusLabel;

    // Service layer object used to handle user-related business logic and database interaction
    private final UserService userService = new UserService(new UserRepository());

    // Array storing available role names with corresponding IDs
    private static final String[] ROLE_NAMES = {
            "1 - ADMIN",
            "2 - WAREHOUSE_MANAGER",
            "3 - SUPERVISOR",
            "4 - ACCOUNTANT",
            "5 - SENIOR_MANAGER"
    };

    // Initializes the form by populating the role dropdown and setting a default value
    @FXML
    public void initialize() {
        roleCombo.getItems().addAll(ROLE_NAMES);
        roleCombo.setValue(ROLE_NAMES[2]); // Default to Supervisor
    }

    // Handles the save button action: validates input and creates a new user
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

    // Clears all input fields and resets the form to default state
    @FXML
    private void handleClear() {
        usernameField.clear();
        passwordField.clear();
        roleCombo.setValue(ROLE_NAMES[2]);
        employeeIdField.clear();
    }

    // Handles navigation back to the User Management view
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