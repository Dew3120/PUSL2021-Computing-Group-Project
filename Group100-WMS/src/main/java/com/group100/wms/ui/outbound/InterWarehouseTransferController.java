package com.group100.wms.ui.outbound;

import com.group100.wms.exception.DatabaseException;
import com.group100.wms.exception.StockShortageException;
import com.group100.wms.model.Batch;
import com.group100.wms.model.GinItem;
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
        fromWarehouseCombo.valueProperty().addListener((obs, oldValue, newValue) -> loadItems(newValue));
    }

    private void loadWarehouses() {
        try {
            List<Warehouse> warehouses = warehouseRepository.findAll();
            fromWarehouseCombo.setItems(FXCollections.observableArrayList(warehouses));
            toWarehouseCombo.setItems(FXCollections.observableArrayList(warehouses));
            if (!warehouses.isEmpty()) fromWarehouseCombo.setValue(warehouses.get(0));
            if (warehouses.size() > 1) toWarehouseCombo.setValue(warehouses.get(1));
        } catch (DatabaseException e) {
            statusLabel.setText("Error loading warehouses: " + e.getMessage());
        }
    }

    private void loadItems(Warehouse warehouse) {
        try {
            List<Item> items = warehouse == null
                    ? itemRepository.findAll()
                    : itemRepository.findByWarehouseId(warehouse.getId());
            itemCombo.setItems(FXCollections.observableArrayList(items));
            itemCombo.setValue(null);
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
        if (item.getWarehouseId() != from.getId()) {
            statusLabel.setText("Selected item does not belong to the source warehouse."); return;
        }
        try {
            int qty = Integer.parseInt(qtyText);
            if (qty <= 0) {
                statusLabel.setText("Quantity must be greater than 0."); return;
            }

            List<GinItem> allocations = inventoryService.deductStockFifo(item.getId(), from.getId(), qty);
            Item destinationItem = resolveDestinationItem(item, to);

            for (GinItem allocation : allocations) {
                Batch sourceBatch = batchRepository.findById(allocation.getBatchId())
                        .orElseThrow(() -> new DatabaseException("Source batch not found: " + allocation.getBatchId()));
                Batch transferBatch = new Batch();
                transferBatch.setItemId(destinationItem.getId());
                transferBatch.setPoId(sourceBatch.getPoId());
                transferBatch.setQuantity(allocation.getQuantityIssued());
                transferBatch.setAvailableQty(allocation.getQuantityIssued());
                transferBatch.setUnitCost(sourceBatch.getUnitCost());
                transferBatch.setReceiptDate(LocalDate.now());
                batchRepository.save(transferBatch);
            }

            statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            statusLabel.setText("Transferred " + qty + " units of "
                    + item.getName() + " to " + to.getName() + ".");
            quantityField.clear();
        } catch (StockShortageException e) {
            statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            statusLabel.setText("Stock shortage: available=" + e.getAvailable());
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid quantity.");
        } catch (DatabaseException e) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    private Item resolveDestinationItem(Item source, Warehouse destination) throws DatabaseException {
        return itemRepository.findEquivalentInWarehouse(source, destination.getId())
                .orElseGet(() -> createDestinationItem(source, destination));
    }

    private Item createDestinationItem(Item source, Warehouse destination) {
        try {
            Item clone = new Item();
            clone.setSku(buildTransferSku(source, destination.getId()));
            clone.setName(source.getName());
            clone.setDescription(source.getDescription());
            clone.setCategory(source.getCategory());
            clone.setColour(source.getColour());
            clone.setUnit(source.getUnit());
            clone.setWarehouseId(destination.getId());
            itemRepository.save(clone);
            return clone;
        } catch (DatabaseException e) {
            throw new IllegalStateException(e);
        }
    }

    private String buildTransferSku(Item source, int warehouseId) throws DatabaseException {
        String base = "TR" + warehouseId + "-" + source.getId();
        String sku = base.length() <= 20 ? base : base.substring(0, 20);
        int suffix = 1;
        String candidate = sku;
        while (itemRepository.findBySku(candidate).isPresent()) {
            String ending = "-" + suffix++;
            int maxBaseLength = Math.max(1, 20 - ending.length());
            candidate = sku.substring(0, Math.min(sku.length(), maxBaseLength)) + ending;
        }
        return candidate;
    }

    @FXML
    private void handleClear() {
        fromWarehouseCombo.setValue(null);
        toWarehouseCombo.setValue(null);
        itemCombo.setValue(null);
        quantityField.clear();
        statusLabel.setStyle("");
        statusLabel.setText("");
    }
}
