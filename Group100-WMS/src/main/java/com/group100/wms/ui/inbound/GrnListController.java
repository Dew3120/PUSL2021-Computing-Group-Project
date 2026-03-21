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

public class GrnListController {

    @FXML private TableView<GoodsReceivedNote> grnTable;
    @FXML private TableColumn<GoodsReceivedNote, Integer> colId;
    @FXML private TableColumn<GoodsReceivedNote, String> colGrnNumber;
    @FXML private TableColumn<GoodsReceivedNote, String> colStatus;
    @FXML private TableColumn<GoodsReceivedNote, String> colReceivedDate;
    @FXML private Label statusLabel;

    private final InboundService inboundService = new InboundService(
            new PurchaseOrderRepository(), new GrnRepository(), new BatchRepository());

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

    private void loadGrns() {
        try {
            List<GoodsReceivedNote> grns = inboundService.getAllGrns();
            grnTable.setItems(FXCollections.observableArrayList(grns));
            statusLabel.setText("Loaded " + grns.size() + " GRNs.");
        } catch (DatabaseException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML private void handleRefresh() { loadGrns(); }
}