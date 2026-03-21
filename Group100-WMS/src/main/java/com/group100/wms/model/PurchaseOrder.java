package com.group100.wms.model;

import java.time.LocalDate;

public class PurchaseOrder {
    private int id;
    private String poNumber;
    private int supplierId;
    private int warehouseId;
    private int createdByUserId;
    private LocalDate orderDate;
    private LocalDate expectedDeliveryDate;
    private String status;
    private String notes;

    public PurchaseOrder() {}
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

    public int getId()                          { return id; }
    public String getPoNumber()                 { return poNumber; }
    public int getSupplierId()                  { return supplierId; }
    public int getWarehouseId()                 { return warehouseId; }
    public int getCreatedByUserId()             { return createdByUserId; }
    public LocalDate getOrderDate()             { return orderDate; }
    public LocalDate getExpectedDeliveryDate()  { return expectedDeliveryDate; }
    public String getStatus()                   { return status; }
    public String getNotes()                    { return notes; }

    public void setId(int id)                                        { this.id = id; }
    public void setPoNumber(String poNumber)                         { this.poNumber = poNumber; }
    public void setSupplierId(int supplierId)                        { this.supplierId = supplierId; }
    public void setWarehouseId(int warehouseId)                      { this.warehouseId = warehouseId; }
    public void setCreatedByUserId(int createdByUserId)              { this.createdByUserId = createdByUserId; }
    public void setOrderDate(LocalDate orderDate)                    { this.orderDate = orderDate; }
    public void setExpectedDeliveryDate(LocalDate expectedDeliveryDate) { this.expectedDeliveryDate = expectedDeliveryDate; }
    public void setStatus(String status)                             { this.status = status; }
    public void setNotes(String notes)                               { this.notes = notes; }

    @Override
    public String toString() {
        return "PurchaseOrder{id=" + id + ", poNumber='" + poNumber
                + "', status='" + status + "'}";
    }
}