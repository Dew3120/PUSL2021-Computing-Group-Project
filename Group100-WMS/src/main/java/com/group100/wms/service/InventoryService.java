package com.group100.wms.service;

import com.group100.wms.core.AppConfig;
import com.group100.wms.core.AuditLogger;
import com.group100.wms.core.SessionManager;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.exception.StockShortageException;
import com.group100.wms.model.Batch;
import com.group100.wms.model.GinItem;
import com.group100.wms.model.Item;
import com.group100.wms.model.User;
import com.group100.wms.repository.BatchRepository;
import com.group100.wms.repository.ItemRepository;

import java.util.ArrayList;
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

    public int getStockLevel(int itemId, int warehouseId) throws DatabaseException {
        return itemRepository.getStockLevel(itemId, warehouseId);
    }

    public List<Item> getLowStockItems(int warehouseId) throws DatabaseException {
        List<Item> items = itemRepository.findByWarehouseId(warehouseId);
        return items.stream()
                .filter(item -> {
                    try {
                        return itemRepository.getStockLevel(item.getId(), warehouseId)
                                <= AppConfig.LOW_STOCK_THRESHOLD;
                    } catch (DatabaseException e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }

    public List<GinItem> deductStockFifo(int itemId, int warehouseId, int quantityNeeded)
            throws DatabaseException, StockShortageException {
        if (quantityNeeded <= 0) {
            throw new StockShortageException(itemId, quantityNeeded, getStockLevel(itemId, warehouseId));
        }

        int totalStock = itemRepository.getStockLevel(itemId, warehouseId);
        if (totalStock < quantityNeeded) {
            throw new StockShortageException(itemId, quantityNeeded, totalStock);
        }

        List<Batch> batches = batchRepository.findByItemFIFO(itemId);
        List<GinItem> allocations = new ArrayList<>();
        int remaining = quantityNeeded;

        for (Batch batch : batches) {
            if (remaining <= 0) break;
            int deduct = Math.min(batch.getAvailableQty(), remaining);
            batchRepository.updateAvailableQty(batch.getId(), batch.getAvailableQty() - deduct);

            GinItem allocation = new GinItem();
            allocation.setItemId(itemId);
            allocation.setBatchId(batch.getId());
            allocation.setQuantityIssued(deduct);
            allocation.setUnitCost(batch.getUnitCost());
            allocations.add(allocation);
            remaining -= deduct;
        }

        AuditLogger.log(currentUserId(),
                "DEDUCT_STOCK", "batches", String.valueOf(itemId),
                "FIFO deducted " + quantityNeeded + " units from itemId=" + itemId
                        + " in warehouseId=" + warehouseId);
        return allocations;
    }

    public void saveItem(Item item) throws DatabaseException {
        itemRepository.save(item);
        AuditLogger.log(currentUserId(),
                "CREATE", "items", String.valueOf(item.getId()),
                "Created item: " + item.getName());
    }

    public void updateItem(Item item) throws DatabaseException {
        itemRepository.update(item);
        AuditLogger.log(currentUserId(),
                "UPDATE", "items", String.valueOf(item.getId()),
                "Updated item: " + item.getName());
    }

    public Optional<Item> getItemById(int id) throws DatabaseException {
        return itemRepository.findById(id);
    }

    private int currentUserId() {
        User user = SessionManager.getCurrentUser();
        return user != null ? user.getId() : 0;
    }
}
