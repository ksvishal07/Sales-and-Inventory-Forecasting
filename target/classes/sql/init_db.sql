-- Initialize the database schema
CREATE DATABASE IF NOT EXISTS inventory_db;
USE inventory_db;

-- Create inventory items table
CREATE TABLE IF NOT EXISTS inventory_items (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    reorder_level INT NOT NULL,
    category VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create sales history table
CREATE TABLE IF NOT EXISTS sales_history (
    id INT PRIMARY KEY AUTO_INCREMENT,
    item_id INT NOT NULL,
    quantity INT NOT NULL,
    revenue DECIMAL(10,2) NOT NULL,
    sale_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (item_id) REFERENCES inventory_items(id)
);

-- Insert sample inventory data
INSERT INTO inventory_items (name, quantity, price, reorder_level, category) VALUES
('Laptop', 15, 999.99, 5, 'Electronics'),
('Mouse', 50, 29.99, 10, 'Electronics'),
('Keyboard', 30, 49.99, 8, 'Electronics'),
('Monitor', 20, 199.99, 5, 'Electronics'),
('Headphones', 25, 79.99, 7, 'Electronics'),
('Desk Chair', 8, 149.99, 3, 'Furniture'),
('Office Desk', 5, 249.99, 2, 'Furniture'),
('Smartphone', 12, 699.99, 4, 'Electronics'),
('Tablet', 10, 399.99, 3, 'Electronics'),
('Printer', 6, 299.99, 2, 'Electronics');

-- Generate sample sales data (90 days of history)
-- This would typically be a stored procedure or generated programmatically
-- Here's a sample for item 1 (Laptop):

INSERT INTO sales_history (item_id, quantity, revenue, sale_date) VALUES
-- Laptop sales (recent 3 months, with weekly pattern)
(1, 2, 1999.98, DATE_SUB(CURDATE(), INTERVAL 90 DAY)),
(1, 3, 2999.97, DATE_SUB(CURDATE(), INTERVAL 83 DAY)),
(1, 1, 999.99, DATE_SUB(CURDATE(), INTERVAL 76 DAY)),
(1, 2, 1999.98, DATE_SUB(CURDATE(), INTERVAL 69 DAY)),
(1, 3, 2999.97, DATE_SUB(CURDATE(), INTERVAL 62 DAY)),
(1, 4, 3999.96, DATE_SUB(CURDATE(), INTERVAL 55 DAY)),
(1, 2, 1999.98, DATE_SUB(CURDATE(), INTERVAL 48 DAY)),
(1, 3, 2999.97, DATE_SUB(CURDATE(), INTERVAL 41 DAY)),
(1, 5, 4999.95, DATE_SUB(CURDATE(), INTERVAL 34 DAY)),
(1, 2, 1999.98, DATE_SUB(CURDATE(), INTERVAL 27 DAY)),
(1, 3, 2999.97, DATE_SUB(CURDATE(), INTERVAL 20 DAY)),
(1, 4, 3999.96, DATE_SUB(CURDATE(), INTERVAL 13 DAY)),
(1, 3, 2999.97, DATE_SUB(CURDATE(), INTERVAL 6 DAY)),

-- Mouse sales (id=2) with greater volume
(2, 5, 149.95, DATE_SUB(CURDATE(), INTERVAL 90 DAY)),
(2, 8, 239.92, DATE_SUB(CURDATE(), INTERVAL 83 DAY)),
(2, 6, 179.94, DATE_SUB(CURDATE(), INTERVAL 76 DAY)),
(2, 9, 269.91, DATE_SUB(CURDATE(), INTERVAL 69 DAY)),
(2, 7, 209.93, DATE_SUB(CURDATE(), INTERVAL 62 DAY)),
(2, 10, 299.90, DATE_SUB(CURDATE(), INTERVAL 55 DAY)),
(2, 6, 179.94, DATE_SUB(CURDATE(), INTERVAL 48 DAY)),
(2, 8, 239.92, DATE_SUB(CURDATE(), INTERVAL 41 DAY)),
(2, 7, 209.93, DATE_SUB(CURDATE(), INTERVAL 34 DAY)),
(2, 9, 269.91, DATE_SUB(CURDATE(), INTERVAL 27 DAY)),
(2, 11, 329.89, DATE_SUB(CURDATE(), INTERVAL 20 DAY)),
(2, 8, 239.92, DATE_SUB(CURDATE(), INTERVAL 13 DAY)),
(2, 10, 299.90, DATE_SUB(CURDATE(), INTERVAL 6 DAY)),

-- Keyboard sales (id=3)
(3, 3, 149.97, DATE_SUB(CURDATE(), INTERVAL 88 DAY)),
(3, 4, 199.96, DATE_SUB(CURDATE(), INTERVAL 74 DAY)),
(3, 2, 99.98, DATE_SUB(CURDATE(), INTERVAL 60 DAY)),
(3, 5, 249.95, DATE_SUB(CURDATE(), INTERVAL 46 DAY)),
(3, 3, 149.97, DATE_SUB(CURDATE(), INTERVAL 32 DAY)),
(3, 4, 199.96, DATE_SUB(CURDATE(), INTERVAL 18 DAY)),
(3, 6, 299.94, DATE_SUB(CURDATE(), INTERVAL 4 DAY)); 