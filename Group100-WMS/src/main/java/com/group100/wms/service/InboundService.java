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

public class InboundService {

    private final PurchaseOrderRepository poRepository;
    private final GrnRepository grnRepository;
    private final BatchRepository batchRepository;

    public InboundService(PurchaseOrderRepository poRepository,
                          GrnRepository grnRepository,
                          BatchRepository batchRepository) {
        this.poRepository = poRepository;
        this.grnRepository = grnRepository;
        this.batchRepository = batchRepository;
    }

    public PurchaseOrder createPurchaseOrder(PurchaseOrder po) throws DatabaseException {
        po.setStatus("PENDING");
        po.setOrderDate(LocalDate.now());
        poRepository.save(po);
        AuditLogger.log(SessionManager.getCurrentUser().getId(),
                "CREATE", "purchase_orders", String.valueOf(po.getId()),
                "Created PO for supplierId=" + po.getSupplierId());
        return po;
    }

    public List<PurchaseOrder> getAllPurchaseOrders() throws DatabaseException {
        return poRepository.findAll();
    }

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

    public List<GoodsReceivedNote> getAllGrns() throws DatabaseException {
        return grnRepository.findAll();
    }

    public Optional<GoodsReceivedNote> getGrnById(int id) throws DatabaseException {
        return grnRepository.findById(id);
    }
}