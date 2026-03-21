package com.group100.wms.ui.shared.controllers;

import com.group100.wms.core.SessionManager;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SessionTimeoutController {

    @FXML private Label countdownLabel;
    private Timeline timeline;
    private int secondsLeft = 60;

    @FXML
    public void initialize() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsLeft--;
            countdownLabel.setText("Session expires in " + secondsLeft + " seconds.");
            if (secondsLeft <= 0) forceLogout();
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @FXML
    private void handleStayLoggedIn() {
        SessionManager.updateActivity();
        timeline.stop();
        ((Stage) countdownLabel.getScene().getWindow()).close();
    }

    @FXML
    private void handleLogout() {
        forceLogout();
    }

    private void forceLogout() {
        timeline.stop();
        SessionManager.logout();
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/fxml/auth/Login.fxml"));
            Stage stage = (Stage) countdownLabel.getScene().getWindow();
            stage.setScene(new Scene(loader.load(), 900, 600));
            stage.setMaximized(false);
        } catch (Exception e) {
            System.err.println("[SESSION] Failed to redirect to login: " + e.getMessage());
        }
    }
}