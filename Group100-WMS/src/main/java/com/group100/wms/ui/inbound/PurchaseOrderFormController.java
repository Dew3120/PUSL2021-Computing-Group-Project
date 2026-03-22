package com.group100.wms.ui.inbound;

import com.group100.wms.core.SessionManager;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.PurchaseOrder;
import com.group100.wms.model.Supplier;
import com.group100.wms.repository.BatchRepository;
import com.group100.wms.repository.GrnRepository;
import com.group100.wms.repository.PurchaseOrderRepository;
import com.group100.wms.repository.SupplierRepository;
import com.group100.wms.service.InboundService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller for handling Purchase Order form operations in the UI.
 *
 * OOP Concepts Used:
 * - Encapsulation: UI components and business logic are managed within this class.
 * - Abstraction: Uses service and repository layers to hide database operations.
 * - Polymorphism: Possible use through service/repository implementations.
 * - No direct inheritance used in this class.
 */
public class PurchaseOrderFormController {

    // ComboBox used to select a supplier
    @FXML private ComboBox<Supplier> supplierCombo;

    // DatePicker used to select expected delivery date
    @FXML private DatePicker expectedDeliveryDate;

    // TextArea used to enter additional notes for the purchase order
    @FXML private TextArea notesArea;

    // Label used to display status messages to the user
    @FXML private Label statusLabel;

    // Service layer used to handle inbound operations such as creating purchase orders
    private final InboundService inboundService = new InboundService(
            new PurchaseOrderRepository(), new GrnRepository(), new BatchRepository());

    // Repository used to fetch supplier data from the database
    private final SupplierRepository supplierRepository = new SupplierRepository();

    // Initializes the form by loading supplier data into the ComboBox
    @FXML
    public void initialize() {
        loadSuppliers();
    }

    // Loads all active suppliers from the database and populates the ComboBox
    private void loadSuppliers() {
        try {
            List<Supplier> suppliers = supplierRepository.findAllActive();
            supplierCombo.setItems(FXCollections.observableArrayList(suppliers));
        } catch (DatabaseException e) {
            statusLabel.setText("Error loading suppliers: " + e.getMessage());
        }
    }

    // Handles the save action: validates input, creates a purchase order, and stores it
    @FXML
    private void handleSave() {
        Supplier supplier = supplierCombo.getValue();
        LocalDate deliveryDate = expectedDeliveryDate.getValue();
        if (supplier == null || deliveryDate == null) {
            statusLabel.setText("Please fill all required fields.");
            return;
        }
        try {
            PurchaseOrder po = new PurchaseOrder();
            po.setPoNumber("PO-" + System.currentTimeMillis());
            po.setSupplierId(supplier.getId());
            po.setWarehouseId(SessionManager.getCurrentUser().getEmployeeId());
            po.setCreatedByUserId(SessionManager.getCurrentUser().getId());
            po.setExpectedDeliveryDate(deliveryDate);
            po.setNotes(notesArea.getText());
            inboundService.createPurchaseOrder(po);
            statusLabel.setText("Purchase Order created: " + po.getPoNumber());
            handleClear();
        } catch (DatabaseException e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    // Clears all input fields in the form
    @FXML
    private void handleClear() {
        supplierCombo.setValue(null);
        expectedDeliveryDate.setValue(null);
        notesArea.clear();
    }
}
