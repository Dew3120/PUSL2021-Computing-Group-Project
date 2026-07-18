package com.group100.wms.model;

public class GinItem {
    private int id;
    private int ginId;
    private int itemId;
    private int batchId;
    private int quantityIssued;
    private double unitCost;

    public GinItem() {}

    public GinItem(int id, int ginId, int itemId, int quantityIssued, double unitCost) {
        this(id, ginId, itemId, 0, quantityIssued, unitCost);
    }

    public GinItem(int id, int ginId, int itemId, int batchId, int quantityIssued, double unitCost) {
        this.id = id;
        this.ginId = ginId;
        this.itemId = itemId;
        this.batchId = batchId;
        this.quantityIssued = quantityIssued;
        this.unitCost = unitCost;
    }

    public int getId()             { return id; }
    public int getGinId()          { return ginId; }
    public int getItemId()         { return itemId; }
    public int getBatchId()        { return batchId; }
    public int getQuantityIssued() { return quantityIssued; }
    public double getUnitCost()    { return unitCost; }

    public void setId(int id)                         { this.id = id; }
    public void setGinId(int ginId)                   { this.ginId = ginId; }
    public void setItemId(int itemId)                 { this.itemId = itemId; }
    public void setBatchId(int batchId)               { this.batchId = batchId; }
    public void setQuantityIssued(int quantityIssued) { this.quantityIssued = quantityIssued; }
    public void setUnitCost(double unitCost)          { this.unitCost = unitCost; }

    @Override
    public String toString() {
        return "GinItem{id=" + id + ", ginId=" + ginId
                + ", itemId=" + itemId + ", batchId=" + batchId
                + ", quantityIssued=" + quantityIssued + "}";
    }
}
