package com.group100.wms.model;

import java.time.LocalDate;

/**
 * Represents a batch of items received from a purchase order.
 * Tracks received quantity, remaining available quantity, cost, and receipt date.
 * 
 * OOP Concepts used in this class:
 * - Encapsulation: All fields are private; external classes interact only through public getters, setters, 
 *   and the controlled deduct() method
 * - Abstraction: Exposes a simple interface for managing batch inventory data and performing 
 *   quantity deductions without exposing internal logic
 */
public class Batch {
    
    // Unique identifier for this batch record in the database
    private int id;
    
    // Foreign key referencing the Purchase Order this batch was received against
    private int poId;
    
    // Foreign key referencing the Item this batch contains
    private int itemId;
    
    // Total quantity originally received in this batch
    private int quantity;
    
    // Current remaining quantity available for allocation/sale/dispatch
    private int availableQty;
    
    // Unit cost (price per unit) at which this batch was purchased/received
    private double unitCost;
    
    // Date when this batch was physically received into the warehouse
    private LocalDate receiptDate;

    // Default constructor - useful for creating empty batch objects or for frameworks
    public Batch() {}

    /**
     * Parameterized constructor to create a fully initialized Batch object
     * @param id unique batch identifier
     * @param poId purchase order ID this batch belongs to
     * @param itemId item ID this batch contains
     * @param quantity total quantity received
     * @param availableQty initial available quantity (usually same as quantity on receipt)
     * @param unitCost cost per unit for this batch
     * @param receiptDate date the batch was received
     */
    public Batch(int id, int poId, int itemId, int quantity,
                 int availableQty, double unitCost, LocalDate receiptDate) {
        this.id = id;
        this.poId = poId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.availableQty = availableQty;
        this.unitCost = unitCost;
        this.receiptDate = receiptDate;
    }

    /**
     * Gets the unique identifier of this batch
     * @return batch ID
     */
    public int getId() { return id; }

    /**
     * Gets the purchase order this batch was received under
     * @return purchase order ID
     */
    public int getPoId() { return poId; }

    /**
     * Gets the item contained in this batch
     * @return item ID
     */
    public int getItemId() { return itemId; }

    /**
     * Gets the original total quantity received in this batch
     * @return total received quantity
     */
    public int getQuantity() { return quantity; }

    /**
     * Gets the current remaining quantity available in this batch
     * @return available quantity
     */
    public int getAvailableQty() { return availableQty; }

    /**
     * Gets the unit cost of items in this batch
     * @return unit cost
     */
    public double getUnitCost() { return unitCost; }

    /**
     * Gets the date this batch was received
     * @return receipt date
     */
    public LocalDate getReceiptDate(){ return receiptDate; }

    /**
     * Sets the unique identifier (typically used only by the persistence layer)
     * @param id the ID to set
     */
    public void setId(int id) { this.id = id; }

    /**
     * Sets or updates the associated purchase order ID
     * @param poId purchase order ID to set
     */
    public void setPoId(int poId) { this.poId = poId; }

    /**
     * Sets or updates the associated item ID
     * @param itemId item ID to set
     */
    public void setItemId(int itemId) { this.itemId = itemId; }

    /**
     * Sets or updates the original received quantity
     * @param quantity total quantity to set
     */
    public void setQuantity(int quantity) { this.quantity = quantity; }

    /**
     * Sets or updates the current available quantity
     * @param availableQty available quantity to set
     */
    public void setAvailableQty(int availableQty) { this.availableQty = availableQty; }

    /**
     * Sets or updates the unit cost for this batch
     * @param unitCost unit cost to set
     */
    public void setUnitCost(double unitCost) { this.unitCost = unitCost; }

    /**
     * Sets or updates the receipt date of this batch
     * @param d receipt date to set
     */
    public void setReceiptDate(LocalDate d) { this.receiptDate = d; }

    /**
     * Reduces the available quantity by the specified amount
     * (used when items from this batch are allocated, sold, or dispatched)
     * @param qty quantity to deduct from availableQty
     */
    public void deduct(int qty) { this.availableQty -= qty; }

    /**
     * Returns a string representation of the Batch object (useful for logging/debugging)
     * @return string containing id, itemId, availableQty, and receiptDate
     */
    @Override
    public String toString() {
        return "Batch{id=" + id + ", itemId=" + itemId
                + ", availableQty=" + availableQty
                + ", receiptDate=" + receiptDate + "}";
    }
}
