-- Dairy Farm ERP Database Schema
-- MySQL 8.0+

CREATE DATABASE IF NOT EXISTS dairy_farm_erp;
USE dairy_farm_erp;

-- Roles Table
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- User Roles Junction Table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- Animals Table
CREATE TABLE IF NOT EXISTS animals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tag_number VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(100),
    animal_type ENUM('COW', 'BUFFALO') NOT NULL,
    breed ENUM('HOLSTEIN', 'JERSEY', 'SAHIWAL', 'MURRAH', 'NILI_RAVI', 'CROSSBREED', 'OTHER'),
    date_of_birth DATE,
    purchase_date DATE,
    purchase_price DECIMAL(10, 2),
    status ENUM('ACTIVE', 'SICK', 'PREGNANT', 'DRY', 'SOLD', 'DECEASED') DEFAULT 'ACTIVE',
    health_status VARCHAR(255),
    notes TEXT,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

-- Milk Entries Table
CREATE TABLE IF NOT EXISTS milk_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    animal_id BIGINT NOT NULL,
    entry_date DATE NOT NULL,
    morning_quantity DECIMAL(10, 2),
    evening_quantity DECIMAL(10, 2),
    total_quantity DECIMAL(10, 2) NOT NULL,
    quality_grade VARCHAR(10),
    temperature DECIMAL(5, 2),
    notes TEXT,
    recorded_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (animal_id) REFERENCES animals(id) ON DELETE CASCADE,
    FOREIGN KEY (recorded_by) REFERENCES users(id) ON DELETE SET NULL,
    UNIQUE KEY unique_animal_date (entry_date, animal_id)
);

-- Customers Table
CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    address TEXT,
    mobile_number VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    daily_milk_quantity DECIMAL(10, 2) DEFAULT 0,
    balance DECIMAL(10, 2) DEFAULT 0,
    milk_type ENUM('COW', 'BUFFALO', 'BOTH') DEFAULT 'COW',
    delivery_status ENUM('ACTIVE', 'PAUSED', 'INACTIVE') DEFAULT 'ACTIVE',
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

-- Receipts Table
CREATE TABLE IF NOT EXISTS receipts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    receipt_number VARCHAR(100) UNIQUE NOT NULL,
    customer_id BIGINT NOT NULL,
    receipt_date DATE NOT NULL,
    quantity_liters DECIMAL(10, 2) NOT NULL,
    milk_rate DECIMAL(10, 2) NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    payment_status ENUM('PENDING', 'PARTIAL', 'PAID') DEFAULT 'PENDING',
    paid_amount DECIMAL(10, 2) DEFAULT 0,
    pending_amount DECIMAL(10, 2) DEFAULT 0,
    payment_date DATE,
    payment_method VARCHAR(50),
    notes TEXT,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

-- Payments Table
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    receipt_id BIGINT,
    payment_date DATE NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    payment_method ENUM('CASH', 'ONLINE', 'CHEQUE', 'UPI', 'BANK_TRANSFER', 'OTHER') NOT NULL,
    reference_number VARCHAR(100),
    status ENUM('PENDING', 'COMPLETED', 'FAILED', 'CANCELLED') DEFAULT 'COMPLETED',
    notes TEXT,
    received_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    FOREIGN KEY (receipt_id) REFERENCES receipts(id) ON DELETE SET NULL,
    FOREIGN KEY (received_by) REFERENCES users(id) ON DELETE SET NULL
);

-- Inventory Items Table
CREATE TABLE IF NOT EXISTS inventory_items (
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
    low_stock_threshold DECIMAL(10, 2),
    notes TEXT,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

-- Inventory Usage Table
CREATE TABLE IF NOT EXISTS inventory_usage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    inventory_item_id BIGINT NOT NULL,
    usage_date DATE NOT NULL,
    quantity_used DECIMAL(10, 2) NOT NULL,
    purpose TEXT,
    used_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (inventory_item_id) REFERENCES inventory_items(id) ON DELETE CASCADE,
    FOREIGN KEY (used_by) REFERENCES users(id) ON DELETE SET NULL
);

-- Expenses Table
CREATE TABLE IF NOT EXISTS expenses (
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
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

-- Salaries Table
CREATE TABLE IF NOT EXISTS salaries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    employee_id BIGINT NOT NULL,
    salary_month DATE NOT NULL,
    base_salary DECIMAL(10, 2) NOT NULL,
    bonus DECIMAL(10, 2) DEFAULT 0,
    deductions DECIMAL(10, 2) DEFAULT 0,
    net_salary DECIMAL(10, 2) NOT NULL,
    payment_status ENUM('PENDING', 'PAID', 'PARTIAL') DEFAULT 'PENDING',
    payment_date DATE,
    notes TEXT,
    paid_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (employee_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (paid_by) REFERENCES users(id) ON DELETE SET NULL,
    UNIQUE KEY unique_employee_month (employee_id, salary_month)
);

-- Notification Queue Table
CREATE TABLE IF NOT EXISTS notification_queue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    customer_id BIGINT,
    notification_type ENUM('PAYMENT_REMINDER', 'RECEIPT_GENERATED', 'BALANCE_UPDATE', 'CUSTOM_MESSAGE') NOT NULL,
    channel ENUM('EMAIL', 'SMS', 'BOTH') NOT NULL,
    recipient VARCHAR(255) NOT NULL,
    subject VARCHAR(255),
    message TEXT NOT NULL,
    status ENUM('PENDING', 'SENT', 'FAILED', 'CANCELLED') DEFAULT 'PENDING',
    sent_at TIMESTAMP NULL,
    error_message TEXT,
    retry_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE
);

-- Audit Logs Table
CREATE TABLE IF NOT EXISTS audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    module VARCHAR(100) NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100),
    entity_id BIGINT,
    details TEXT,
    ip_address VARCHAR(45),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

-- Indexes for performance
CREATE INDEX idx_milk_entry_date ON milk_entries(entry_date);
CREATE INDEX idx_receipt_date ON receipts(receipt_date);
CREATE INDEX idx_receipt_customer ON receipts(customer_id);
CREATE INDEX idx_payment_date ON payments(payment_date);
CREATE INDEX idx_expense_date ON expenses(expense_date);
CREATE INDEX idx_audit_user ON audit_logs(user_id);
CREATE INDEX idx_audit_module ON audit_logs(module);
