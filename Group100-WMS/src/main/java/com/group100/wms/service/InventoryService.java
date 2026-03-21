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

public class InventoryService {

    private final ItemRepository itemRepository;
    private final BatchRepository batchRepository;

    public InventoryService(ItemRepository itemRepository, BatchRepository batchRepository) {
        this.itemRepository = itemRepository;
        this.batchRepository = batchRepository;
    }

    public List<Item> getAllItems() throws DatabaseException {
        return itemRepository.findAll();
    }

    public List<Item> getItemsByWarehouse(int warehouseId) throws DatabaseException {
        return itemRepository.findByWarehouseId(warehouseId);
    }

    public int getStockLevel(int itemId) throws DatabaseException {
        return itemRepository.getStockLevel(itemId);
    }

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

    public void saveItem(Item item) throws DatabaseException {
        itemRepository.save(item);
        AuditLogger.log(SessionManager.getCurrentUser().getId(),
                "CREATE", "items", String.valueOf(item.getId()),
                "Created item: " + item.getName());
    }

    public void updateItem(Item item) throws DatabaseException {
        itemRepository.update(item);
        AuditLogger.log(SessionManager.getCurrentUser().getId(),
                "UPDATE", "items", String.valueOf(item.getId()),
                "Updated item: " + item.getName());
    }

    public Optional<Item> getItemById(int id) throws DatabaseException {
        return itemRepository.findById(id);
    }
}