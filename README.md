# Inventory Forecast Dashboard

A JavaFX-based dashboard application for inventory management and sales forecasting. The application uses machine learning models to predict future sales and provides insights for inventory management.

## Features

### 1. Sales Data Visualization
- Historical sales data display with date, quantity, and revenue
- Revenue values rounded to 2 decimal places for precision
- Interactive line chart showing sales trends

### 2. Forecasting System
- Multiple forecasting models:
  - Linear Regression (default)
  - Random Forest
  - Support Vector Machine (SVM)
- Seasonal adjustments based on:
  - Monthly patterns
  - Holiday effects
  - Trend analysis
- Confidence intervals for predictions

### 3. Forecast Results
- Monthly predictions for:
  - Stocks to buy (rounded quantities)
  - Assumed sales price (2 decimal places)
  - Month and year
- Model accuracy display
- Exportable reports

### 4. Data Management
- CSV data import functionality
- Real-time data updates
- Historical data tracking
- Export reports to Excel

## Technical Implementation

### Forecasting Algorithm
The system uses a sophisticated forecasting approach that considers multiple factors:

1. **Seasonal Factors**
   - January: 0.9 (Post-holiday slowdown)
   - February: 0.85 (Valentine's Day boost)
   - March: 0.95 (Spring start)
   - April: 1.0 (Normal)
   - May: 1.1 (Graduation season)
   - June: 1.15 (Summer start)
   - July: 1.2 (Summer peak)
   - August: 1.1 (Back to school)
   - September: 1.0 (Normal)
   - October: 1.05 (Halloween)
   - November: 1.2 (Thanksgiving)
   - December: 1.5 (Holiday season)

2. **Holiday Adjustments**
   - 30% boost on major holidays
   - Includes: New Year's Day, Valentine's Day, St. Patrick's Day, etc.

3. **Trend Analysis**
   - Month-over-month growth rate calculation
   - Historical data analysis
   - Projection of future trends

4. **Random Variation**
   - ±10% random variation to prevent uniform predictions
   - Makes forecasts more realistic

### UI Components

1. **Main Dashboard**
   - Product selector dropdown
   - Model selector (Linear, Forest, SVM)
   - Accuracy display
   - Interactive forecast chart

2. **Sales Table**
   - Date (formatted as MMM dd)
   - Quantity
   - Revenue (2 decimal places)

3. **Forecast Results Table**
   - Stocks to Buy
   - Assumed Sales Price (2 decimal places)
   - Month and Year

## Setup and Installation

### Prerequisites
- Java 11 or higher
- Maven
- MySQL Database

### Database Setup
1. Create a MySQL database
2. Configure database connection in `application.properties`
3. Run database initialization scripts

### Building the Application
```bash
mvn clean install
```

### Running the Application
```bash
mvn javafx:run
```

## Data Import Format
The application accepts CSV files with the following columns:
- Item ID
- Item Name
- Sale Date
- Quantity
- Revenue

## Export Format
Reports are exported in Excel format (.xlsx) containing:
- Sales history
- Forecast results
- Model accuracy metrics
- Seasonal adjustments

## Contributing
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License
This project is licensed under the MIT License - see the LICENSE file for details. 