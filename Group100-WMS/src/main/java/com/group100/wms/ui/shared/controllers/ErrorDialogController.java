package com.group100.wms.ui.shared.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ErrorDialogController {

    @FXML private Label messageLabel;
    @FXML private TextArea detailArea;

    public void setError(String message, String detail) {
        messageLabel.setText(message);
        if (detail != null && !detail.isBlank()) {
            detailArea.setText(detail);
            detailArea.setVisible(true);
        } else {
            detailArea.setVisible(false);
        }
    }

    @FXML
    private void handleClose() {
        ((Stage) messageLabel.getScene().getWindow()).close();
    }
}