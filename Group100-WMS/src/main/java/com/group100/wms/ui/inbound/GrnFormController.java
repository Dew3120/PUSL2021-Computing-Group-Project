package com.group100.wms.ui.inbound;

import com.group100.wms.core.SessionManager;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.GoodsReceivedNote;
import com.group100.wms.model.GrnItem;
import com.group100.wms.model.Item;
import com.group100.wms.model.PurchaseOrder;
import com.group100.wms.repository.BatchRepository;
import com.group100.wms.repository.GrnRepository;
import com.group100.wms.repository.ItemRepository;
import com.group100.wms.repository.PurchaseOrderRepository;
import com.group100.wms.service.InboundService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;

public class GrnFormController {

    @FXML private ComboBox<PurchaseOrder> poCombo;
    @FXML private ComboBox<Item> itemCombo;
    @FXML private TextField quantityField;
    @FXML private TextField unitCostField;
    @FXML private ListView<String> itemListView;
    @FXML private TextArea notesArea;
    @FXML private Label statusLabel;

    private final InboundService inboundService = new InboundService(
            new PurchaseOrderRepository(), new GrnRepository(), new BatchRepository());
    private final ItemRepository itemRepository = new ItemRepository();
    private final PurchaseOrderRepository poRepository = new PurchaseOrderRepository();
    private final List<GrnItem> grnItems = new ArrayList<>();

    @FXML
    public void initialize() {
        loadPOs();
        loadItems();
    }

    private void loadPOs() {
        try {
            List<PurchaseOrder> pos = poRepository.findAll();
            poCombo.setItems(FXCollections.observableArrayList(pos));
        } catch (DatabaseException e) {
            statusLabel.setText("Error loading POs: " + e.getMessage());
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
    private void handleAddItem() {
        Item item = itemCombo.getValue();
        String qtyText = quantityField.getText().trim();
        String costText = unitCostField.getText().trim();
        if (item == null || qtyText.isBlank() || costText.isBlank()) {
            statusLabel.setText("Fill item, quantity and cost.");
            return;
        }
        try {
            int qty = Integer.parseInt(qtyText);
            double cost = Double.parseDouble(costText);
            GrnItem grnItem = new GrnItem();
            grnItem.setItemId(item.getId());
            grnItem.setQuantity(qty);
            grnItem.setUnitCost(cost);
            grnItems.add(grnItem);
            itemListView.getItems().add(item.getName() + " x" + qty + " @ " + cost);
            quantityField.clear();
            unitCostField.clear();
        } catch (NumberFormatException e) {
            statusLabel.setText("Invalid quantity or cost.");
        }
    }

    @FXML
    private void handleSave() {
        PurchaseOrder po = poCombo.getValue();
        if (po == null || grnItems.isEmpty()) {
            statusLabel.setText("Select a PO and add at least one item.");
            return;
        }
        try {
            GoodsReceivedNote grn = new GoodsReceivedNote();
            grn.setPoId(po.getId());
            grn.setWarehouseId(po.getWarehouseId());
            grn.setSupplierId(po.getSupplierId());
            grn.setReceivedBy(SessionManager.getCurrentUser().getId());
            inboundService.receiveGoods(grn, grnItems);
            statusLabel.setText("GRN saved successfully.");
            grnItems.clear();
            itemListView.getItems().clear();
        } catch (DatabaseException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        poCombo.setValue(null);
        itemCombo.setValue(null);
        quantityField.clear();
        unitCostField.clear();
        if (notesArea != null) notesArea.clear();
        grnItems.clear();
        itemListView.getItems().clear();
        statusLabel.setText("");
    }
}