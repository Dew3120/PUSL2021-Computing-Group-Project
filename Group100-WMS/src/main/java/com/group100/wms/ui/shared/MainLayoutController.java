package com.group100.wms.ui.shared;

import com.group100.wms.core.SessionManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;

// OOP Concepts:
// 1. Composition: The MainLayout "has-a" Sidebar and "has-a" Center View.
// 2. Observer Pattern (Implicit): It monitors user activity (mouse/keyboard) to reset the session.
// 3. Centralized Control: All view switching logic is funneled through the loadView() method.
public class MainLayoutController {

    @FXML private BorderPane rootPane; // The main layout container
    @FXML private VBox sidebarContainer; // Container for the navigation menu

    private Timeline sessionTimer;

    @FXML
    public void initialize() {
        // Step 1: Dynamically load the Sidebar into the left side of the BorderPane
        try {
            FXMLLoader sidebarLoader = new FXMLLoader(
                    getClass().getResource("/fxml/shared/Sidebar.fxml"));
            sidebarContainer.getChildren().add(sidebarLoader.load());
        } catch (IOException e) {
            System.err.println("[UI] Failed to load sidebar: " + e.getMessage());
        }

        // Step 2: Security - Session Timeout Checker
        // Every 30 seconds, it asks SessionManager if the user has been idle too long
        sessionTimer = new Timeline(new KeyFrame(Duration.seconds(30), event -> {
            if (SessionManager.isSessionExpired()) {
                sessionTimer.stop();
                Platform.runLater(() -> handleSessionTimeout());
            }
        }));
        sessionTimer.setCycleCount(Timeline.INDEFINITE);
        sessionTimer.play();

        // Step 3: Activity Tracking
        // Any movement or click resets the "Last Activity" timestamp in SessionManager
        rootPane.setOnMouseMoved(e -> SessionManager.updateActivity());
        rootPane.setOnMouseClicked(e -> SessionManager.updateActivity());
        rootPane.setOnKeyPressed(e -> SessionManager.updateActivity());
    }

    /**
     * The heart of the navigation system. 
     * Instead of opening new windows, this replaces the 'Center' of the BorderPane.
     */
    public void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            rootPane.setCenter(loader.load());
            SessionManager.updateActivity(); // View change counts as activity
        } catch (IOException e) {
            // Robust error logging for debugging FXML issues
            System.err.println("[UI] Failed to load view: " + fxmlPath);
            e.printStackTrace();
        }
    }

    // Forces a logout and redirects to the Login screen if the user is idle for 15+ mins
    private void handleSessionTimeout() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Session Expired");
        alert.setHeaderText("Security Timeout");
        alert.setContentText("You have been inactive for 15 minutes. For security, please login again.");
        alert.showAndWait();

        SessionManager.logout();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/auth/Login.fxml"));
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 900, 600));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
