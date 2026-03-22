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

// OOP Concepts Used:
// Encapsulation - All UI components and related logic are contained within this controller class.
// Abstraction - Database operations are abstracted via AuditLogRepository and the AuditLog model.
// Inheritance - Extends functionality of JavaFX TableView/TableColumn classes indirectly through composition and controller usage.
// Polymorphism - Uses method overriding and lambda expressions to provide specific behavior for TableColumn value factories.

public class AuditLogController {

    // TableView that displays all audit log entries
    @FXML private TableView<AuditLog> auditTable;

    // TableColumn for AuditLog ID
    @FXML private TableColumn<AuditLog, Integer> colId;

    // TableColumn for User ID associated with the audit log
    @FXML private TableColumn<AuditLog, Integer> colUserId;

    // TableColumn for the action performed (INSERT, UPDATE, DELETE, etc.)
    @FXML private TableColumn<AuditLog, String> colAction;

    // TableColumn for the database table affected by the action
    @FXML private TableColumn<AuditLog, String> colTable;

    // TableColumn for the specific record ID affected in the table
    @FXML private TableColumn<AuditLog, String> colRecordId;

    // TableColumn for any additional details of the action
    @FXML private TableColumn<AuditLog, String> colDetails;

    // TableColumn for the timestamp when the action occurred
    @FXML private TableColumn<AuditLog, String> colTimestamp;

    // TextField for filtering the audit logs by User ID or Table Name
    @FXML private TextField filterField;

    // Label to display status messages (like number of entries loaded or errors)
    @FXML private Label statusLabel;

    // Repository instance to handle database operations for AuditLog
    private final AuditLogRepository auditLogRepository = new AuditLogRepository();

    // Initializes the TableView and its columns when the FXML loads
    @FXML
    public void initialize() {
        // Set up TableColumn mappings to AuditLog properties
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

        // Load all audit logs into the TableView
        loadAuditLogs();
    }

    // Loads all audit logs from the database and displays them in the TableView
    private void loadAuditLogs() {
        try {
            List<AuditLog> logs = auditLogRepository.findAll();
            auditTable.setItems(FXCollections.observableArrayList(logs));
            statusLabel.setText("Loaded " + logs.size() + " audit log entries.");
        } catch (DatabaseException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    // Handles filtering of audit logs based on the value in filterField
    // If the input is an integer, filters by User ID
    // Otherwise, filters by table name
    @FXML
    private void handleFilter() {
        String filter = filterField.getText().trim();
        if (filter.isEmpty()) {
            loadAuditLogs();
            return;
        }
        try {
            // Try filtering by User ID first
            int userId = Integer.parseInt(filter);
            List<AuditLog> logs = auditLogRepository.findByUserId(userId);
            auditTable.setItems(FXCollections.observableArrayList(logs));
            statusLabel.setText("Filtered by User ID: " + userId + " — " + logs.size() + " entries.");
        } catch (NumberFormatException e) {
            // If input is not an integer, filter by Table Name
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

    // Clears any filters and reloads all audit logs
    @FXML
    private void handleRefresh() {
        filterField.clear();
        loadAuditLogs();
    }
}