package com.group100.wms.ui.auth;

import com.group100.wms.exception.AuthenticationException;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.User;
import com.group100.wms.repository.UserRepository;
import com.group100.wms.service.AuthService;
import com.group100.wms.ui.shared.MainLayoutController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

// OOP Concepts Used:
// Encapsulation - Variables and methods are contained within this controller class.
// Abstraction - AuthService hides the details of authentication logic and database access.
// Association - This controller interacts with AuthService, UserRepository, and JavaFX UI components.

public class LoginController {

    // TextField for entering username
    @FXML private TextField usernameField;

    // PasswordField for entering user password
    @FXML private PasswordField passwordField;

    // Label for displaying error or status messages
    @FXML private Label errorLabel;

    // Service object for handling authentication logic
    private final AuthService authService =
            new AuthService(new UserRepository());

    // Handles login button action, validates user, and loads corresponding dashboard
    @FXML
    private void handleLogin() {
        errorLabel.setText("");
        String username = usernameField.getText();
        String password = passwordField.getText();
        try {
            User user = authService.login(username, password);
            loadDashboard(user);
        } catch (AuthenticationException e) {
            errorLabel.setText(e.getMessage());
        } catch (DatabaseException e) {
            errorLabel.setText("Database error. Please try again.");
        }
    }

    // Loads the dashboard UI based on the user's role
    private void loadDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/shared/MainLayout.fxml"));
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Scene scene = new Scene(loader.load(), 1200, 750);
            scene.getStylesheets().addAll(
                    getClass().getResource("/css/global.css").toExternalForm(),
                    getClass().getResource("/css/dashboard.css").toExternalForm(),
                    getClass().getResource("/css/tables.css").toExternalForm(),
                    getClass().getResource("/css/forms.css").toExternalForm()
            );
            MainLayoutController controller = loader.getController();
            scene.setUserData(controller);
            stage.setScene(scene);
            stage.setMaximized(true);
            controller.loadView(resolveDashboardFxml(user.getRoleId()));
        } catch (IOException e) {
            errorLabel.setText("Failed to load dashboard.");
            System.err.println("[UI] Dashboard load error: " + e.getMessage());
        }
    }

    // Determines which dashboard FXML to load based on the user's role ID
    private String resolveDashboardFxml(int roleId) {
        return switch (roleId) {
            case 1 -> "/fxml/dashboard/AdminDashboard.fxml";
            case 2 -> "/fxml/dashboard/WarehouseManagerDashboard.fxml";
            case 3 -> "/fxml/dashboard/SupervisorDashboard.fxml";
            case 4 -> "/fxml/dashboard/AccountantDashboard.fxml";
            case 5 -> "/fxml/dashboard/SeniorManagerDashboard.fxml";
            default -> "/fxml/dashboard/AdminDashboard.fxml";
        };
    }
}
