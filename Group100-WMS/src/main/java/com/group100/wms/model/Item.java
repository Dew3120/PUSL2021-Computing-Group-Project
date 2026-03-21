package com.group100.wms.model;

public class Item {
    private int id;
    private String sku, name, description, category, colour, unit;
    private int warehouseId;

    public Item() {}

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

    public int getId()             { return id; }
    public String getSku()         { return sku; }
    public String getName()        { return name; }
    public String getDescription() { return description; }
    public String getCategory()    { return category; }
    public String getColour()      { return colour; }
    public String getUnit()        { return unit; }
    public int getWarehouseId()    { return warehouseId; }

    public void setId(int id)                      { this.id = id; }
    public void setSku(String sku)                 { this.sku = sku; }
    public void setName(String name)               { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setCategory(String category)       { this.category = category; }
    public void setColour(String colour)           { this.colour = colour; }
    public void setUnit(String unit)               { this.unit = unit; }
    public void setWarehouseId(int warehouseId)    { this.warehouseId = warehouseId; }

    @Override
    public String toString() {
        return "Item{id=" + id + ", sku='" + sku + "', name='" + name + "'}";
    }
}