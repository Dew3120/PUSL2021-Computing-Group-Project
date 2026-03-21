package com.group100.wms.ui.shared.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ConfirmDialogController {

    @FXML private Label messageLabel;
    private boolean confirmed = false;

    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    @FXML
    private void handleYes() {
        confirmed = true;
        ((Stage) messageLabel.getScene().getWindow()).close();
    }

    @FXML
    private void handleNo() {
        confirmed = false;
        ((Stage) messageLabel.getScene().getWindow()).close();
    }
}