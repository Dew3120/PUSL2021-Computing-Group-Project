package com.group100.wms.model;

import java.time.LocalDate;

public class Forecast {
    private int id;
    private int itemId;
    private int warehouseId;
    private LocalDate forecastDate;
    private int forecastedQuantity;
    private double confidenceLower;
    private double confidenceUpper;
    private String modelUsed;

    // Display-only fields (not in DB, populated by JOIN queries)
    private String itemName;
    private String warehouseName;
    private double predictedQty;
    private double confidence;
    private String method;
    private LocalDate generatedDate;

    public Forecast() {}
    public Forecast(int id, int itemId, int warehouseId, LocalDate forecastDate,
                    int forecastedQuantity, double confidenceLower,
                    double confidenceUpper, String modelUsed) {
        this.id = id;
        this.itemId = itemId;
        this.warehouseId = warehouseId;
        this.forecastDate = forecastDate;
        this.forecastedQuantity = forecastedQuantity;
        this.confidenceLower = confidenceLower;
        this.confidenceUpper = confidenceUpper;
        this.modelUsed = modelUsed;
    }

    // Original getters/setters
    public int getId()                    { return id; }
    public int getItemId()                { return itemId; }
    public int getWarehouseId()           { return warehouseId; }
    public LocalDate getForecastDate()    { return forecastDate; }
    public int getForecastedQuantity()    { return forecastedQuantity; }
    public double getConfidenceLower()    { return confidenceLower; }
    public double getConfidenceUpper()    { return confidenceUpper; }
    public String getModelUsed()          { return modelUsed; }

    public void setId(int id)                              { this.id = id; }
    public void setItemId(int itemId)                      { this.itemId = itemId; }
    public void setWarehouseId(int warehouseId)            { this.warehouseId = warehouseId; }
    public void setForecastDate(LocalDate forecastDate)    { this.forecastDate = forecastDate; }
    public void setForecastedQuantity(int forecastedQuantity) { this.forecastedQuantity = forecastedQuantity; }
    public void setConfidenceLower(double confidenceLower) { this.confidenceLower = confidenceLower; }
    public void setConfidenceUpper(double confidenceUpper) { this.confidenceUpper = confidenceUpper; }
    public void setModelUsed(String modelUsed)             { this.modelUsed = modelUsed; }

    // New display-only getters/setters
    public String getItemName()              { return itemName; }
    public void setItemName(String v)        { this.itemName = v; }
    public String getWarehouseName()         { return warehouseName; }
    public void setWarehouseName(String v)   { this.warehouseName = v; }
    public double getPredictedQty()          { return predictedQty; }
    public void setPredictedQty(double v)    { this.predictedQty = v; }
    public double getConfidence()            { return confidence; }
    public void setConfidence(double v)      { this.confidence = v; }
    public String getMethod()                { return method; }
    public void setMethod(String v)          { this.method = v; }
    public LocalDate getGeneratedDate()      { return generatedDate; }
    public void setGeneratedDate(LocalDate v){ this.generatedDate = v; }

    // Alias for backward compat
    public int getForecastId()               { return id; }
    public void setForecastId(int v)         { this.id = v; }

    @Override
    public String toString() {
        return "Forecast{id=" + id + ", itemId=" + itemId
                + ", forecastDate=" + forecastDate
                + ", forecastedQuantity=" + forecastedQuantity + "}";
    }
}