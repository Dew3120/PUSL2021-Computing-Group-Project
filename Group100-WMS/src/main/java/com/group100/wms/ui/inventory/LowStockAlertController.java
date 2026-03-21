package com.group100.wms.ui.inventory;

import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.Item;
import com.group100.wms.repository.BatchRepository;
import com.group100.wms.repository.ItemRepository;
import com.group100.wms.service.InventoryService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;

public class LowStockAlertController {

    @FXML private TableView<Item> alertTable;
    @FXML private TableColumn<Item, String> colCode;
    @FXML private TableColumn<Item, String> colName;
    @FXML private TableColumn<Item, String> colCategory;
    @FXML private Label statusLabel;

    private final InventoryService inventoryService =
            new InventoryService(new ItemRepository(), new BatchRepository());

    @FXML
    public void initialize() {
        colCode.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getSku()));
        colName.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getName()));
        colCategory.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getCategory()));
        loadLowStock();
    }

    private void loadLowStock() {
        try {
            List<Item> items = inventoryService.getLowStockItems(1);
            alertTable.setItems(FXCollections.observableArrayList(items));
            statusLabel.setText(items.size() + " low stock item(s) found.");
        } catch (DatabaseException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() { loadLowStock(); }
}