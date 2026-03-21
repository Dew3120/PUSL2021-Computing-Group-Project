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

public class InterWarehouseTransferController {

    @FXML private ComboBox<Warehouse> fromWarehouseCombo;
    @FXML private ComboBox<Warehouse> toWarehouseCombo;
    @FXML private ComboBox<Item> itemCombo;
    @FXML private TextField quantityField;
    @FXML private Label statusLabel;

    private final InventoryService inventoryService =
            new InventoryService(new ItemRepository(), new BatchRepository());
    private final WarehouseRepository warehouseRepository = new WarehouseRepository();
    private final ItemRepository itemRepository = new ItemRepository();
    private final BatchRepository batchRepository = new BatchRepository();

    @FXML
    public void initialize() {
        loadWarehouses();
        loadItems();
    }

    private void loadWarehouses() {
        try {
            List<Warehouse> warehouses = warehouseRepository.findAll();
            fromWarehouseCombo.setItems(FXCollections.observableArrayList(warehouses));
            toWarehouseCombo.setItems(FXCollections.observableArrayList(warehouses));
        } catch (DatabaseException e) {
            statusLabel.setText("Error loading warehouses: " + e.getMessage());
        }
    }

    private void loadItems() {
        try {
            List<Item> items = itemRepository.findAll();
            itemCombo.setItems(FXCollections.observableArrayList(items));
        } catch (DatabaseException e) {
            statusLabel.setText("Error loading items: " + e.getMessage());
        }
    }

    @FXML
    private void handleTransfer() {
        Warehouse from = fromWarehouseCombo.getValue();
        Warehouse to = toWarehouseCombo.getValue();
        Item item = itemCombo.getValue();
        String qtyText = quantityField.getText().trim();

        if (from == null || to == null || item == null || qtyText.isBlank()) {
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

    @FXML
    private void handleClear() {
        fromWarehouseCombo.setValue(null);
        toWarehouseCombo.setValue(null);
        itemCombo.setValue(null);
        quantityField.clear();
        statusLabel.setText("");
    }
}