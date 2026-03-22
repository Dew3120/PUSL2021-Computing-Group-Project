package com.group100.wms.ui.outbound;

import com.group100.wms.exception.DatabaseException;
import com.group100.wms.exception.StockShortageException;
import com.group100.wms.model.Batch;
import com.group100.wms.model.Item;
import com.group100.wms.model.Warehouse;
import com.group100.wms.repository.BatchRepository;
import com.group100.wms.repository.ItemRepository;
import com.group100.wms.repository.WarehouseRepository;
import com.group100.wms.service.InventoryService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for handling inter-warehouse stock transfers.
 *
 * OOP Concepts Used:
 * - Encapsulation: UI components and transfer logic are contained within this class.
 * - Abstraction: Uses service and repository layers to hide business and database logic.
 * - Polymorphism: Demonstrated via exception handling (different exception types handled differently).
 * - No direct inheritance defined in this class.
 */
public class InterWarehouseTransferController {

    // ComboBox for selecting source warehouse
    @FXML private ComboBox<Warehouse> fromWarehouseCombo;

    // ComboBox for selecting destination warehouse
    @FXML private ComboBox<Warehouse> toWarehouseCombo;

    // ComboBox for selecting item to transfer
    @FXML private ComboBox<Item> itemCombo;

    // TextField for entering transfer quantity
    @FXML private TextField quantityField;

    // Label for displaying status messages
    @FXML private Label statusLabel;

    // Service layer for inventory operations such as stock deduction
    private final InventoryService inventoryService =
            new InventoryService(new ItemRepository(), new BatchRepository());

    // Repository for fetching warehouse data
    private final WarehouseRepository warehouseRepository = new WarehouseRepository();

    // Repository for fetching item data
    private final ItemRepository itemRepository = new ItemRepository();

    // Repository for saving batch data
    private final BatchRepository batchRepository = new BatchRepository();

    // Initializes the controller by loading warehouses and items into UI components
    @FXML
    public void initialize() {
        loadWarehouses();
        loadItems();
    }

    // Loads all warehouses into both source and destination ComboBoxes
    private void loadWarehouses() {
        try {
            List<Warehouse> warehouses = warehouseRepository.findAll();
            fromWarehouseCombo.setItems(FXCollections.observableArrayList(warehouses));
            toWarehouseCombo.setItems(FXCollections.observableArrayList(warehouses));
        } catch (DatabaseException e) {
            statusLabel.setText("Error loading warehouses: " + e.getMessage());
        }
    }

    // Loads all items into the item ComboBox
    private void loadItems() {
        try {
            List<Item> items = itemRepository.findAll();
            itemCombo.setItems(FXCollections.observableArrayList(items));
        } catch (DatabaseException e) {
            statusLabel.setText("Error loading items: " + e.getMessage());
        }
    }

    // Handles the transfer process: validates input, deducts stock, and creates a new batch
    @FXML
    private void handleTransfer() {
        Warehouse from = fromWarehouseCombo.getValue();
        Warehouse to = toWarehouseCombo.getValue();
        Item item = itemCombo.getValue();
        String qtyText = quantityField.getText().trim();

        if (from == null  to == null  item == null || qtyText.isBlank()) {
            statusLabel.setText("Please fill all fields."); return;
        }
        if (from.getId() == to.getId()) {
            statusLabel.setText("Source and destination must differ."); return;
        }
        try {
            int qty = Integer.parseInt(qtyText);
            inventoryService.deductStockFifo(item.getId(), from.getId(), qty);
                    Batch batch = new Batch();
            batch.setItemId(item.getId());
            batch.setPoId(0);
            batch.setQuantity(qty);
            batch.setAvailableQty(qty);
            batch.setUnitCost(0.0);
            batch.setReceiptDate(LocalDate.now());
            batchRepository.save(batch);

            statusLabel.setText("Transferred " + qty + " units of "
                    + item.getName() + " successfully.");
            quantityField.clear();
        } catch (StockShortageException e) {
            statusLabel.setText("Stock shortage: available=" + e.getAvailable());
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid quantity.");
        } catch (DatabaseException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    // Clears all input fields and resets the form
    @FXML
    private void handleClear() {
        fromWarehouseCombo.setValue(null);
        toWarehouseCombo.setValue(null);
        itemCombo.setValue(null);
        quantityField.clear();
        statusLabel.setText("");
    }
}
