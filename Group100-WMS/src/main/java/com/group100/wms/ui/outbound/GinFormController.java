package com.group100.wms.ui.outbound;

import com.group100.wms.core.SessionManager;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.exception.StockShortageException;
import com.group100.wms.model.GinItem;
import com.group100.wms.model.GoodsIssueNote;
import com.group100.wms.model.Item;
import com.group100.wms.model.Warehouse;
import com.group100.wms.repository.BatchRepository;
import com.group100.wms.repository.GinRepository;
import com.group100.wms.repository.ItemRepository;
import com.group100.wms.repository.WarehouseRepository;
import com.group100.wms.service.InventoryService;
import com.group100.wms.service.OutboundService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;

public class GinFormController {

    @FXML private ComboBox<Warehouse> warehouseCombo;
    @FXML private TextField issuedToField;
    @FXML private ComboBox<Item> itemCombo;
    @FXML private TextField quantityField;
    @FXML private ListView<String> itemListView;
    @FXML private TextArea notesArea;
    @FXML private Label statusLabel;

    private final InventoryService inventoryService =
            new InventoryService(new ItemRepository(), new BatchRepository());
    private final OutboundService outboundService = new OutboundService(
            new GinRepository(), inventoryService);
    private final ItemRepository itemRepository = new ItemRepository();
    private final WarehouseRepository warehouseRepository = new WarehouseRepository();
    private final List<GinItem> ginItems = new ArrayList<>();

    @FXML
    public void initialize() {
        loadWarehouses();
        warehouseCombo.valueProperty().addListener((obs, oldValue, newValue) -> loadItems(newValue));
    }

    private void loadWarehouses() {
        try {
            List<Warehouse> warehouses = warehouseRepository.findAll();
            warehouseCombo.setItems(FXCollections.observableArrayList(warehouses));
            if (!warehouses.isEmpty()) warehouseCombo.setValue(warehouses.get(0));
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
            ginItems.clear();
            itemListView.getItems().clear();
        } catch (DatabaseException e) {
            statusLabel.setText("Error loading items: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddItem() {
        Warehouse warehouse = warehouseCombo.getValue();
        Item item = itemCombo.getValue();
        String qtyText = quantityField.getText().trim();
        if (warehouse == null || item == null || qtyText.isBlank()) {
            statusLabel.setText("Select warehouse, item, and quantity.");
            return;
        }
        try {
            int qty = Integer.parseInt(qtyText);
            if (qty <= 0) {
                statusLabel.setText("Quantity must be greater than 0.");
                return;
            }

            int available = inventoryService.getStockLevel(item.getId(), warehouse.getId());
            int alreadyAdded = ginItems.stream()
                    .filter(g -> g.getItemId() == item.getId())
                    .mapToInt(GinItem::getQuantityIssued)
                    .sum();
            int totalNeeded = alreadyAdded + qty;

            if (totalNeeded > available) {
                statusLabel.setText("SHORTAGE WARNING: " + item.getName()
                        + " requested total: " + totalNeeded
                        + ", available: " + available
                        + ", short by " + (totalNeeded - available) + " units.");
                statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                return;
            }

            statusLabel.setStyle("");
            statusLabel.setText("Stock OK: " + item.getName()
                    + " available: " + available + ", requesting: " + totalNeeded);

            GinItem ginItem = new GinItem();
            ginItem.setItemId(item.getId());
            ginItem.setQuantityIssued(qty);
            ginItems.add(ginItem);
            itemListView.getItems().add(item.getName() + " x" + qty
                    + " (stock: " + available + ")");
            quantityField.clear();

        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid quantity.");
        } catch (DatabaseException e) {
            statusLabel.setText("Error checking stock: " + e.getMessage());
        }
    }

    @FXML
    private void handleSave() {
        Warehouse warehouse = warehouseCombo.getValue();
        String issuedTo = issuedToField.getText().trim();
        if (warehouse == null || issuedTo.isBlank() || ginItems.isEmpty()) {
            statusLabel.setText("Select warehouse, fill issued to, and add at least one item.");
            return;
        }
        try {
            GoodsIssueNote gin = new GoodsIssueNote();
            gin.setWarehouseId(warehouse.getId());
            gin.setDestination(issuedTo);
            gin.setDestType("PRODUCTION");
            gin.setIssuedBy(SessionManager.getCurrentUser() != null
                    ? SessionManager.getCurrentUser().getId() : 0);
            gin.setStatus("PENDING");
            outboundService.issueGoods(gin, ginItems);
            statusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
            statusLabel.setText("GIN saved successfully.");
            ginItems.clear();
            itemListView.getItems().clear();
        } catch (StockShortageException e) {
            statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            statusLabel.setText("Stock shortage: requested=" + e.getRequested()
                    + ", available=" + e.getAvailable());
        } catch (DatabaseException e) {
            statusLabel.setStyle("-fx-text-fill: red;");
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        issuedToField.clear();
        itemCombo.setValue(null);
        quantityField.clear();
        if (notesArea != null) notesArea.clear();
        ginItems.clear();
        itemListView.getItems().clear();
        statusLabel.setStyle("");
        statusLabel.setText("");
    }
}
