package com.group100.wms.model;

import java.time.LocalDate;

/**
 * Represents a purchase order placed with a supplier for receiving goods/items into the warehouse.
 * 
 * OOP Concepts used in this class:
 * - Encapsulation: All fields are private; access and modification are strictly controlled through 
 *   public getter and setter methods
 * - Abstraction: Provides a clean, high-level interface for working with purchase order data 
 *   without exposing internal implementation details
 */
public class PurchaseOrder {
    
    // Unique identifier for the purchase order in the database
    private int id;
    
    // Human-readable/prefix-based purchase order number (e.g., "PO-2025-00123")
    private String poNumber;
    
    // Foreign key referencing the Supplier this order was placed with
    private int supplierId;
    
    // Foreign key referencing the Warehouse where the ordered goods are expected to be delivered
    private int warehouseId;
    
    // Foreign key referencing the User (employee) who created/placed this purchase order
    private int createdByUserId;
    
    // Date when this purchase order was created/issued
    private LocalDate orderDate;
    
    // Expected date of delivery from the supplier
    private LocalDate expectedDeliveryDate;
    
    // Current status of the purchase order (e.g., "PENDING", "APPROVED", "RECEIVED", "CANCELLED")
    private String status;
    
    // Additional notes, special instructions, or internal remarks about this order
    private String notes;

    // Default constructor - useful for creating empty purchase order objects or for frameworks
    public PurchaseOrder() {}

    /**
     * Parameterized constructor to create a fully initialized PurchaseOrder object
     * @param id unique purchase order identifier
     * @param poNumber purchase order number/reference
     * @param supplierId supplier identifier
     * @param warehouseId destination warehouse identifier
     * @param createdByUserId user who created the order
     * @param orderDate date the order was placed
     * @param expectedDeliveryDate anticipated delivery date
     * @param status current status of the order
     * @param notes any additional notes or comments
     */
    public PurchaseOrder(int id, String poNumber, int supplierId, int warehouseId,
                         int createdByUserId, LocalDate orderDate,
                         LocalDate expectedDeliveryDate, String status, String notes) {
        this.id = id;
        this.poNumber = poNumber;
        this.supplierId = supplierId;
        this.warehouseId = warehouseId;
        this.createdByUserId = createdByUserId;
        this.orderDate = orderDate;
        this.expectedDeliveryDate = expectedDeliveryDate;
        this.status = status;
        this.notes = notes;
    }

    /**
     * Gets the unique identifier of this purchase order
     * @return purchase order ID
     */
    public int getId() { return id; }

    /**
     * Gets the purchase order number/reference
     * @return PO number
     */
    public String getPoNumber() { return poNumber; }

    /**
     * Gets the ID of the supplier this order was placed with
     * @return supplier ID
     */
    public int getSupplierId() { return supplierId; }

    /**
     * Gets the ID of the destination warehouse
     * @return warehouse ID
     */
    public int getWarehouseId() { return warehouseId; }

    /**
     * Gets the ID of the user who created this purchase order
     * @return creator user ID
     */
    public int getCreatedByUserId() { return createdByUserId; }

    /**
     * Gets the date this purchase order was created
     * @return order date
     */
    public LocalDate getOrderDate() { return orderDate; }

    /**
     * Gets the expected delivery date from the supplier
     * @return expected delivery date
     */
    public LocalDate getExpectedDeliveryDate() { return expectedDeliveryDate; }

    /**
     * Gets the current status of the purchase order
     * @return status string
     */
    public String getStatus() { return status; }

    /**
     * Gets any additional notes or comments associated with the order
     * @return notes
     */
    public String getNotes() { return notes; }

    /**
     * Sets the unique identifier (typically used only by the persistence layer)
     * @param id the ID to set
     */
    public void setId(int id) { this.id = id; }

    /**
     * Sets or updates the purchase order number
     * @param poNumber the PO number to set
     */
    public void setPoNumber(String poNumber) { this.poNumber = poNumber; }

    /**
     * Sets or updates the supplier for this order
     * @param supplierId supplier ID to set
     */
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    /**
     * Sets or updates the destination warehouse
     * @param warehouseId warehouse ID to set
     */
    public void setWarehouseId(int warehouseId) { this.warehouseId = warehouseId; }

    /**
     * Sets or updates the user who created this order
     * @param createdByUserId user ID to set
     */
    public void setCreatedByUserId(int createdByUserId) { this.createdByUserId = createdByUserId; }

    /**
     * Sets or updates the order creation date
     * @param orderDate order date to set
     */
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }

    /**
     * Sets or updates the expected delivery date
     * @param expectedDeliveryDate expected delivery date to set
     */
    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) { this.expectedDeliveryDate = expectedDeliveryDate; }

    /**
     * Sets or updates the current status of the purchase order
     * @param status status to set (e.g., "PENDING", "RECEIVED")
     */
    public void setStatus(String status) { this.status = status; }

    /**
     * Sets or updates any additional notes for this order
     * @param notes notes to set
     */
    public void setNotes(String notes) { this.notes = notes; }

    /**
     * Returns a string representation of the PurchaseOrder object (useful for logging/debugging)
     * @return string containing id, poNumber, and status
     */
    @Override
    public String toString() {
        return "PurchaseOrder{id=" + id + ", poNumber='" + poNumber
                + "', status='" + status + "'}";
    }
}
