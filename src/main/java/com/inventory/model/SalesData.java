package com.inventory.model;

import javafx.beans.property.*;
import java.time.LocalDate;

public class SalesData {
    private final IntegerProperty id;
    private final IntegerProperty itemId;
    private final StringProperty itemName;
    private final IntegerProperty quantity;
    private final DoubleProperty revenue;
    private final ObjectProperty<LocalDate> saleDate;
    private final IntegerProperty sold;
    private final IntegerProperty remaining;

    public SalesData(int id, int itemId, String itemName, int quantity, double revenue, LocalDate saleDate) {
        this.id = new SimpleIntegerProperty(id);
        this.itemId = new SimpleIntegerProperty(itemId);
        this.itemName = new SimpleStringProperty(itemName);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.revenue = new SimpleDoubleProperty(revenue);
        this.saleDate = new SimpleObjectProperty<>(saleDate);
        this.sold = new SimpleIntegerProperty(0);
        this.remaining = new SimpleIntegerProperty(quantity);
    }

    // Getters and setters
    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }

    public int getItemId() { return itemId.get(); }
    public IntegerProperty itemIdProperty() { return itemId; }

    public String getItemName() { return itemName.get(); }
    public StringProperty itemNameProperty() { return itemName; }

    public int getQuantity() { return quantity.get(); }
    public IntegerProperty quantityProperty() { return quantity; }

    public double getRevenue() { return revenue.get(); }
    public DoubleProperty revenueProperty() { return revenue; }

    public LocalDate getSaleDate() { return saleDate.get(); }
    public ObjectProperty<LocalDate> saleDateProperty() { return saleDate; }

    public int getSold() { return sold.get(); }
    public void setSold(int value) { sold.set(value); }
    public IntegerProperty soldProperty() { return sold; }

    public int getRemaining() { return remaining.get(); }
    public void setRemaining(int value) { remaining.set(value); }
    public IntegerProperty remainingProperty() { return remaining; }
} 