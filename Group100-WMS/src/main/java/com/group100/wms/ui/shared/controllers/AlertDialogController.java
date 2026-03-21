package com.group100.wms.ui.shared.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class AlertDialogController {

    @FXML private Label messageLabel;

    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    @FXML
    private void handleOk() {
        ((Stage) messageLabel.getScene().getWindow()).close();
    }
}