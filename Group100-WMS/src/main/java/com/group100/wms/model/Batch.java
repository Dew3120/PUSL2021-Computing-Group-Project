package com.group100.wms.model;

import java.time.LocalDate;

public class Batch {
    private int id;
    private int poId;
    private int itemId;
    private int quantity;
    private int availableQty;
    private double unitCost;
    private LocalDate receiptDate;

    public Batch() {}

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

    public int getId()               { return id; }
    public int getPoId()             { return poId; }
    public int getItemId()           { return itemId; }
    public int getQuantity()         { return quantity; }
    public int getAvailableQty()     { return availableQty; }
    public double getUnitCost()      { return unitCost; }
    public LocalDate getReceiptDate(){ return receiptDate; }

    public void setId(int id)                     { this.id = id; }
    public void setPoId(int poId)                 { this.poId = poId; }
    public void setItemId(int itemId)             { this.itemId = itemId; }
    public void setQuantity(int quantity)         { this.quantity = quantity; }
    public void setAvailableQty(int availableQty) { this.availableQty = availableQty; }
    public void setUnitCost(double unitCost)      { this.unitCost = unitCost; }
    public void setReceiptDate(LocalDate d)       { this.receiptDate = d; }

    public void deduct(int qty) { this.availableQty -= qty; }

    @Override
    public String toString() {
        return "Batch{id=" + id + ", itemId=" + itemId
                + ", availableQty=" + availableQty
                + ", receiptDate=" + receiptDate + "}";
    }
}