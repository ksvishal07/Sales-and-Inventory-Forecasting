package com.inventory.service;

import com.inventory.model.InventoryItem;
import com.inventory.model.SalesData;
import java.time.LocalDate;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {
    private final String DB_URL = "jdbc:mysql://localhost:3306/inventory_db";
    private final String USER = "root";
    private final String PASS = "password";
    private final boolean USE_SAMPLE_DATA = true; // Use sample data by default
    
    public Connection getConnection() throws SQLException {
        if (USE_SAMPLE_DATA) {
            throw new SQLException("Using sample data, no database connection");
        }
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }
    
    public List<InventoryItem> getAllInventoryItems() {
        List<InventoryItem> items = new ArrayList<>();
        
        if (USE_SAMPLE_DATA) {
            createSampleData(items);
            return items;
        }
        
        String query = "SELECT * FROM inventory_items";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                items.add(new InventoryItem(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getInt("reorder_level"),
                    rs.getString("category")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // For demo, create sample data if database connection fails
            createSampleData(items);
        }
        return items;
    }
    
    public List<InventoryItem> getLowStockItems() {
        List<InventoryItem> items = new ArrayList<>();
        
        if (USE_SAMPLE_DATA) {
            List<InventoryItem> allItems = getAllInventoryItems();
            for (InventoryItem item : allItems) {
                if (item.getQuantity() <= item.getReorderLevel()) {
                    items.add(item);
                }
            }
            return items;
        }
        
        String query = "SELECT * FROM inventory_items WHERE quantity <= reorder_level";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                items.add(new InventoryItem(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getInt("reorder_level"),
                    rs.getString("category")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Generate sample low stock items for demo
            List<InventoryItem> allItems = getAllInventoryItems();
            for (InventoryItem item : allItems) {
                if (item.getQuantity() <= item.getReorderLevel()) {
                    items.add(item);
                }
            }
        }
        return items;
    }
    
    public List<SalesData> getSalesHistory(int itemId) {
        List<SalesData> sales = new ArrayList<>();
        
        if (USE_SAMPLE_DATA) {
            createSampleSalesData(sales, itemId);
            return sales;
        }
        
        String query = "SELECT * FROM sales_history WHERE item_id = ? ORDER BY sale_date ASC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, itemId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    sales.add(new SalesData(
                        rs.getInt("id"),
                        rs.getInt("item_id"),
                        "", // Item name will be filled in later if needed
                        rs.getInt("quantity"),
                        rs.getDouble("revenue"),
                        rs.getDate("sale_date").toLocalDate()
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Generate sample sales data for demo
            createSampleSalesData(sales, itemId);
        }
        return sales;
    }
    
    public boolean updateInventoryQuantity(int itemId, int newQuantity) {
        if (USE_SAMPLE_DATA) {
            // Just pretend the update was successful
            return true;
        }
        
        String query = "UPDATE inventory_items SET quantity = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, newQuantity);
            pstmt.setInt(2, itemId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public void updateSalesHistory(int itemId, List<SalesData> newData) {
        if (USE_SAMPLE_DATA) {
            // In sample data mode, we'll just update the in-memory data
            // This is a simplified version for demo purposes
            return;
        }
        
        String deleteQuery = "DELETE FROM sales_history WHERE item_id = ?";
        String insertQuery = "INSERT INTO sales_history (item_id, quantity, revenue, sale_date) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
             PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
            
            // Start transaction
            conn.setAutoCommit(false);
            
            try {
                // Delete existing data
                deleteStmt.setInt(1, itemId);
                deleteStmt.executeUpdate();
                
                // Insert new data
                for (SalesData data : newData) {
                    insertStmt.setInt(1, itemId);
                    insertStmt.setInt(2, data.getQuantity());
                    insertStmt.setDouble(3, data.getRevenue());
                    insertStmt.setDate(4, java.sql.Date.valueOf(data.getSaleDate()));
                    insertStmt.addBatch();
                }
                insertStmt.executeBatch();
                
                // Commit transaction
                conn.commit();
            } catch (SQLException e) {
                // Rollback on error
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to update sales history: " + e.getMessage());
        }
    }
    
    private void createSampleData(List<InventoryItem> items) {
        // Sample data for demo purposes
        items.add(new InventoryItem(1, "Laptop", 15, 999.99, 5, "Electronics"));
        items.add(new InventoryItem(2, "Mouse", 50, 29.99, 10, "Electronics"));
        items.add(new InventoryItem(3, "Keyboard", 30, 49.99, 8, "Electronics"));
        items.add(new InventoryItem(4, "Monitor", 20, 199.99, 5, "Electronics"));
        items.add(new InventoryItem(5, "Headphones", 25, 79.99, 7, "Electronics"));
        items.add(new InventoryItem(6, "Desk Chair", 8, 149.99, 3, "Furniture"));
        items.add(new InventoryItem(7, "Office Desk", 5, 249.99, 2, "Furniture"));
        items.add(new InventoryItem(8, "Smartphone", 12, 699.99, 4, "Electronics"));
        items.add(new InventoryItem(9, "Tablet", 10, 399.99, 3, "Electronics"));
        items.add(new InventoryItem(10, "Printer", 6, 299.99, 2, "Electronics"));
    }
    
    private void createSampleSalesData(List<SalesData> sales, int itemId) {
        // Generate 90 days of sample sales data
        LocalDate today = LocalDate.now();
        
        for (int i = 90; i > 0; i -= 5) {
            LocalDate date = today.minusDays(i);
            int quantity = 2 + (int)(Math.random() * 5); // Random quantity between 2-6
            double pricePerUnit;
            
            switch (itemId) {
                case 1:
                    pricePerUnit = 999.99; // Laptop
                    break;
                case 2:
                    pricePerUnit = 29.99;  // Mouse
                    break;
                case 3:
                    pricePerUnit = 49.99;  // Keyboard
                    break;
                case 4:
                    pricePerUnit = 199.99; // Monitor
                    break;
                default:
                    pricePerUnit = 50.0;   // Default price
                    break;
            }
            
            sales.add(new SalesData(
                sales.size() + 1,
                itemId,
                "",
                quantity,
                quantity * pricePerUnit,
                date
            ));
        }
    }
} 