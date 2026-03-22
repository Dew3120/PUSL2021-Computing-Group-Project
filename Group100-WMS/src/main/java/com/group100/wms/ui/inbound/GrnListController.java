package com.group100.wms.ui.inbound;

import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.GoodsReceivedNote;
import com.group100.wms.repository.BatchRepository;
import com.group100.wms.repository.GrnRepository;
import com.group100.wms.repository.PurchaseOrderRepository;
import com.group100.wms.service.InboundService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

/**
 * Controller for displaying and managing Goods Received Notes (GRNs) in the UI.
 *
 * OOP Concepts Used:
 * - Encapsulation: UI components and data handling are contained within this class.
 * - Abstraction: Uses service layer to hide database and business logic.
 * - Polymorphism: Used via JavaFX property bindings and method overriding internally.
 * - No direct inheritance defined in this class.
 */
public class GrnListController {

    // TableView used to display list of GRNs
    @FXML private TableView<GoodsReceivedNote> grnTable;

    // Column for displaying GRN ID
    @FXML private TableColumn<GoodsReceivedNote, Integer> colId;

    // Column for displaying formatted GRN number
    @FXML private TableColumn<GoodsReceivedNote, String> colGrnNumber;

    // Column for displaying GRN status
    @FXML private TableColumn<GoodsReceivedNote, String> colStatus;

    // Column for displaying received date
    @FXML private TableColumn<GoodsReceivedNote, String> colReceivedDate;

    // Label used to display status messages to the user
    @FXML private Label statusLabel;

    // Service layer used to retrieve GRN data and perform inbound operations
    private final InboundService inboundService = new InboundService(
            new PurchaseOrderRepository(), new GrnRepository(), new BatchRepository());

    // Initializes table columns and loads GRN data into the table
    @FXML
    public void initialize() {
        colId.setCellValueFactory(d ->
                new SimpleIntegerProperty(d.getValue().getId()).asObject());
        colGrnNumber.setCellValueFactory(d ->
                new SimpleStringProperty(
                        "GRN-" + String.format("%03d", d.getValue().getId())));
        colStatus.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getStatus()));
        colReceivedDate.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getReceivedDate() != null
                                ? d.getValue().getReceivedDate().toString() : ""));
        loadGrns();
    }

    // Loads all GRNs from the database and displays them in the table
    private void loadGrns() {
        try {
            List<GoodsReceivedNote> grns = inboundService.getAllGrns();
            grnTable.setItems(FXCollections.observableArrayList(grns));
            statusLabel.setText("Loaded " + grns.size() + " GRNs.");
        } catch (DatabaseException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    // Refreshes the GRN list by reloading data from the database
    @FXML private void handleRefresh() { loadGrns(); }
}
