package com.group100.wms.ui.shared.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

// OOP Concepts Used:
// Encapsulation - Variables and methods are contained within this controller class.
// Association - This controller interacts with JavaFX Label and Stage components for UI handling.

public class ConfirmDialogController {

    // Label used to display the confirmation message
    @FXML private Label messageLabel;

    // Boolean to track whether the user confirmed the action
    private boolean confirmed = false;

    // Sets the message text to display in the confirmation dialog
    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    // Returns whether the user confirmed the action
    public boolean isConfirmed() {
        return confirmed;
    }

    // Handles the Yes button action, marks confirmed as true, and closes the dialog
    @FXML
    private void handleYes() {
        confirmed = true;
        ((Stage) messageLabel.getScene().getWindow()).close();
    }

    // Handles the No button action, marks confirmed as false, and closes the dialog
    @FXML
    private void handleNo() {
        confirmed = false;
        ((Stage) messageLabel.getScene().getWindow()).close();
    }
}
