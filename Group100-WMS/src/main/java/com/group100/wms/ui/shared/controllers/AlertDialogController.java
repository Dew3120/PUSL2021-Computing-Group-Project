package com.group100.wms.ui.shared.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

// OOP Concepts Used:
// Encapsulation - Variables and methods are encapsulated within this controller class.
// Association - This controller interacts with JavaFX Label and Stage components for UI handling.

public class AlertDialogController {

    // Label used to display the alert message
    @FXML private Label messageLabel;

    // Sets the message text to display in the alert dialog
    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    // Handles the OK button action to close the alert dialog window
    @FXML
    private void handleOk() {
        ((Stage) messageLabel.getScene().getWindow()).close();
    }
}
