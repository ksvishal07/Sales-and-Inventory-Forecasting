package com.inventory.model;

import javafx.beans.property.*;

public class InventoryItem {
    private final IntegerProperty id;
    private final StringProperty name;
    private final IntegerProperty quantity;
    private final DoubleProperty price;
    private final IntegerProperty reorderLevel;
    private final StringProperty category;

    public InventoryItem(int id, String name, int quantity, double price, int reorderLevel, String category) {
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.price = new SimpleDoubleProperty(price);
        this.reorderLevel = new SimpleIntegerProperty(reorderLevel);
        this.category = new SimpleStringProperty(category);
    }

    // Getters and property accessors
    public int getId() { return id.get(); }
    public IntegerProperty idProperty() { return id; }

    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }

    public int getQuantity() { return quantity.get(); }
    public void setQuantity(int quantity) { this.quantity.set(quantity); }
    public IntegerProperty quantityProperty() { return quantity; }

    public double getPrice() { return price.get(); }
    public DoubleProperty priceProperty() { return price; }

    public int getReorderLevel() { return reorderLevel.get(); }
    public IntegerProperty reorderLevelProperty() { return reorderLevel; }

    public String getCategory() { return category.get(); }
    public StringProperty categoryProperty() { return category; }
    
    @Override
    public String toString() {
        return name.get();
    }
} 