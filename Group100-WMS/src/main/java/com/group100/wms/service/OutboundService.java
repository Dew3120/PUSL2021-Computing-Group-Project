package com.group100.wms.service;

import com.group100.wms.core.AuditLogger;
import com.group100.wms.core.SessionManager;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.exception.StockShortageException;
import com.group100.wms.model.GinItem;
import com.group100.wms.model.GoodsIssueNote;
import com.group100.wms.repository.GinRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class OutboundService {

    private final GinRepository ginRepository;
    private final InventoryService inventoryService;

    public OutboundService(GinRepository ginRepository, InventoryService inventoryService) {
        this.ginRepository = ginRepository;
        this.inventoryService = inventoryService;
    }

    public GoodsIssueNote issueGoods(GoodsIssueNote gin, List<GinItem> items)
            throws DatabaseException, StockShortageException {
        for (GinItem item : items) {
            inventoryService.deductStockFifo(
                    item.getItemId(), gin.getWarehouseId(), item.getQuantityIssued());
        }
        gin.setIssuedDate(LocalDate.now());
        gin.setStatus("COMPLETED");
        ginRepository.save(gin);

        AuditLogger.log(SessionManager.getCurrentUser().getId(),
                "CREATE", "goods_issue_notes", String.valueOf(gin.getId()),
                "GIN created for warehouseId=" + gin.getWarehouseId());
        return gin;
    }

    public List<GoodsIssueNote> getAllGins() throws DatabaseException {
        return ginRepository.findAll();
    }

    public Optional<GoodsIssueNote> getGinById(int id) throws DatabaseException {
        return ginRepository.findById(id);
    }
}