package com.group100.wms.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Goods Received Note (GRN) — document recording the actual receipt
 * of goods/items from a supplier against a Purchase Order.
 * 
 * OOP Concepts used in this class:
 * - Encapsulation: All fields are private; access is controlled through public getters and setters
 * - Abstraction: Provides a high-level interface for managing received goods data
 * - Composition: Maintains a List<GrnItem> to represent the line items received in this GRN
 */
public class GoodsReceivedNote {
    
    // Unique identifier for this Goods Received Note in the database
    private int id;
    
    // Foreign key linking this GRN to the corresponding Purchase Order
    private int poId;
    
    // Foreign key indicating which warehouse received the goods
    private int warehouseId;
    
    // Foreign key referencing the Supplier from whom goods were received
    private int supplierId;
    
    // Date when the goods were physically received and this GRN was created/recorded
    private LocalDate receivedDate;
    
    // Current status of the GRN (e.g., "DRAFT", "COMPLETED", "VERIFIED", "PARTIAL")
    private String status;
    
    // ID of the User/Employee who received and recorded this GRN
    private int receivedBy;
    
    // Cached/denormalized name of the supplier (for display/UI/reporting convenience)
    private String supplierName;
    
    // List of line items detailing which items, quantities, and other details were received
    private List<GrnItem> items = new ArrayList<>();

    // Default constructor - useful for creating empty GRN objects or for frameworks
    public GoodsReceivedNote() {}

    /**
     * Parameterized constructor to create a fully initialized GoodsReceivedNote object
     * (without items — items can be added later via setItems() or add methods if implemented)
     * @param id unique GRN identifier
     * @param poId linked purchase order ID
     * @param warehouseId receiving warehouse ID
     * @param supplierId supplier ID
     * @param receivedDate date of receipt
     * @param status current GRN status
     * @param receivedBy user/employee ID who performed the receipt
     */
    public GoodsReceivedNote(int id, int poId, int warehouseId,
                             int supplierId, LocalDate receivedDate,
                             String status, int receivedBy) {
        this.id = id;
        this.poId = poId;
        this.warehouseId = warehouseId;
        this.supplierId = supplierId;
        this.receivedDate = receivedDate;
        this.status = status;
        this.receivedBy = receivedBy;
    }

    /**
     * Gets the unique identifier of this Goods Received Note
     * @return GRN ID
     */
    public int getId() { return id; }

    /**
     * Gets the linked Purchase Order ID
     * @return purchase order ID
     */
    public int getPoId() { return poId; }

    /**
     * Gets the ID of the warehouse where goods were received
     * @return warehouse ID
     */
    public int getWarehouseId() { return warehouseId; }

    /**
     * Gets the ID of the supplier
     * @return supplier ID
     */
    public int getSupplierId() { return supplierId; }

    /**
     * Gets the date when goods were received
     * @return receipt date
     */
    public LocalDate getReceivedDate() { return receivedDate; }

    /**
     * Gets the current status of this GRN
     * @return status string
     */
    public String getStatus() { return status; }

    /**
     * Gets the ID of the user/employee who received the goods
     * @return receiver user ID
     */
    public int getReceivedBy() { return receivedBy; }

    /**
     * Gets the cached supplier name (for display purposes)
     * @return supplier name
     */
    public String getSupplierName() { return supplierName; }

    /**
     * Gets the list of received items in this GRN
     * @return list of GrnItem objects
     */
    public List<GrnItem> getItems() { return items; }

    /**
     * Sets the unique identifier (typically used only by the persistence layer)
     * @param id the ID to set
     */
    public void setId(int id) { this.id = id; }

    /**
     * Sets or updates the associated Purchase Order
     * @param poId purchase order ID to set
     */
    public void setPoId(int poId) { this.poId = poId; }

    /**
     * Sets or updates the receiving warehouse
     * @param warehouseId warehouse ID to set
     */
    public void setWarehouseId(int warehouseId) { this.warehouseId = warehouseId; }

    /**
     * Sets or updates the supplier
     * @param supplierId supplier ID to set
     */
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    /**
     * Sets or updates the receipt date
     * @param d receipt date to set
     */
    public void setReceivedDate(LocalDate d) { this.receivedDate = d; }

    /**
     * Sets or updates the status of the GRN
     * @param status status to set
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * Sets or updates the user who received the goods
     * @param receivedBy receiver user ID to set
     */
    public void setReceivedBy(int receivedBy) { this.receivedBy = receivedBy; }

    /**
     * Sets or updates the cached supplier name
     * @param supplierName supplier name to set
     */
    public void setSupplierName(String supplierName){ this.supplierName = supplierName; }

    /**
     * Replaces the entire list of items in this GRN
     * @param items new list of GrnItem objects
     */
    public void setItems(List<GrnItem> items) { this.items = items; }

    /**
     * Returns a string representation of the GoodsReceivedNote object (useful for logging/debugging)
     * @return string containing id, poId, and status
     */
    @Override
    public String toString() {
        return "GoodsReceivedNote{id=" + id + ", poId=" + poId
                + ", status='" + status + "'}";
    }
}
