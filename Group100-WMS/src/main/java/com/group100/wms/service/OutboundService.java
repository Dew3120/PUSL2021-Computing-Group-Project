```java id="k92lm1"
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

// OOP Concepts Used:
// Encapsulation - Data and related operations are encapsulated within models and repositories.
// Abstraction - Service layer hides complex business logic from controllers/UI.
// Inheritance - Custom exceptions like DatabaseException and StockShortageException inherit from base Exception.
// Polymorphism - Repository methods may have multiple implementations with the same interface.

public class OutboundService {

    // Stores reference to GinRepository for handling Goods Issue Note (GIN) database operations
    private final GinRepository ginRepository;

    // Stores reference to InventoryService for handling stock deduction logic
    private final InventoryService inventoryService;

    // Constructor to initialize repository and inventory service dependencies
    public OutboundService(GinRepository ginRepository, InventoryService inventoryService) {
        this.ginRepository = ginRepository;
        this.inventoryService = inventoryService;
    }

    // Issues goods by deducting stock using FIFO, saving the GIN, and logging the transaction
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

    // Retrieves all Goods Issue Notes (GINs) from the database
    public List<GoodsIssueNote> getAllGins() throws DatabaseException {
        return ginRepository.findAll();
    }

    // Retrieves a specific GIN by its ID, wrapped in Optional to handle null safely
    public Optional<GoodsIssueNote> getGinById(int id) throws DatabaseException {
        return ginRepository.findById(id);
    }
}
```
