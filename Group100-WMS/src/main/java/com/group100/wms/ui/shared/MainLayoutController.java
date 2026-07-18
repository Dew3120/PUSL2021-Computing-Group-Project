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

public class MainLayoutController {

    @FXML private BorderPane rootPane;
    @FXML private VBox sidebarContainer;

    private Timeline sessionTimer;

    @FXML
    public void initialize() {
        try {
            FXMLLoader sidebarLoader = new FXMLLoader(
                    getClass().getResource("/fxml/shared/Sidebar.fxml"));
            sidebarContainer.getChildren().add(sidebarLoader.load());
        } catch (IOException e) {
            System.err.println("[UI] Failed to load sidebar: " + e.getMessage());
        }

        // Start session timeout checker - runs every 30 seconds
        sessionTimer = new Timeline(new KeyFrame(Duration.seconds(30), event -> {
            if (SessionManager.isSessionExpired()) {
                sessionTimer.stop();
                Platform.runLater(() -> handleSessionTimeout());
            }
        }));
        sessionTimer.setCycleCount(Timeline.INDEFINITE);
        sessionTimer.play();

        // Track mouse and keyboard activity to reset session timer
        rootPane.setOnMouseMoved(e -> SessionManager.updateActivity());
        rootPane.setOnMouseClicked(e -> SessionManager.updateActivity());
        rootPane.setOnKeyPressed(e -> SessionManager.updateActivity());
    }

    public void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            rootPane.setCenter(loader.load());
            SessionManager.updateActivity();
        } catch (IOException e) {
            System.err.println("[UI] Failed to load view: " + fxmlPath);
            System.err.println("[UI] Cause: " + e.getMessage());
            if (e.getCause() != null) {
                System.err.println("[UI] Root cause: " + e.getCause().getMessage());
                e.getCause().printStackTrace();
            }
        }
    }

    private void handleSessionTimeout() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Session Expired");
        alert.setHeaderText("Your session has expired");
        alert.setContentText("You have been inactive for 15 minutes. Please login again.");
        alert.showAndWait();

        SessionManager.logout();
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/auth/Login.fxml"));
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(SceneStyles.createScene(loader.load(), SceneStyles.LOGIN_WIDTH, SceneStyles.LOGIN_HEIGHT, getClass()));
            stage.setMaximized(false);
        } catch (IOException e) {
            System.err.println("[UI] Failed to navigate to login: " + e.getMessage());
        }
    }
}
