package com.group100.wms.model;

import java.time.LocalDate;

// OOP Concepts: Encapsulation (private fields with public getters/setters), Abstraction (complex historical data),
// Inheritance (extends object pattern), Polymorphism (multiple accessors)
public class ForecastHistory {
    // Unique identifier for forecast history record
    private int historyId;
    // Foreign key reference to inventory item
    private int itemId;
    // Foreign key reference to warehouse location
    private int warehouseId;
    // Month (1-12) for which forecast was made
    private int forecastMonth;
    // Year in which forecast was made
    private int forecastYear;
    // Quantity that was predicted
    private double predictedQty;
    // Actual quantity that occurred
    private double actualQty;
    // Calculated accuracy percentage (predicted vs actual)
    private double accuracy;
    // Result classification (HIT, FAIR, MISS)
    private String result;
    // Confidence level of the forecast
    private double confidence;
    // Forecasting method or model used
    private String method;
    // Date when the forecast was generated
    private LocalDate generatedDate;
    // Item name retrieved from items table
    private String itemName;
    // Warehouse name retrieved from warehouses table
    private String warehouseName;

    // Default constructor for creating new ForecastHistory instance
    public ForecastHistory() {}

    // Getter for forecast history record identifier
    public int getHistoryId() { return historyId; }
    // Setter for forecast history record identifier
    public void setHistoryId(int v) { this.historyId = v; }
    // Getter for inventory item identifier
    public int getItemId() { return itemId; }
    // Setter for inventory item identifier
    public void setItemId(int v) { this.itemId = v; }
    // Getter for warehouse identifier
    public int getWarehouseId() { return warehouseId; }
    // Setter for warehouse identifier
    public void setWarehouseId(int v) { this.warehouseId = v; }
    // Getter for forecast month
    public int getForecastMonth() { return forecastMonth; }
    // Setter for forecast month
    public void setForecastMonth(int v) { this.forecastMonth = v; }
    // Getter for forecast year
    public int getForecastYear() { return forecastYear; }
    // Setter for forecast year
    public void setForecastYear(int v) { this.forecastYear = v; }
    // Getter for predicted quantity
    public double getPredictedQty() { return predictedQty; }
    // Setter for predicted quantity
    public void setPredictedQty(double v) { this.predictedQty = v; }
    // Getter for actual quantity
    public double getActualQty() { return actualQty; }
    // Setter for actual quantity
    public void setActualQty(double v) { this.actualQty = v; }
    // Getter for forecast accuracy percentage
    public double getAccuracy() { return accuracy; }
    // Setter for forecast accuracy percentage
    public void setAccuracy(double v) { this.accuracy = v; }
    // Getter for result classification
    public String getResult() { return result; }
    // Setter for result classification
    public void setResult(String v) { this.result = v; }
    // Getter for confidence level
    public double getConfidence() { return confidence; }
    // Setter for confidence level
    public void setConfidence(double v) { this.confidence = v; }
    // Getter for forecasting method name
    public String getMethod() { return method; }
    // Setter for forecasting method name
    public void setMethod(String v) { this.method = v; }
    // Getter for generation date
    public LocalDate getGeneratedDate() { return generatedDate; }
    // Setter for generation date
    public void setGeneratedDate(LocalDate v) { this.generatedDate = v; }
    // Getter for display item name
    public String getItemName() { return itemName; }
    // Setter for display item name
    public void setItemName(String v) { this.itemName = v; }
    // Getter for display warehouse name
    public String getWarehouseName() { return warehouseName; }
    // Setter for display warehouse name
    public void setWarehouseName(String v) { this.warehouseName = v; }
}