package com.inventory.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class ForecastResult {
    private final ObjectProperty<LocalDate> date;
    private final DoubleProperty forecastValue;
    private final DoubleProperty lowerBound;
    private final DoubleProperty upperBound;
    private final StringProperty modelType;
    private double forecastedQuantity;
    private double assumedSalesPrice;
    private String productName;
    
    public ForecastResult(LocalDate date, double forecastValue, double lowerBound, double upperBound, String modelType) {
        this.date = new SimpleObjectProperty<>(date);
        this.forecastValue = new SimpleDoubleProperty(forecastValue);
        this.lowerBound = new SimpleDoubleProperty(lowerBound);
        this.upperBound = new SimpleDoubleProperty(upperBound);
        this.modelType = new SimpleStringProperty(modelType);
    }
    
    public ForecastResult(LocalDate date, double forecastedQuantity, double assumedSalesPrice, String productName) {
        this.date = new SimpleObjectProperty<>(date);
        this.forecastValue = new SimpleDoubleProperty(0.0);
        this.lowerBound = new SimpleDoubleProperty(0.0);
        this.upperBound = new SimpleDoubleProperty(0.0);
        this.modelType = new SimpleStringProperty("");
        this.forecastedQuantity = forecastedQuantity;
        this.assumedSalesPrice = assumedSalesPrice;
        this.productName = productName;
    }
    
    // Getters and property accessors
    public LocalDate getDate() { return date.get(); }
    public ObjectProperty<LocalDate> dateProperty() { return date; }
    
    public double getForecastValue() { return forecastValue.get(); }
    public DoubleProperty forecastValueProperty() { return forecastValue; }
    
    public double getLowerBound() { return lowerBound.get(); }
    public DoubleProperty lowerBoundProperty() { return lowerBound; }
    
    public double getUpperBound() { return upperBound.get(); }
    public DoubleProperty upperBoundProperty() { return upperBound; }
    
    public String getModelType() { return modelType.get(); }
    public StringProperty modelTypeProperty() { return modelType; }

    // Getters and setters
    public double getForecastedQuantity() { return forecastedQuantity; }
    public void setForecastedQuantity(double forecastedQuantity) { this.forecastedQuantity = forecastedQuantity; }
    public double getAssumedSalesPrice() { return assumedSalesPrice; }
    public void setAssumedSalesPrice(double assumedSalesPrice) { this.assumedSalesPrice = assumedSalesPrice; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
} 