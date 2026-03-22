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

// OOP Concepts used in this class:
// 1. Encapsulation: The class bundles inbound logistics logic and keeps repository dependencies private, controlling access through public methods.
// 2. Abstraction: It provides a high-level interface for complex workflows like "receiving goods," which involves multiple database tables (GRN, Batches, PO updates).
// 3. Inheritance: Implicitly used via custom exceptions like DatabaseException which extend the base Exception class.
public class InboundService {

    // Stores the repository instance for handling Purchase Order (PO) data operations
    private final PurchaseOrderRepository poRepository;

    // Stores the repository instance for handling Goods Received Note (GRN) data operations
    private final GrnRepository grnRepository;

    // Stores the repository instance for managing stock batches in the warehouse
    private final BatchRepository batchRepository;

    // Constructor to inject the required repository dependencies for inbound processing
    public InboundService(PurchaseOrderRepository poRepository,
                          GrnRepository grnRepository,
                          BatchRepository batchRepository) {
        this.poRepository = poRepository;
        this.grnRepository = grnRepository;
        this.batchRepository = batchRepository;
    }

    // Creates a new Purchase Order, sets its initial status to PENDING, and logs the creation event
    public PurchaseOrder createPurchaseOrder(PurchaseOrder po) throws DatabaseException {
        po.setStatus("PENDING");
        po.setOrderDate(LocalDate.now());
        poRepository.save(po);
        AuditLogger.log(SessionManager.getCurrentUser().getId(),
                "CREATE", "purchase_orders", String.valueOf(po.getId()),
                "Created PO for supplierId=" + po.getSupplierId());
        return po;
    }

    // Retrieves a complete list of all Purchase Orders currently in the system
    public List<PurchaseOrder> getAllPurchaseOrders() throws DatabaseException {
        return poRepository.findAll();
    }

    // Handles the workflow of receiving inventory: saves the GRN, creates individual batches for items, and updates the PO status
    public GoodsReceivedNote receiveGoods(GoodsReceivedNote grn, List<GrnItem> items)
            throws DatabaseException {
        grn.setReceivedDate(LocalDate.now());
        grn.setStatus("ACCEPTED");
        grnRepository.save(grn);

        // Iterates through the list of received items to create searchable inventory batches
        for (GrnItem grnItem : items) {
            grnItem.setGrnId(grn.getId());

            // Stores the temporary Batch object being prepared for database insertion
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

    // Retrieves all Goods Received Notes from the database
    public List<GoodsReceivedNote> getAllGrns() throws DatabaseException {
        return grnRepository.findAll();
    }

    // Finds a specific Goods Received Note by its unique ID
    public Optional<GoodsReceivedNote> getGrnById(int id) throws DatabaseException {
        return grnRepository.findById(id);
    }
}
