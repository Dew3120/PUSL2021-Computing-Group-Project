package com.group100.wms.model;

import java.time.LocalDate;

// OOP Concepts: Encapsulation (private fields with public getters/setters), Abstraction (complex forecast data),
// Inheritance (extends object pattern), Polymorphism (multiple constructors)
public class Forecast {
    // Unique identifier for forecast record
    private int id;
    // Foreign key reference to inventory item
    private int itemId;
    // Foreign key reference to warehouse location
    private int warehouseId;
    // Predicted date for which forecast was generated
    private LocalDate forecastDate;
    // Quantity predicted by the forecasting model
    private int forecastedQuantity;
    // Lower bound of confidence interval
    private double confidenceLower;
    // Upper bound of confidence interval
    private double confidenceUpper;
    // AI model or method used to generate forecast
    private String modelUsed;

    // Display-only fields (not in DB, populated by JOIN queries)
    // Item name retrieved from items table via JOIN
    private String itemName;
    // Warehouse name retrieved from warehouses table via JOIN
    private String warehouseName;
    // Predicted quantity for display purposes
    private double predictedQty;
    // Confidence metric displayed in dashboard
    private double confidence;
    // Forecasting method displayed in UI
    private String method;
    // Generated date displayed to user
    private LocalDate generatedDate;

    // Default constructor initializing new Forecast instance
    public Forecast() {}
    // Constructor initializing Forecast with all core database field values
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

    // Getter for unique forecast identifier
    public int getId()                    { return id; }
    // Getter for inventory item identifier
    public int getItemId()                { return itemId; }
    // Getter for warehouse identifier
    public int getWarehouseId()           { return warehouseId; }
    // Getter for forecast date
    public LocalDate getForecastDate()    { return forecastDate; }
    // Getter for forecasted quantity value
    public int getForecastedQuantity()    { return forecastedQuantity; }
    // Getter for lower confidence interval bound
    public double getConfidenceLower()    { return confidenceLower; }
    // Getter for upper confidence interval bound
    public double getConfidenceUpper()    { return confidenceUpper; }
    // Getter for model or method name used for forecasting
    public String getModelUsed()          { return modelUsed; }

    // Setter for unique forecast identifier
    public void setId(int id)                              { this.id = id; }
    // Setter for inventory item identifier
    public void setItemId(int itemId)                      { this.itemId = itemId; }
    // Setter for warehouse identifier
    public void setWarehouseId(int warehouseId)            { this.warehouseId = warehouseId; }
    // Setter for forecast date
    public void setForecastDate(LocalDate forecastDate)    { this.forecastDate = forecastDate; }
    // Setter for forecasted quantity value
    public void setForecastedQuantity(int forecastedQuantity) { this.forecastedQuantity = forecastedQuantity; }
    // Setter for lower confidence interval bound
    public void setConfidenceLower(double confidenceLower) { this.confidenceLower = confidenceLower; }
    // Setter for upper confidence interval bound
    public void setConfidenceUpper(double confidenceUpper) { this.confidenceUpper = confidenceUpper; }
    // Setter for model or method name used for forecasting
    public void setModelUsed(String modelUsed)             { this.modelUsed = modelUsed; }

    // Getter for display item name
    public String getItemName()              { return itemName; }
    // Setter for display item name
    public void setItemName(String v)        { this.itemName = v; }
    // Getter for display warehouse name
    public String getWarehouseName()         { return warehouseName; }
    // Setter for display warehouse name
    public void setWarehouseName(String v)   { this.warehouseName = v; }
    // Getter for display predicted quantity
    public double getPredictedQty()          { return predictedQty; }
    // Setter for display predicted quantity
    public void setPredictedQty(double v)    { this.predictedQty = v; }
    // Getter for display confidence metric
    public double getConfidence()            { return confidence; }
    // Setter for display confidence metric
    public void setConfidence(double v)      { this.confidence = v; }
    // Getter for display method name
    public String getMethod()                { return method; }
    // Setter for display method name
    public void setMethod(String v)          { this.method = v; }
    // Getter for display generated date
    public LocalDate getGeneratedDate()      { return generatedDate; }
    // Setter for display generated date
    public void setGeneratedDate(LocalDate v){ this.generatedDate = v; }

    // Alias getter for forecast ID (backward compatibility)
    public int getForecastId()               { return id; }
    // Alias setter for forecast ID (backward compatibility)
    public void setForecastId(int v)         { this.id = v; }

    // Returns string representation of Forecast object for debugging
    @Override
    public String toString() {
        return "Forecast{id=" + id + ", itemId=" + itemId
                + ", forecastDate=" + forecastDate
                + ", forecastedQuantity=" + forecastedQuantity + "}";
    }
}