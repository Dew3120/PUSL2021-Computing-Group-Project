package com.group100.wms.model;

public class GrnItem {
    private int id;
    private int grnId;
    private int itemId;
    private int quantity;
    private double unitCost;

    public GrnItem() {}

    public GrnItem(int id, int grnId, int itemId, int quantity, double unitCost) {
        this.id = id;
        this.grnId = grnId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.unitCost = unitCost;
    }

    public int getId()          { return id; }
    public int getGrnId()       { return grnId; }
    public int getItemId()      { return itemId; }
    public int getQuantity()    { return quantity; }
    public double getUnitCost() { return unitCost; }

    public void setId(int id)             { this.id = id; }
    public void setGrnId(int grnId)       { this.grnId = grnId; }
    public void setItemId(int itemId)     { this.itemId = itemId; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setUnitCost(double u)     { this.unitCost = u; }

    @Override
    public String toString() {
        return "GrnItem{id=" + id + ", grnId=" + grnId
                + ", itemId=" + itemId + ", quantity=" + quantity + "}";
    }
}