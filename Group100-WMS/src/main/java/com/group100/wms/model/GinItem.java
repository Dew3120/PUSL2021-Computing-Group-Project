package com.group100.wms.model;

/**
 * Represents a line item in a Goods Issued Note (GIN) — detailing which specific item,
 * how many units were issued, and at what unit cost.
 * 
 * OOP Concepts used in this class:
 * - Encapsulation: All fields are private; access and modification are controlled exclusively 
 *   through public getter and setter methods
 * - Abstraction: Provides a simple, focused interface for managing individual issued item details 
 *   without exposing internal storage
 */
public class GinItem {
    
    // Unique identifier for this line item record in the database
    private int id;
    
    // Foreign key linking this line item to its parent Goods Issued Note (GIN)
    private int ginId;
    
    // Foreign key referencing the Item that was issued
    private int itemId;
    
    // Number of units of this item that were issued in this transaction
    private int quantityIssued;
    
    // Unit cost applied for this issuance (used for costing/valuation purposes)
    private double unitCost;

    // Default constructor - useful for creating empty GinItem objects or for frameworks
    public GinItem() {}

    /**
     * Parameterized constructor to create a fully initialized GinItem object
     * @param id unique line item identifier
     * @param ginId ID of the parent Goods Issued Note
     * @param itemId ID of the issued item
     * @param quantityIssued number of units issued
     * @param unitCost unit cost applied for this issuance
     */
    public GinItem(int id, int ginId, int itemId, int quantityIssued, double unitCost) {
        this.id = id;
        this.ginId = ginId;
        this.itemId = itemId;
        this.quantityIssued = quantityIssued;
        this.unitCost = unitCost;
    }

    /**
     * Gets the unique identifier of this line item
     * @return line item ID
     */
    public int getId() { return id; }

    /**
     * Gets the ID of the parent Goods Issued Note this item belongs to
     * @return GIN ID
     */
    public int getGinId() { return ginId; }

    /**
     * Gets the ID of the item that was issued
     * @return item ID
     */
    public int getItemId() { return itemId; }

    /**
     * Gets the quantity of this item that was issued
     * @return issued quantity
     */
    public int getQuantityIssued() { return quantityIssued; }

    /**
     * Gets the unit cost used for this issuance
     * @return unit cost
     */
    public double getUnitCost() { return unitCost; }

    /**
     * Sets the unique identifier (typically used only by the persistence layer)
     * @param id the ID to set
     */
    public void setId(int id) { this.id = id; }

    /**
     * Sets or updates the parent Goods Issued Note reference
     * @param ginId GIN ID to set
     */
    public void setGinId(int ginId) { this.ginId = ginId; }

    /**
     * Sets or updates the referenced item
     * @param itemId item ID to set
     */
    public void setItemId(int itemId) { this.itemId = itemId; }

    /**
     * Sets or updates the issued quantity
     * @param quantityIssued quantity to set
     */
    public void setQuantityIssued(int quantityIssued) { this.quantityIssued = quantityIssued; }

    /**
     * Sets or updates the unit cost for this issued item
     * @param unitCost unit cost to set
     */
    public void setUnitCost(double unitCost) { this.unitCost = unitCost; }

    /**
     * Returns a string representation of the GinItem object (useful for logging/debugging)
     * @return string containing id, ginId, itemId, and quantityIssued
     */
    @Override
    public String toString() {
        return "GinItem{id=" + id + ", ginId=" + ginId
                + ", itemId=" + itemId + ", quantityIssued=" + quantityIssued + "}";
    }
}
