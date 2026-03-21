package com.group100.wms.model;

import java.time.LocalDate;

public class ForecastHistory {
    private int historyId;
    private int itemId;
    private int warehouseId;
    private int forecastMonth;
    private int forecastYear;
    private double predictedQty;
    private double actualQty;
    private double accuracy;
    private String result;
    private double confidence;
    private String method;
    private LocalDate generatedDate;
    private String itemName;
    private String warehouseName;

    public ForecastHistory() {}

    public int getHistoryId() { return historyId; }
    public void setHistoryId(int v) { this.historyId = v; }
    public int getItemId() { return itemId; }
    public void setItemId(int v) { this.itemId = v; }
    public int getWarehouseId() { return warehouseId; }
    public void setWarehouseId(int v) { this.warehouseId = v; }
    public int getForecastMonth() { return forecastMonth; }
    public void setForecastMonth(int v) { this.forecastMonth = v; }
    public int getForecastYear() { return forecastYear; }
    public void setForecastYear(int v) { this.forecastYear = v; }
    public double getPredictedQty() { return predictedQty; }
    public void setPredictedQty(double v) { this.predictedQty = v; }
    public double getActualQty() { return actualQty; }
    public void setActualQty(double v) { this.actualQty = v; }
    public double getAccuracy() { return accuracy; }
    public void setAccuracy(double v) { this.accuracy = v; }
    public String getResult() { return result; }
    public void setResult(String v) { this.result = v; }
    public double getConfidence() { return confidence; }
    public void setConfidence(double v) { this.confidence = v; }
    public String getMethod() { return method; }
    public void setMethod(String v) { this.method = v; }
    public LocalDate getGeneratedDate() { return generatedDate; }
    public void setGeneratedDate(LocalDate v) { this.generatedDate = v; }
    public String getItemName() { return itemName; }
    public void setItemName(String v) { this.itemName = v; }
    public String getWarehouseName() { return warehouseName; }
    public void setWarehouseName(String v) { this.warehouseName = v; }
}