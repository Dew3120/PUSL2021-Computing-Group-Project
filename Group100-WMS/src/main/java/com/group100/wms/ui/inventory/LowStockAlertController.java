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

// OOP Concepts Used:
// Encapsulation - UI components and logic are encapsulated within the controller.
// Abstraction - The controller abstracts the process of retrieving and displaying low stock data.
// Inheritance - JavaFX UI components inherit from base classes.
// Polymorphism - Service and repository methods can have multiple implementations.

public class LowStockAlertController {

    // TableView used to display items with low stock levels
    @FXML private TableView<Item> alertTable;

    // Table column for displaying item SKU/code
    @FXML private TableColumn<Item, String> colCode;

    // Table column for displaying item name
    @FXML private TableColumn<Item, String> colName;

    // Table column for displaying item category
    @FXML private TableColumn<Item, String> colCategory;

    // Label used to display status messages such as number of low stock items or errors
    @FXML private Label statusLabel;

    // Service used to retrieve inventory-related data such as low stock items
    private final InventoryService inventoryService =
            new InventoryService(new ItemRepository(), new BatchRepository());

    // Initializes table columns and loads low stock items when the UI is loaded
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

    // Loads items that are below the stock threshold and updates the table and status label
    private void loadLowStock() {
        try {
            List<Item> items = inventoryService.getLowStockItems(1);
            alertTable.setItems(FXCollections.observableArrayList(items));
            statusLabel.setText(items.size() + " low stock item(s) found.");
        } catch (DatabaseException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    // Refreshes the low stock list when triggered (e.g., button click)
    @FXML
    private void handleRefresh() { loadLowStock(); }
}
