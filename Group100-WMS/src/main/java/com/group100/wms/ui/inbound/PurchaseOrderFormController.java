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

public class PurchaseOrderFormController {

    @FXML private ComboBox<Supplier> supplierCombo;
    @FXML private DatePicker expectedDeliveryDate;
    @FXML private TextArea notesArea;
    @FXML private Label statusLabel;

    private final InboundService inboundService = new InboundService(
            new PurchaseOrderRepository(), new GrnRepository(), new BatchRepository());
    private final SupplierRepository supplierRepository = new SupplierRepository();

    @FXML
    public void initialize() {
        loadSuppliers();
    }

    private void loadSuppliers() {
        try {
            List<Supplier> suppliers = supplierRepository.findAllActive();
            supplierCombo.setItems(FXCollections.observableArrayList(suppliers));
        } catch (DatabaseException e) {
            statusLabel.setText("Error loading suppliers: " + e.getMessage());
        }
    }

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

    @FXML
    private void handleClear() {
        supplierCombo.setValue(null);
        expectedDeliveryDate.setValue(null);
        notesArea.clear();
    }
}