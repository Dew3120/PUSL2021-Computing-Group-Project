package com.group100.wms.model;

/**
 * Represents an inventory item (product/goods) stored in the warehouse.
 * 
 * OOP Concepts used in this class:
 * - Encapsulation: All fields are private; access is controlled exclusively through public getter and setter methods
 * - Abstraction: Provides a clean, high-level interface for working with item data without exposing internal representation
 */
public class Item {
    
    // Unique identifier for the item in the database
    private int id;
    
    // Stock Keeping Unit - unique alphanumeric code to identify this specific item variant
    private String sku;
    
    // Human-readable name/title of the item
    private String name;
    
    // Detailed textual description of the item (features, specifications, etc.)
    private String description;
    
    // Product category or classification (e.g., "T-Shirts", "Footwear", "Accessories")
    private String category;
    
    // Colour variant of the item (e.g., "Red", "Black", "Navy Blue")
    private String colour;
    
    // Unit of measurement/sale (e.g., "Piece", "Pack", "Dozen", "Kg")
    private String unit;
    
    // Foreign key referencing the Warehouse where this item is primarily stored/managed
    private int warehouseId;

    // Default constructor - useful for creating empty item objects or for frameworks
    public Item() {}

    /**
     * Parameterized constructor to create a fully initialized Item object
     * @param id unique item identifier
     * @param sku stock keeping unit (unique code)
     * @param name item name/title
     * @param description detailed item description
     * @param category product category
     * @param colour colour variant
     * @param unit unit of measurement
     * @param warehouseId ID of the associated warehouse
     */
    public Item(int id, String sku, String name, String description,
                String category, String colour, String unit, int warehouseId) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.category = category;
        this.colour = colour;
        this.unit = unit;
        this.warehouseId = warehouseId;
    }

    /**
     * Gets the unique identifier of this item
     * @return the item ID
     */
    public int getId() { return id; }

    /**
     * Gets the Stock Keeping Unit (SKU) code
     * @return SKU string
     */
    public String getSku() { return sku; }

    /**
     * Gets the name of the item
     * @return item name
     */
    public String getName() { return name; }

    /**
     * Gets the detailed description of the item
     * @return item description
     */
    public String getDescription() { return description; }

    /**
     * Gets the category this item belongs to
     * @return category name
     */
    public String getCategory() { return category; }

    /**
     * Gets the colour variant of this item
     * @return colour
     */
    public String getColour() { return colour; }

    /**
     * Gets the unit of measurement for this item
     * @return unit (e.g., "Piece", "Pack")
     */
    public String getUnit() { return unit; }

    /**
     * Gets the ID of the warehouse this item is associated with
     * @return warehouse ID
     */
    public int getWarehouseId() { return warehouseId; }

    /**
     * Sets the unique identifier (typically used only by the persistence layer)
     * @param id the ID to set
     */
    public void setId(int id) { this.id = id; }

    /**
     * Sets or updates the SKU code
     * @param sku the SKU to set
     */
    public void setSku(String sku) { this.sku = sku; }

    /**
     * Sets or updates the item name
     * @param name the name to set
     */
    public void setName(String name) { this.name = name; }

    /**
     * Sets or updates the item description
     * @param description the description to set
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Sets or updates the item category
     * @param category the category to set
     */
    public void setCategory(String category) { this.category = category; }

    /**
     * Sets or updates the colour variant
     * @param colour the colour to set
     */
    public void setColour(String colour) { this.colour = colour; }

    /**
     * Sets or updates the unit of measurement
     * @param unit the unit to set
     */
    public void setUnit(String unit) { this.unit = unit; }

    /**
     * Assigns or updates the associated warehouse
     * @param warehouseId the warehouse ID to set
     */
    public void setWarehouseId(int warehouseId) { this.warehouseId = warehouseId; }

    /**
     * Returns a string representation of the Item object (useful for logging/debugging)
     * @return string containing id, sku, and name
     */
    @Override
    public String toString() {
        return "Item{id=" + id + ", sku='" + sku + "', name='" + name + "'}";
    }
}
