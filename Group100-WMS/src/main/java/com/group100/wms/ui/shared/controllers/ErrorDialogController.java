package com.group100.wms.ui.shared.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

// OOP Concepts Used:
// Encapsulation - UI components (Label, TextArea) are kept private and accessed through methods
// Abstraction - Error display logic is hidden inside setError() method
// Polymorphism - JavaFX handles event methods like handleClose() dynamically at runtime
// Inheritance - Uses JavaFX classes like Label, TextArea, and Stage which inherit from core JavaFX classes

public class ErrorDialogController {

    // Label used to display the main error message
    @FXML private Label messageLabel;

    // TextArea used to display detailed error information (optional)
    @FXML private TextArea detailArea;

    // Sets the error message and optional detail text in the dialog
    public void setError(String message, String detail) {
        messageLabel.setText(message);
        if (detail != null && !detail.isBlank()) {
            detailArea.setText(detail);
            detailArea.setVisible(true);
        } else {
            detailArea.setVisible(false);
        }
    }

    // Handles closing the error dialog window
    @FXML
    private void handleClose() {
        ((Stage) messageLabel.getScene().getWindow()).close();
    }
}
