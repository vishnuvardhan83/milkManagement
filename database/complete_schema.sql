-- Dairy Farm ERP - Complete Database Schema
-- MySQL 8.0+

CREATE DATABASE IF NOT EXISTS dairy_farm_erp;
USE dairy_farm_erp;

-- Users table for authentication
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Roles table
CREATE TABLE roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- User-Role mapping (Many-to-Many)
CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    INDEX idx_user_id (user_id),
    INDEX idx_role_id (role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Customers table
CREATE TABLE customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address TEXT,
    mobile_number VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    daily_milk_quantity DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    balance DECIMAL(10, 2) DEFAULT 0.00,
    milk_type ENUM('COW', 'BUFFALO', 'BOTH') DEFAULT 'COW',
    delivery_status ENUM('ACTIVE', 'PAUSED', 'INACTIVE') DEFAULT 'ACTIVE',
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_mobile (mobile_number),
    INDEX idx_status (delivery_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Products table
CREATE TABLE products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    unit VARCHAR(20) DEFAULT 'LITRE',
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_product_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Product prices (to track price history)
CREATE TABLE product_prices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    price_per_unit DECIMAL(10, 2) NOT NULL,
    effective_from DATE NOT NULL,
    effective_to DATE,
    is_active BOOLEAN DEFAULT TRUE,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_product (product_id),
    INDEX idx_effective_date (effective_from, effective_to)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Stock table
CREATE TABLE stock (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    quantity DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    UNIQUE KEY unique_product_stock (product_id),
    INDEX idx_product (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Animals table
CREATE TABLE animals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tag_number VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100),
    animal_type ENUM('COW', 'BUFFALO') NOT NULL,
    breed ENUM('HOLSTEIN', 'JERSEY', 'SAHIWAL', 'MURRAH', 'NILI_RAVI', 'CROSSBREED', 'OTHER'),
    date_of_birth DATE,
    purchase_date DATE,
    purchase_price DECIMAL(10, 2),
    status ENUM('ACTIVE', 'SICK', 'PREGNANT', 'DRY', 'SOLD', 'DECEASED') NOT NULL DEFAULT 'ACTIVE',
    health_status VARCHAR(255),
    notes TEXT,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_tag_number (tag_number),
    INDEX idx_animal_type (animal_type),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Daily milk collections table
CREATE TABLE daily_milk_collections (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    animal_id BIGINT NOT NULL,
    collection_date DATE NOT NULL,
    morning_quantity DECIMAL(10, 2),
    evening_quantity DECIMAL(10, 2),
    total_quantity DECIMAL(10, 2) NOT NULL,
    quality_grade VARCHAR(50),
    notes TEXT,
    collected_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (animal_id) REFERENCES animals(id) ON DELETE CASCADE,
    FOREIGN KEY (collected_by) REFERENCES users(id) ON DELETE SET NULL,
    UNIQUE KEY unique_daily_collection (collection_date, animal_id),
    INDEX idx_animal (animal_id),
    INDEX idx_collection_date (collection_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Inventory items table
CREATE TABLE inventory_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    category ENUM('FODDER', 'MEDICINE', 'PACKAGING', 'CANS', 'SUPPLEMENTS', 'EQUIPMENT', 'OTHER') NOT NULL,
    quantity DECIMAL(10, 2) NOT NULL,
    unit VARCHAR(50) NOT NULL,
    cost_per_unit DECIMAL(10, 2),
    total_cost DECIMAL(10, 2),
    supplier_name VARCHAR(255),
    purchase_date DATE,
    expiry_date DATE,
    notes TEXT,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_category (category),
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Inventory usage table
CREATE TABLE inventory_usage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    inventory_item_id BIGINT NOT NULL,
    usage_date DATE NOT NULL,
    quantity_used DECIMAL(10, 2) NOT NULL,
    purpose TEXT,
    used_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (inventory_item_id) REFERENCES inventory_items(id) ON DELETE CASCADE,
    FOREIGN KEY (used_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_item (inventory_item_id),
    INDEX idx_usage_date (usage_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Receipts table
CREATE TABLE receipts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    receipt_number VARCHAR(100) UNIQUE NOT NULL,
    customer_id BIGINT NOT NULL,
    receipt_date DATE NOT NULL,
    quantity_liters DECIMAL(10, 2) NOT NULL,
    milk_rate DECIMAL(10, 2) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    payment_status ENUM('PENDING', 'PARTIAL', 'PAID') NOT NULL DEFAULT 'PENDING',
    paid_amount DECIMAL(10, 2) DEFAULT 0.00,
    pending_amount DECIMAL(10, 2) DEFAULT 0.00,
    payment_date DATE,
    payment_method VARCHAR(50),
    notes TEXT,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_customer (customer_id),
    INDEX idx_receipt_date (receipt_date),
    INDEX idx_payment_status (payment_status),
    INDEX idx_receipt_number (receipt_number)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Expenses table
CREATE TABLE expenses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    expense_date DATE NOT NULL,
    category ENUM('FEED', 'MEDICINE', 'LABOR', 'MAINTENANCE', 'UTILITIES', 'TRANSPORT', 'INSURANCE', 'OTHER') NOT NULL,
    description VARCHAR(255) NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method VARCHAR(50),
    receipt_number VARCHAR(100),
    notes TEXT,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_expense_date (expense_date),
    INDEX idx_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Salaries table
CREATE TABLE salaries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    salary_month DATE NOT NULL,
    base_salary DECIMAL(10, 2) NOT NULL,
    bonus DECIMAL(10, 2) DEFAULT 0.00,
    deductions DECIMAL(10, 2) DEFAULT 0.00,
    net_salary DECIMAL(10, 2) NOT NULL,
    payment_status ENUM('PENDING', 'PAID', 'PARTIAL') NOT NULL DEFAULT 'PENDING',
    payment_date DATE,
    notes TEXT,
    paid_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (paid_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_employee (employee_id),
    INDEX idx_salary_month (salary_month),
    INDEX idx_payment_status (payment_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Notification queue table
CREATE TABLE notification_queue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT,
    notification_type ENUM('PAYMENT_REMINDER', 'RECEIPT_GENERATED', 'BALANCE_UPDATE', 'CUSTOM_MESSAGE') NOT NULL,
    channel ENUM('EMAIL', 'SMS', 'BOTH') NOT NULL,
    recipient VARCHAR(255) NOT NULL,
    subject VARCHAR(255),
    message TEXT NOT NULL,
    status ENUM('PENDING', 'SENT', 'FAILED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    sent_at TIMESTAMP NULL,
    error_message TEXT,
    retry_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    INDEX idx_status (status),
    INDEX idx_customer (customer_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Milk deliveries table (existing)
CREATE TABLE milk_deliveries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    delivery_date DATE NOT NULL,
    quantity_delivered DECIMAL(10, 2) NOT NULL,
    price_per_unit DECIMAL(10, 2) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    delivered_by BIGINT,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    FOREIGN KEY (delivered_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_customer (customer_id),
    INDEX idx_delivery_date (delivery_date),
    INDEX idx_product (product_id),
    UNIQUE KEY unique_daily_delivery (customer_id, product_id, delivery_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Payments table
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_date DATE NOT NULL,
    payment_method ENUM('CASH', 'ONLINE', 'CHEQUE', 'OTHER') DEFAULT 'CASH',
    reference_number VARCHAR(100),
    notes TEXT,
    received_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    FOREIGN KEY (received_by) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_customer (customer_id),
    INDEX idx_payment_date (payment_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert default roles
INSERT INTO roles (name, description) VALUES
('ROLE_ADMIN', 'Administrator with full access'),
('ROLE_WORKER', 'Worker with data entry access'),
('ROLE_CUSTOMER', 'Customer with limited access'),
('ROLE_MANAGER', 'Manager with operational access');

-- Insert default admin user (password: admin123 - hashed with BCrypt)
INSERT INTO users (username, email, password, enabled) VALUES
('admin', 'admin@dairyfarm.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pK5qO', TRUE),
('worker', 'worker@dairyfarm.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pK5qO', TRUE);

-- Assign roles to default users
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), -- admin has ROLE_ADMIN
(2, 2); -- worker has ROLE_WORKER

-- Insert default products
INSERT INTO products (name, unit, description) VALUES
('Milk', 'LITRE', 'Fresh cow/buffalo milk'),
('Curd', 'KG', 'Fresh curd'),
('Butter', 'KG', 'Fresh butter');

-- Insert default product prices
INSERT INTO product_prices (product_id, price_per_unit, effective_from, is_active, created_by) VALUES
(1, 50.00, CURDATE(), TRUE, 1),
(2, 80.00, CURDATE(), TRUE, 1),
(3, 500.00, CURDATE(), TRUE, 1);

-- Initialize stock
INSERT INTO stock (product_id, quantity) VALUES
(1, 0.00),
(2, 0.00),
(3, 0.00);

