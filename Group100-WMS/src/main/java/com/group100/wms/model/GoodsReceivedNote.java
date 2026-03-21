package com.group100.wms.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GoodsReceivedNote {
    private int id;
    private int poId;
    private int warehouseId;
    private int supplierId;
    private LocalDate receivedDate;
    private String status;
    private int receivedBy;
    private String supplierName;
    private List<GrnItem> items = new ArrayList<>();

    public GoodsReceivedNote() {}

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

    public int getId()                  { return id; }
    public int getPoId()                { return poId; }
    public int getWarehouseId()         { return warehouseId; }
    public int getSupplierId()          { return supplierId; }
    public LocalDate getReceivedDate()  { return receivedDate; }
    public String getStatus()           { return status; }
    public int getReceivedBy()          { return receivedBy; }
    public String getSupplierName()     { return supplierName; }
    public List<GrnItem> getItems()     { return items; }

    public void setId(int id)                       { this.id = id; }
    public void setPoId(int poId)                   { this.poId = poId; }
    public void setWarehouseId(int warehouseId)     { this.warehouseId = warehouseId; }
    public void setSupplierId(int supplierId)       { this.supplierId = supplierId; }
    public void setReceivedDate(LocalDate d)        { this.receivedDate = d; }
    public void setStatus(String status)            { this.status = status; }
    public void setReceivedBy(int receivedBy)       { this.receivedBy = receivedBy; }
    public void setSupplierName(String supplierName){ this.supplierName = supplierName; }
    public void setItems(List<GrnItem> items)       { this.items = items; }

    @Override
    public String toString() {
        return "GoodsReceivedNote{id=" + id + ", poId=" + poId
                + ", status='" + status + "'}";
    }
}