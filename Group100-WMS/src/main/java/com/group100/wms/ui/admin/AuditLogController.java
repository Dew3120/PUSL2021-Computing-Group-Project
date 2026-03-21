package com.group100.wms.ui.admin;

import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.AuditLog;
import com.group100.wms.repository.AuditLogRepository;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class AuditLogController {

    @FXML private TableView<AuditLog> auditTable;
    @FXML private TableColumn<AuditLog, Integer> colId;
    @FXML private TableColumn<AuditLog, Integer> colUserId;
    @FXML private TableColumn<AuditLog, String> colAction;
    @FXML private TableColumn<AuditLog, String> colTable;
    @FXML private TableColumn<AuditLog, String> colRecordId;
    @FXML private TableColumn<AuditLog, String> colDetails;
    @FXML private TableColumn<AuditLog, String> colTimestamp;
    @FXML private TextField filterField;
    @FXML private Label statusLabel;

    private final AuditLogRepository auditLogRepository = new AuditLogRepository();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(d ->
                new SimpleIntegerProperty(d.getValue().getId()).asObject());
        colUserId.setCellValueFactory(d ->
                new SimpleIntegerProperty(d.getValue().getUserId()).asObject());
        colAction.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getAction()));
        colTable.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getTableName()));
        colRecordId.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getRecordId()));
        colDetails.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getDetails()));
        colTimestamp.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getCreatedAt() != null
                                ? d.getValue().getCreatedAt().toString() : ""));

        loadAuditLogs();
    }

    private void loadAuditLogs() {
        try {
            List<AuditLog> logs = auditLogRepository.findAll();
            auditTable.setItems(FXCollections.observableArrayList(logs));
            statusLabel.setText("Loaded " + logs.size() + " audit log entries.");
        } catch (DatabaseException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleFilter() {
        String filter = filterField.getText().trim();
        if (filter.isEmpty()) {
            loadAuditLogs();
            return;
        }
        try {
            // Try as user ID first
            int userId = Integer.parseInt(filter);
            List<AuditLog> logs = auditLogRepository.findByUserId(userId);
            auditTable.setItems(FXCollections.observableArrayList(logs));
            statusLabel.setText("Filtered by User ID: " + userId + " — " + logs.size() + " entries.");
        } catch (NumberFormatException e) {
            // Otherwise filter by table name
            try {
                List<AuditLog> logs = auditLogRepository.findByTableName(filter);
                auditTable.setItems(FXCollections.observableArrayList(logs));
                statusLabel.setText("Filtered by table: " + filter + " — " + logs.size() + " entries.");
            } catch (DatabaseException ex) {
                statusLabel.setText("Error: " + ex.getMessage());
            }
        } catch (DatabaseException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        filterField.clear();
        loadAuditLogs();
    }
}