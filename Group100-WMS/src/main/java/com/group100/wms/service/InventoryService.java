```java
package com.group100.wms.service;

import com.group100.wms.core.AppConfig;
import com.group100.wms.core.AuditLogger;
import com.group100.wms.core.SessionManager;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.exception.StockShortageException;
import com.group100.wms.model.Batch;
import com.group100.wms.model.Item;
import com.group100.wms.repository.BatchRepository;
import com.group100.wms.repository.ItemRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// OOP Concepts Used:
// Encapsulation - Data and methods are bundled within classes like Item, Batch, and repositories.
// Abstraction - Service layer abstracts business logic from controllers/UI.
// Inheritance - Exceptions like DatabaseException and StockShortageException likely extend base Exception.
// Polymorphism - Repository methods (e.g., findAll, findById) may have different implementations.

public class InventoryService {

    // Stores reference to ItemRepository for performing item-related database operations
    private final ItemRepository itemRepository;

    // Stores reference to BatchRepository for performing batch-related database operations
    private final BatchRepository batchRepository;

    // Constructor to initialize repositories used in the service
    public InventoryService(ItemRepository itemRepository, BatchRepository batchRepository) {
        this.itemRepository = itemRepository;
        this.batchRepository = batchRepository;
    }

    // Retrieves all items from the database
    public List<Item> getAllItems() throws DatabaseException {
        return itemRepository.findAll();
    }

    // Retrieves all items that belong to a specific warehouse
    public List<Item> getItemsByWarehouse(int warehouseId) throws DatabaseException {
        return itemRepository.findByWarehouseId(warehouseId);
    }

    // Returns the total stock level of a given item
    public int getStockLevel(int itemId) throws DatabaseException {
        return itemRepository.getStockLevel(itemId);
    }

    // Retrieves items in a warehouse that are below or equal to the low stock threshold
    public List<Item> getLowStockItems(int warehouseId) throws DatabaseException {
        List<Item> items = itemRepository.findByWarehouseId(warehouseId);
        return items.stream()
                .filter(item -> {
                    try {
                        return itemRepository.getStockLevel(item.getId())
                                <= AppConfig.LOW_STOCK_THRESHOLD;
                    } catch (DatabaseException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    // Deducts stock using FIFO (First-In-First-Out) method across batches
    public void deductStockFifo(int itemId, int warehouseId, int quantityNeeded)
            throws DatabaseException, StockShortageException {
        int totalStock = itemRepository.getStockLevel(itemId);
        if (totalStock < quantityNeeded)
            throw new StockShortageException(itemId, quantityNeeded, totalStock);

        List<Batch> batches = batchRepository.findByItemFIFO(itemId);
        int remaining = quantityNeeded;
        for (Batch batch : batches) {
            if (remaining <= 0) break;
            int deduct = Math.min(batch.getAvailableQty(), remaining);
            batchRepository.updateAvailableQty(batch.getId(),
                    batch.getAvailableQty() - deduct);
            remaining -= deduct;
        }
        AuditLogger.log(SessionManager.getCurrentUser().getId(),
                "DEDUCT_STOCK", "batches", String.valueOf(itemId),
                "FIFO deducted " + quantityNeeded + " units from itemId=" + itemId);
    }

    // Saves a new item into the database and logs the creation
    public void saveItem(Item item) throws DatabaseException {
        itemRepository.save(item);
        AuditLogger.log(SessionManager.getCurrentUser().getId(),
                "CREATE", "items", String.valueOf(item.getId()),
                "Created item: " + item.getName());
    }

    // Updates an existing item in the database and logs the update action
    public void updateItem(Item item) throws DatabaseException {
        itemRepository.update(item);
        AuditLogger.log(SessionManager.getCurrentUser().getId(),
                "UPDATE", "items", String.valueOf(item.getId()),
                "Updated item: " + item.getName());
    }

    // Retrieves a single item by its ID (returns Optional to handle null safely)
    public Optional<Item> getItemById(int id) throws DatabaseException {
        return itemRepository.findById(id);
    }
}
```
