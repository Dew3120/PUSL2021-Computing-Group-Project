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

// OOP Concepts Used:
// Encapsulation - Private variables like timeline and secondsLeft are controlled within the class
// Abstraction - Session timeout logic is encapsulated in methods like initialize() and forceLogout()
// Polymorphism - Lambda expression inside Timeline (e -> {...}) behaves dynamically at runtime
// Inheritance - Uses JavaFX classes like Timeline, Label, and Stage which inherit from base classes

public class SessionTimeoutController {

    // Label used to display countdown timer to the user
    @FXML private Label countdownLabel;

    // Timeline object used to handle countdown execution every second
    private Timeline timeline;

    // Stores remaining seconds before session expires
    private int secondsLeft = 60;

    // Initializes the session timeout countdown when the UI loads
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

    // Handles user action to stay logged in by resetting session activity and stopping countdown
    @FXML
    private void handleStayLoggedIn() {
        SessionManager.updateActivity();
        timeline.stop();
        ((Stage) countdownLabel.getScene().getWindow()).close();
    }

    // Handles user action to manually log out
    @FXML
    private void handleLogout() {
        forceLogout();
    }

    // Forces logout when session expires or user chooses to log out
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
