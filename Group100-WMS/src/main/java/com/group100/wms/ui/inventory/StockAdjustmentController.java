package com.group100.wms.ui.inventory;

import com.group100.wms.core.AuditLogger;
import com.group100.wms.core.SessionManager;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.Batch;
import com.group100.wms.model.Item;
import com.group100.wms.repository.BatchRepository;
import com.group100.wms.repository.ItemRepository;
import com.group100.wms.service.InventoryService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;

public class StockAdjustmentController {

    @FXML private ComboBox<Item> itemComboBox;
    @FXML private TextField quantityField;
    @FXML private TextField costPriceField;
    @FXML private ComboBox<String> adjustmentTypeCombo;
    @FXML private Label currentStockLabel;
    @FXML private Label statusLabel;

    private final InventoryService inventoryService =
            new InventoryService(new ItemRepository(), new BatchRepository());
    private final BatchRepository batchRepository = new BatchRepository();
    private final ItemRepository itemRepository = new ItemRepository();

    @FXML
    public void initialize() {
        adjustmentTypeCombo.setItems(FXCollections.observableArrayList("ADD", "REMOVE"));
        adjustmentTypeCombo.setValue("ADD");
        loadItems();
        itemComboBox.setOnAction(e -> updateCurrentStock());
    }

    private void loadItems() {
        try {
            List<Item> items = itemRepository.findAll();
            itemComboBox.setItems(FXCollections.observableArrayList(items));
        } catch (DatabaseException e) {
            statusLabel.setText("Error loading items: " + e.getMessage());
        }
    }

    private void updateCurrentStock() {
        Item selected = itemComboBox.getValue();
        if (selected == null) return;
        try {
            int stock = inventoryService.getStockLevel(selected.getId());
            currentStockLabel.setText("Current Stock: " + stock);
        } catch (DatabaseException e) {
            currentStockLabel.setText("Error fetching stock.");
        }
    }

    @FXML
    private void handleApply() {
        Item selected = itemComboBox.getValue();
        if (selected == null) { statusLabel.setText("Please select an item."); return; }
        String qtyText = quantityField.getText().trim();
        String costText = costPriceField.getText().trim();
        if (qtyText.isBlank() || costText.isBlank()) {
            statusLabel.setText("Please fill all fields."); return;
        }
        try {
            int qty = Integer.parseInt(qtyText);
            double cost = Double.parseDouble(costText);
            String type = adjustmentTypeCombo.getValue();

            if ("ADD".equals(type)) {
                Batch batch = new Batch();
                batch.setItemId(selected.getId());
                batch.setPoId(0);
                batch.setQuantity(qty);
                batch.setAvailableQty(qty);
                batch.setUnitCost(cost);
                batch.setReceiptDate(LocalDate.now());
                batchRepository.save(batch);
                AuditLogger.log(SessionManager.getCurrentUser().getId(),
                        "STOCK_ADJUST_ADD", "batches",
                        String.valueOf(selected.getId()),
                        "Added " + qty + " units to itemId=" + selected.getId());
                statusLabel.setText("Stock added successfully.");
            } else {
                inventoryService.deductStockFifo(selected.getId(), 1, qty);
                statusLabel.setText("Stock removed successfully.");
            }
            updateCurrentStock();
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid quantity or cost price.");
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        itemComboBox.setValue(null);
        quantityField.clear();
        costPriceField.clear();
        currentStockLabel.setText("Current Stock: —");
        statusLabel.setText("");
    }
}