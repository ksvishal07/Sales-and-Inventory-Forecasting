package com.inventory.controller;

import com.inventory.model.InventoryItem;
import com.inventory.model.SalesData;
import com.inventory.model.ForecastResult;
import com.inventory.service.DatabaseService;
import com.inventory.service.ForecastService;
import com.inventory.service.ReportService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;

public class DashboardController {
    @FXML private TableView<InventoryItem> inventoryTable;
    @FXML private LineChart<Number, Number> forecastChart;
    @FXML private TableView<InventoryItem> alertsTable;
    @FXML private ComboBox<InventoryItem> itemSelector;
    @FXML private ComboBox<String> modelSelector;
    @FXML private Label accuracyLabel;
    @FXML private VBox chartContainer;
    @FXML private TableView<SalesData> currentSalesTable;
    @FXML private TableColumn<SalesData, String> currentSalesDateCol;
    @FXML private TableColumn<SalesData, Integer> currentSalesQuantityCol;
    @FXML private TableColumn<SalesData, Double> currentSalesRevenueCol;
    @FXML private Label productTitleLabel;
    @FXML private TableView<ForecastResult> forecastResultsTable;
    @FXML private TableColumn<ForecastResult, LocalDate> forecastDateCol;
    @FXML private TableColumn<ForecastResult, Double> forecastQuantityCol;
    @FXML private TableColumn<ForecastResult, Double> forecastLowerBoundCol;
    @FXML private TableColumn<ForecastResult, Double> forecastUpperBoundCol;
    @FXML private TableColumn<ForecastResult, String> forecastModelTypeCol;
    @FXML private TableColumn<ForecastResult, Double> forecastSalesPriceCol;
    @FXML private TableColumn<ForecastResult, String> forecastMonthCol;
    
    private DatabaseService dbService;
    private ForecastService forecastService;
    private ReportService reportService;
    private ObservableList<InventoryItem> inventoryItems;
    private ObservableList<InventoryItem> alertItems;
    private XYChart.Series<Number, Number> forecastSeries;
    private XYChart.Series<Number, Number> lowerBoundSeries;
    private XYChart.Series<Number, Number> upperBoundSeries;
    private XYChart.Series<Number, Number> historicalSeries;
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMM dd");

    @FXML
    public void initialize() {
        dbService = new DatabaseService();
        forecastService = new ForecastService(dbService);
        reportService = new ReportService(dbService, forecastService);
        
        // Initialize series
        forecastSeries = new XYChart.Series<>();
        forecastSeries.setName("Forecast");
        historicalSeries = new XYChart.Series<>();
        historicalSeries.setName("Actual Sales");
        
        setupForecastChart();
        setupItemSelector();
        setupModelSelector();
        setupCurrentSalesTable();
        setupForecastResultsTable();
        productTitleLabel.setText("No product selected");
        loadData();
    }

    private void setupForecastChart() {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Day of Month");
        
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Quantity");
        
        forecastChart.setTitle("Sales Forecast");
        forecastChart.setCreateSymbols(true);
        forecastChart.setAnimated(false);

        // Initialize series
        forecastSeries = new XYChart.Series<>();
        forecastSeries.setName("Forecast");
        
        historicalSeries = new XYChart.Series<>();
        historicalSeries.setName("Actual Sales");

        forecastChart.getData().addAll(historicalSeries, forecastSeries);
        
        // Apply styles
        forecastSeries.getNode().setStyle("-fx-stroke: blue; -fx-stroke-width: 2px;");
        historicalSeries.getNode().setStyle("-fx-stroke: green; -fx-stroke-width: 2px;");
        
        forecastChart.setLegendVisible(true);
    }

    private void setupItemSelector() {
        itemSelector.setCellFactory(param -> new ListCell<InventoryItem>() {
            @Override
            protected void updateItem(InventoryItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });

        itemSelector.setOnAction(event -> updateForecast());
    }

    private void setupModelSelector() {
        modelSelector.setItems(FXCollections.observableArrayList(
            "Linear Regression",
            "Random Forest",
            "Support Vector Machine"
        ));
        modelSelector.setValue("Linear Regression");
        modelSelector.setOnAction(event -> {
            String selected = modelSelector.getValue();
            switch (selected) {
                case "Random Forest":
                    forecastService.setModel("forest");
                    break;
                case "Support Vector Machine":
                    forecastService.setModel("svm");
                    break;
                default:
                    forecastService.setModel("linear");
            }
            updateForecast();
        });
    }

    private void setupCurrentSalesTable() {
        currentSalesDateCol.setCellValueFactory(cellData -> {
            LocalDate date = cellData.getValue().getSaleDate();
            return new SimpleStringProperty(dateFormatter.format(date));
        });
        currentSalesQuantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        currentSalesRevenueCol.setCellValueFactory(cellData -> {
            double revenue = cellData.getValue().getRevenue();
            return new SimpleDoubleProperty(Math.round(revenue * 100.0) / 100.0).asObject();
        });
    }

    private void loadData() {
        // Get all inventory items for the item selector
        List<InventoryItem> allItems = dbService.getAllInventoryItems();
        inventoryItems = FXCollections.observableArrayList(allItems);
        itemSelector.setItems(inventoryItems);

        // Update forecast results
        InventoryItem selectedItem = itemSelector.getValue();
        if (selectedItem != null) {
            List<ForecastResult> forecastResults = forecastService.getForecastResults(selectedItem.getId());
            forecastResultsTable.setItems(FXCollections.observableArrayList(forecastResults));
        } else {
            forecastResultsTable.getItems().clear();
        }

        // Update current sales data if an item is selected
        if (!inventoryItems.isEmpty()) {
            itemSelector.setValue(inventoryItems.get(0));
            updateForecast();
        } else {
            // Clear the current sales table if no items are available
            currentSalesTable.getItems().clear();
        }
    }

    private void updateForecast() {
        InventoryItem selectedItem = itemSelector.getValue();
        if (selectedItem == null) {
            forecastSeries.getData().clear();
            historicalSeries.getData().clear();
            currentSalesTable.getItems().clear();
            accuracyLabel.setText("No item selected");
            productTitleLabel.setText("No product selected");
            forecastResultsTable.getItems().clear();
            return;
        }

        // Update the product title
        productTitleLabel.setText("Product: " + selectedItem.getName());

        // Clear existing data
        forecastSeries.getData().clear();
        historicalSeries.getData().clear();

        // Get forecast data
        Map<String, Object> forecastData = forecastService.forecastNextMonth(selectedItem.getId());
        if (forecastData.isEmpty()) {
            accuracyLabel.setText("No data available for forecasting");
            forecastResultsTable.getItems().clear();
            return;
        }

        double accuracy = (double) forecastData.get("accuracy");
        accuracyLabel.setText(String.format("Model Accuracy: %.1f%%", accuracy * 100));

        List<Double> forecast = (List<Double>) forecastData.get("forecast");
        LocalDate nextMonth = LocalDate.now().plusMonths(1);
        for (int i = 0; i < forecast.size(); i++) {
            forecastSeries.getData().add(new XYChart.Data<>(i + 1, forecast.get(i)));
        }

        // Add historical data
        List<SalesData> historicalData = dbService.getSalesHistory(selectedItem.getId());
        for (SalesData sale : historicalData) {
            historicalSeries.getData().add(new XYChart.Data<>(sale.getSaleDate().getDayOfMonth(), sale.getQuantity()));
        }

        // Update the current sales table with the latest sales data
        currentSalesTable.setItems(FXCollections.observableArrayList(historicalData));

        // Update the forecasted results table
        List<ForecastResult> forecastResults = forecastService.getForecastResults(selectedItem.getId());
        forecastResultsTable.setItems(FXCollections.observableArrayList(forecastResults));

        // Set chart legend and axis labels
        forecastSeries.setName("Forecast");
        historicalSeries.setName("Actual Sales");
        forecastChart.getXAxis().setLabel("Day of Month");
        forecastChart.getYAxis().setLabel("Quantity");
        forecastChart.setLegendVisible(true);
    }

    @FXML
    private void handleExportReport() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        fileChooser.setInitialFileName("inventory_report_" + LocalDate.now().toString() + ".xlsx");

        File file = fileChooser.showSaveDialog(chartContainer.getScene().getWindow());
        if (file != null) {
            try {
                reportService.generateReport(file.getAbsolutePath());
                showAlert("Success", "Report generated successfully!", Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Error", "Failed to generate report: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void handleUploadData() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/upload_dialog.fxml"));
            DialogPane dialogPane = loader.load();
            UploadDialogController controller = loader.getController();
            
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Upload Sales Data");
            
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                List<SalesData> uploadedData = controller.getParsedData();
                Map<String, Integer> productIdMap = controller.getProductIdMap();
                
                if (!uploadedData.isEmpty()) {
                    // Clear existing data
                    currentSalesTable.getItems().clear();
                    
                    // Update inventory and sales data for each product
                    for (Map.Entry<String, Integer> entry : productIdMap.entrySet()) {
                        String productName = entry.getKey();
                        int productId = entry.getValue();
                        
                        // Filter data for this product
                        List<SalesData> productData = uploadedData.stream()
                            .filter(data -> data.getItemId() == productId)
                            .collect(Collectors.toList());
                        
                        if (!productData.isEmpty()) {
                            // Get the latest data point for inventory update
                            SalesData latestData = productData.get(productData.size() - 1);
                            
                            // Update inventory quantity
                            dbService.updateInventoryQuantity(productId, latestData.getRemaining());
                            
                            // Update sales history
                            forecastService.updateSalesData(productId, productData);
                        }
                    }
                    
                    // Train the model with the uploaded data
                    forecastService.trainModel(uploadedData);
                    
                    // Refresh the dashboard
                    loadData();
                    // Update the product title based on the selected item
                    InventoryItem selectedItem = itemSelector.getValue();
                    if (selectedItem != null) {
                        productTitleLabel.setText("Product: " + selectedItem.getName());
                    }
                    showAlert("Success", "Data uploaded and model trained successfully!", Alert.AlertType.INFORMATION);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError("Error", "Failed to load upload dialog: " + e.getMessage());
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Add new method for forecast results table
    private void setupForecastResultsTable() {
        forecastQuantityCol.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getForecastedQuantity()).asObject());
        forecastSalesPriceCol.setCellValueFactory(cellData -> {
            double price = cellData.getValue().getAssumedSalesPrice();
            return new SimpleDoubleProperty(Math.round(price * 100.0) / 100.0).asObject();
        });
        forecastMonthCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate().getMonth().toString() + " " + cellData.getValue().getDate().getYear()));
    }
} 