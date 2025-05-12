package com.inventory.service;

import com.inventory.model.InventoryItem;
import com.inventory.model.SalesData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ReportService {
    private final DatabaseService dbService;
    private final ForecastService forecastService;

    public ReportService(DatabaseService dbService, ForecastService forecastService) {
        this.dbService = dbService;
        this.forecastService = forecastService;
    }

    public void generateReport(String filePath) {
        try (Workbook workbook = new XSSFWorkbook()) {
            // Create styles
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle dateStyle = createDateStyle(workbook);
            CellStyle numberStyle = createNumberStyle(workbook);

            // Inventory Sheet
            createInventorySheet(workbook, headerStyle, numberStyle);

            // Sales Forecast Sheet
            createForecastSheet(workbook, headerStyle, dateStyle, numberStyle);

            // Alerts Sheet
            createAlertsSheet(workbook, headerStyle, numberStyle);

            // Write to file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createInventorySheet(Workbook workbook, CellStyle headerStyle, CellStyle numberStyle) {
        Sheet sheet = workbook.createSheet("Inventory");
        List<InventoryItem> items = dbService.getAllInventoryItems();

        // Create header
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Name", "Quantity", "Price", "Reorder Level", "Category"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Add data
        int rowNum = 1;
        for (InventoryItem item : items) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(item.getId());
            row.createCell(1).setCellValue(item.getName());
            
            Cell quantityCell = row.createCell(2);
            quantityCell.setCellValue(item.getQuantity());
            quantityCell.setCellStyle(numberStyle);
            
            Cell priceCell = row.createCell(3);
            priceCell.setCellValue(item.getPrice());
            priceCell.setCellStyle(numberStyle);
            
            Cell reorderCell = row.createCell(4);
            reorderCell.setCellValue(item.getReorderLevel());
            reorderCell.setCellStyle(numberStyle);
            
            row.createCell(5).setCellValue(item.getCategory());
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createForecastSheet(Workbook workbook, CellStyle headerStyle, CellStyle dateStyle, CellStyle numberStyle) {
        Sheet sheet = workbook.createSheet("Sales Forecast");
        List<InventoryItem> items = dbService.getAllInventoryItems();

        // Create header
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Item", "Date", "Forecast", "Lower Bound", "Upper Bound", "Accuracy"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Add data
        int rowNum = 1;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate nextMonth = LocalDate.now().plusMonths(1);

        for (InventoryItem item : items) {
            Map<String, Object> forecastData = forecastService.forecastNextMonth(item.getId());
            if (forecastData.isEmpty()) continue;

            List<Double> forecast = (List<Double>) forecastData.get("forecast");
            List<Double> lowerBound = (List<Double>) forecastData.get("lowerBound");
            List<Double> upperBound = (List<Double>) forecastData.get("upperBound");
            double accuracy = (double) forecastData.get("accuracy");

            for (int i = 0; i < forecast.size(); i++) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(item.getName());
                
                Cell dateCell = row.createCell(1);
                dateCell.setCellValue(nextMonth.withDayOfMonth(i + 1).format(formatter));
                dateCell.setCellStyle(dateStyle);
                
                Cell forecastCell = row.createCell(2);
                forecastCell.setCellValue(forecast.get(i));
                forecastCell.setCellStyle(numberStyle);
                
                Cell lowerCell = row.createCell(3);
                lowerCell.setCellValue(lowerBound.get(i));
                lowerCell.setCellStyle(numberStyle);
                
                Cell upperCell = row.createCell(4);
                upperCell.setCellValue(upperBound.get(i));
                upperCell.setCellStyle(numberStyle);
                
                Cell accuracyCell = row.createCell(5);
                accuracyCell.setCellValue(accuracy);
                accuracyCell.setCellStyle(numberStyle);
            }
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private void createAlertsSheet(Workbook workbook, CellStyle headerStyle, CellStyle numberStyle) {
        Sheet sheet = workbook.createSheet("Inventory Alerts");
        List<InventoryItem> alerts = dbService.getLowStockItems();

        // Create header
        Row headerRow = sheet.createRow(0);
        String[] headers = {"Item", "Current Stock", "Reorder Level", "Deficit"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Add data
        int rowNum = 1;
        for (InventoryItem item : alerts) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(item.getName());
            
            Cell stockCell = row.createCell(1);
            stockCell.setCellValue(item.getQuantity());
            stockCell.setCellStyle(numberStyle);
            
            Cell reorderCell = row.createCell(2);
            reorderCell.setCellValue(item.getReorderLevel());
            reorderCell.setCellStyle(numberStyle);
            
            Cell deficitCell = row.createCell(3);
            deficitCell.setCellValue(item.getReorderLevel() - item.getQuantity());
            deficitCell.setCellStyle(numberStyle);
        }

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }

    private CellStyle createDateStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("yyyy-mm-dd"));
        return style;
    }

    private CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
        return style;
    }
} 