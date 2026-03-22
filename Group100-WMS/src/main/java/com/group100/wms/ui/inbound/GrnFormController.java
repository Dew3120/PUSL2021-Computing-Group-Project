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

/**
 * Controller for handling GRN (Goods Received Note) form operations.
 *
 * OOP Concepts Used:
 * - Encapsulation: UI components and GRN item data are managed within this class.
 * - Abstraction: Uses service and repository layers to hide business and database logic.
 * - Polymorphism: Used through JavaFX controls and method handling.
 * - No direct inheritance defined in this class.
 */
public class GrnFormController {

    // ComboBox for selecting a Purchase Order
    @FXML private ComboBox<PurchaseOrder> poCombo;

    // ComboBox for selecting an Item
    @FXML private ComboBox<Item> itemCombo;

    // TextField for entering quantity of the item
    @FXML private TextField quantityField;

    // TextField for entering unit cost of the item
    @FXML private TextField unitCostField;

    // ListView to display added GRN items
    @FXML private ListView<String> itemListView;

    // TextArea for entering additional notes
    @FXML private TextArea notesArea;

    // Label for displaying status messages
    @FXML private Label statusLabel;

    // Service layer to handle inbound operations like receiving goods
    private final InboundService inboundService = new InboundService(
            new PurchaseOrderRepository(), new GrnRepository(), new BatchRepository());

    // Repository to fetch item data from the database
    private final ItemRepository itemRepository = new ItemRepository();

    // Repository to fetch purchase order data
    private final PurchaseOrderRepository poRepository = new PurchaseOrderRepository();

    // Stores the list of GRN items added by the user
    private final List<GrnItem> grnItems = new ArrayList<>();

    // Initializes the form by loading purchase orders and items
    @FXML
    public void initialize() {
        loadPOs();
        loadItems();
    }

    // Loads all purchase orders into the ComboBox
    private void loadPOs() {
        try {
            List<PurchaseOrder> pos = poRepository.findAll();
            poCombo.setItems(FXCollections.observableArrayList(pos));
        } catch (DatabaseException e) {
            statusLabel.setText("Error loading POs: " + e.getMessage());
        }
    }

    // Loads all items into the ComboBox
    private void loadItems() {
        try {
            List<Item> items = itemRepository.findAll();
            itemCombo.setItems(FXCollections.observableArrayList(items));
        } catch (DatabaseException e) {
            statusLabel.setText("Error loading items: " + e.getMessage());
        }
    }
    // Handles adding an item to the GRN item list with validation
    @FXML
    private void handleAddItem() {
        Item item = itemCombo.getValue();
        String qtyText = quantityField.getText().trim();
        String costText = unitCostField.getText().trim();
        if (item == null  qtyText.isBlank()  costText.isBlank()) {
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

    // Handles saving the GRN and its items to the system
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

    // Clears all input fields and resets the form
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
    
