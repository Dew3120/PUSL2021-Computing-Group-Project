```java id="k3y1mf"
package com.group100.wms.ui.outbound;

import com.group100.wms.core.SessionManager;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.exception.StockShortageException;
import com.group100.wms.model.GinItem;
import com.group100.wms.model.GoodsIssueNote;
import com.group100.wms.model.Item;
import com.group100.wms.repository.BatchRepository;
import com.group100.wms.repository.GinRepository;
import com.group100.wms.repository.ItemRepository;
import com.group100.wms.service.InventoryService;
import com.group100.wms.service.OutboundService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;

// OOP Concepts Used:
// Encapsulation - UI fields, lists, and methods encapsulate the form’s functionality.
// Abstraction - Controller abstracts stock checks, GIN creation, and business logic.
// Inheritance - JavaFX UI components inherit from base classes like TextField, ComboBox, ListView, Label.
// Polymorphism - Alert, TableView, and service method calls demonstrate polymorphic behavior.

public class GinFormController {

    // TextField to input destination/issued-to information
    @FXML private TextField issuedToField;

    // ComboBox to select items for the GIN
    @FXML private ComboBox<Item> itemCombo;

    // TextField to input quantity for the selected item
    @FXML private TextField quantityField;

    // ListView to display added items in the current GIN
    @FXML private ListView<String> itemListView;

    // TextArea for additional notes (optional)
    @FXML private TextArea notesArea;

    // Label for displaying status messages, warnings, or errors
    @FXML private Label statusLabel;

    // Service for inventory operations like checking stock
    private final InventoryService inventoryService =
            new InventoryService(new ItemRepository(), new BatchRepository());

    // Service for outbound operations such as issuing GINs
    private final OutboundService outboundService = new OutboundService(
            new GinRepository(), inventoryService);

    // Repository to fetch item data
    private final ItemRepository itemRepository = new ItemRepository();

    // List storing GinItem objects representing items added to the current GIN
    private final List<GinItem> ginItems = new ArrayList<>();

    // Initializes the form by loading available items into the ComboBox
    @FXML
    public void initialize() { loadItems(); }

    // Loads all items from the database into the itemCombo ComboBox
    private void loadItems() {
        try {
            List<Item> items = itemRepository.findAll();
            itemCombo.setItems(FXCollections.observableArrayList(items));
        } catch (DatabaseException e) {
            statusLabel.setText("Error loading items: " + e.getMessage());
        }
    }

    // Handles adding an item to the GIN, including stock check and shortage warning
    @FXML
    private void handleAddItem() {
        Item item = itemCombo.getValue();
        String qtyText = quantityField.getText().trim();
        if (item == null || qtyText.isBlank()) {
            statusLabel.setText("Select item and enter quantity.");
            return;
        }
        try {
            int qty = Integer.parseInt(qtyText);
            if (qty <= 0) {
                statusLabel.setText("Quantity must be greater than 0.");
                return;
            }

            // AUTO SHORTAGE DETECTION — check stock BEFORE adding
            int available = inventoryService.getStockLevel(item.getId());

            // Calculate already-added quantity for this item in the current GIN
            int alreadyAdded = ginItems.stream()
                    .filter(g -> g.getItemId() == item.getId())
                    .mapToInt(GinItem::getQuantityIssued)
                    .sum();

            int totalNeeded = alreadyAdded + qty;

            if (totalNeeded > available) {
                statusLabel.setText("SHORTAGE WARNING: " + item.getName()
                        + " — requested total: " + totalNeeded
                        + ", available: " + available
                        + ". Short by " + (totalNeeded - available) + " units.");
                statusLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");

                // Show confirmation dialog
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Stock Shortage Detected");
                alert.setHeaderText("Insufficient stock for " + item.getName());
                alert.setContentText("Requested: " + totalNeeded
                        + "\nAvailable: " + available
                        + "\nShort by: " + (totalNeeded - available)
                        + "\n\nDo you still want to add this item?");
                if (alert.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
                    return;
                }
            } else {
                statusLabel.setStyle("");
                statusLabel.setText("Stock OK: " + item.getName()
                        + " — available: " + available + ", requesting: " + totalNeeded);
            }

            // Add item to the GIN list and display in the ListView
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

    // Handles saving the GIN, issuing the goods via the OutboundService
    @FXML
    private void handleSave() {
        String issuedTo = issuedToField.getText().trim();
        if (issuedTo.isBlank() || ginItems.isEmpty()) {
            statusLabel.setText("Fill issued to and add at least one item.");
            return;
        }
        try {
            GoodsIssueNote gin = new GoodsIssueNote();
            gin.setWarehouseId(SessionManager.getCurrentUser().getId());
            gin.setDestination(issuedTo);
            gin.setDestType("PRODUCTION");
            gin.setIssuedBy(SessionManager.getCurrentUser().getId());
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

    // Clears all form fields, resets the ListView and status label
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
```
