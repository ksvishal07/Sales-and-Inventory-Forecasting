package com.inventory.controller;

import com.inventory.model.SalesData;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UploadDialogController {
    @FXML private TextField filePathField;
    @FXML private TableView<SalesData> previewTable;
    @FXML private Label statusLabel;
    
    private ObservableList<SalesData> previewData = FXCollections.observableArrayList();
    private List<SalesData> parsedData = new ArrayList<>();
    private Map<String, Integer> productIdMap = new HashMap<>();
    
    @FXML
    public void initialize() {
        setupPreviewTable();
    }
    
    private void setupPreviewTable() {
        TableColumn<SalesData, String> productCol = new TableColumn<>("Product");
        productCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        
        TableColumn<SalesData, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("saleDate"));
        
        TableColumn<SalesData, Integer> quantityCol = new TableColumn<>("Quantity");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        
        TableColumn<SalesData, Double> revenueCol = new TableColumn<>("Revenue");
        revenueCol.setCellValueFactory(new PropertyValueFactory<>("revenue"));
        revenueCol.setCellFactory(column -> new TableCell<SalesData, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", item));
                }
            }
        });
        
        TableColumn<SalesData, Integer> soldCol = new TableColumn<>("Sold");
        soldCol.setCellValueFactory(new PropertyValueFactory<>("sold"));
        
        TableColumn<SalesData, Integer> remainingCol = new TableColumn<>("Remaining");
        remainingCol.setCellValueFactory(new PropertyValueFactory<>("remaining"));
        
        previewTable.getColumns().addAll(productCol, dateCol, quantityCol, revenueCol, soldCol, remainingCol);
        previewTable.setItems(previewData);
    }
    
    @FXML
    private void handleBrowse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CSV File");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("CSV Files", "*.csv")
        );
        
        File file = fileChooser.showOpenDialog(filePathField.getScene().getWindow());
        if (file != null) {
            filePathField.setText(file.getAbsolutePath());
            parseCSV(file);
        }
    }
    
    private void parseCSV(File file) {
        previewData.clear();
        parsedData.clear();
        productIdMap.clear();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            AtomicInteger currentProductId = new AtomicInteger(1);
            
            while ((line = reader.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue; // Skip header
                }
                
                String[] values = line.split(",");
                if (values.length >= 6) {
                    try {
                        String productName = values[0].trim();
                        LocalDate date = LocalDate.parse(values[1].trim(), dateFormatter);
                        int quantity = Integer.parseInt(values[2].trim());
                        double revenue = Double.parseDouble(values[3].trim());
                        int sold = Integer.parseInt(values[4].trim());
                        int remaining = Integer.parseInt(values[5].trim());
                        
                        // Get or create product ID
                        int productId = productIdMap.computeIfAbsent(productName, k -> currentProductId.getAndIncrement());
                        
                        SalesData data = new SalesData(0, productId, productName, quantity, revenue, date);
                        data.setSold(sold);
                        data.setRemaining(remaining);
                        
                        parsedData.add(data);
                        
                        if (previewData.size() < 10) { // Show only first 10 rows in preview
                            previewData.add(data);
                        }
                    } catch (Exception e) {
                        statusLabel.setText("Error parsing line: " + line);
                        return;
                    }
                }
            }
            
            statusLabel.setText("Successfully loaded " + parsedData.size() + " records for " + productIdMap.size() + " products");
        } catch (IOException e) {
            statusLabel.setText("Error reading file: " + e.getMessage());
        }
    }
    
    public List<SalesData> getParsedData() {
        return parsedData;
    }
    
    public Map<String, Integer> getProductIdMap() {
        return productIdMap;
    }
} 