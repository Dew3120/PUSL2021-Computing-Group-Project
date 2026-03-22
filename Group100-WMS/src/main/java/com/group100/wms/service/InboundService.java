```java
package com.group100.wms.service;

import com.group100.wms.core.AuditLogger;
import com.group100.wms.core.SessionManager;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.*;
import com.group100.wms.repository.BatchRepository;
import com.group100.wms.repository.GrnRepository;
import com.group100.wms.repository.PurchaseOrderRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

// OOP Concepts Used:
// Encapsulation - Data and related operations are encapsulated within model and repository classes.
// Abstraction - This service layer hides business logic details from the UI/controller.
// Inheritance - Custom exceptions like DatabaseException likely inherit from base Exception class.
// Polymorphism - Repository interfaces may have different implementations with same method signatures.

public class InboundService {

    // Stores reference to PurchaseOrderRepository for handling purchase order database operations
    private final PurchaseOrderRepository poRepository;

    // Stores reference to GrnRepository for handling Goods Received Note (GRN) operations
    private final GrnRepository grnRepository;

    // Stores reference to BatchRepository for handling batch-related database operations
    private final BatchRepository batchRepository;

    // Constructor to initialize repositories used in inbound operations
    public InboundService(PurchaseOrderRepository poRepository,
                          GrnRepository grnRepository,
                          BatchRepository batchRepository) {
        this.poRepository = poRepository;
        this.grnRepository = grnRepository;
        this.batchRepository = batchRepository;
    }

    // Creates a new Purchase Order, sets initial status and date, and logs the action
    public PurchaseOrder createPurchaseOrder(PurchaseOrder po) throws DatabaseException {
        po.setStatus("PENDING");
        po.setOrderDate(LocalDate.now());
        poRepository.save(po);
        AuditLogger.log(SessionManager.getCurrentUser().getId(),
                "CREATE", "purchase_orders", String.valueOf(po.getId()),
                "Created PO for supplierId=" + po.getSupplierId());
        return po;
    }

    // Retrieves all purchase orders from the database
    public List<PurchaseOrder> getAllPurchaseOrders() throws DatabaseException {
        return poRepository.findAll();
    }

    // Processes received goods by creating a GRN, generating batches, and updating PO status
    public GoodsReceivedNote receiveGoods(GoodsReceivedNote grn, List<GrnItem> items)
            throws DatabaseException {
        grn.setReceivedDate(LocalDate.now());
        grn.setStatus("ACCEPTED");
        grnRepository.save(grn);

        for (GrnItem grnItem : items) {
            grnItem.setGrnId(grn.getId());

            Batch batch = new Batch();
            batch.setPoId(grn.getPoId());
            batch.setItemId(grnItem.getItemId());
            batch.setQuantity(grnItem.getQuantity());
            batch.setAvailableQty(grnItem.getQuantity());
            batch.setUnitCost(grnItem.getUnitCost());
            batch.setReceiptDate(LocalDate.now());
            batchRepository.save(batch);
        }

        poRepository.updateStatus(grn.getPoId(), "RECEIVED");
        AuditLogger.log(SessionManager.getCurrentUser().getId(),
                "CREATE", "goods_received_notes", String.valueOf(grn.getId()),
                "GRN created for poId=" + grn.getPoId());
        return grn;
    }

    // Retrieves all Goods Received Notes (GRNs) from the database
    public List<GoodsReceivedNote> getAllGrns() throws DatabaseException {
        return grnRepository.findAll();
    }

    // Retrieves a specific GRN by its ID, wrapped in Optional to handle null values safely
    public Optional<GoodsReceivedNote> getGrnById(int id) throws DatabaseException {
        return grnRepository.findById(id);
    }
}
```
