package com.group100.wms.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class GoodsIssueNote {
    private int id;
    private int warehouseId;
    private String destination;
    private String destType;
    private int issuedBy;
    private LocalDate issuedDate;
    private String status;
    private List<GinItem> items = new ArrayList<>();

    public GoodsIssueNote() {}

    public GoodsIssueNote(int id, int warehouseId, String destination,
                          String destType, int issuedBy,
                          LocalDate issuedDate, String status) {
        this.id = id;
        this.warehouseId = warehouseId;
        this.destination = destination;
        this.destType = destType;
        this.issuedBy = issuedBy;
        this.issuedDate = issuedDate;
        this.status = status;
    }

    public int getId()               { return id; }
    public int getWarehouseId()      { return warehouseId; }
    public String getDestination()   { return destination; }
    public String getDestType()      { return destType; }
    public int getIssuedBy()         { return issuedBy; }
    public LocalDate getIssuedDate() { return issuedDate; }
    public String getStatus()        { return status; }
    public List<GinItem> getItems()  { return items; }

    public void setId(int id)                   { this.id = id; }
    public void setWarehouseId(int warehouseId) { this.warehouseId = warehouseId; }
    public void setDestination(String d)        { this.destination = d; }
    public void setDestType(String destType)    { this.destType = destType; }
    public void setIssuedBy(int issuedBy)       { this.issuedBy = issuedBy; }
    public void setIssuedDate(LocalDate d)      { this.issuedDate = d; }
    public void setStatus(String status)        { this.status = status; }
    public void setItems(List<GinItem> items)   { this.items = items; }

    @Override
    public String toString() {
        return "GoodsIssueNote{id=" + id + ", destination='"
                + destination + "', status='" + status + "'}";
    }
}