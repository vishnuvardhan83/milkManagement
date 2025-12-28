CREATE TABLE IF NOT EXISTS farms (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(50) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    address TEXT,
    phone VARCHAR(20),
    email VARCHAR(100),
    gst_number VARCHAR(50),
    logo_url VARCHAR(500),
    subscription_enabled BOOLEAN DEFAULT TRUE,
    delivery_enabled BOOLEAN DEFAULT TRUE,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    description VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    full_name VARCHAR(100),
    phone VARCHAR(20),
    address TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_tenant_username (tenant_id, username),
    UNIQUE KEY unique_tenant_email (tenant_id, email)
);

CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS animals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    tag_number VARCHAR(50) NOT NULL,
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
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL,
    UNIQUE KEY unique_tenant_tag (tenant_id, tag_number)
);

CREATE TABLE IF NOT EXISTS milk_entries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
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

CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    address TEXT,
    mobile_number VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    daily_milk_quantity DECIMAL(10, 2) DEFAULT 0,
    balance DECIMAL(10, 2) DEFAULT 0,
    wallet_balance DECIMAL(10, 2) DEFAULT 0,
    loyalty_points INT DEFAULT 0,
    milk_type ENUM('COW', 'BUFFALO', 'BOTH') DEFAULT 'COW',
    delivery_status ENUM('ACTIVE', 'PAUSED', 'INACTIVE') DEFAULT 'ACTIVE',
    referral_code VARCHAR(50) UNIQUE,
    referred_by BIGINT,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS product_catalog (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    product_type ENUM('MILK', 'CURD', 'GHEE', 'PANEER', 'BUTTER', 'CHEESE', 'BUTTERMILK', 'OTHER') NOT NULL,
    description TEXT,
    unit VARCHAR(50) NOT NULL,
    price_per_unit DECIMAL(10, 2) NOT NULL,
    subscription_price DECIMAL(10, 2),
    image_url VARCHAR(500),
    available_for_subscription BOOLEAN DEFAULT TRUE,
    available_for_daily_sale BOOLEAN DEFAULT TRUE,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS subscription_plans (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    plan_type ENUM('BASIC_MILK_PASS', 'FAMILY_PACK', 'CUSTOM_PACKAGE', 'PREMIUM') NOT NULL,
    monthly_price DECIMAL(10, 2) NOT NULL,
    duration_days INT NOT NULL DEFAULT 30,
    products_json TEXT,
    discount_percentage DECIMAL(5, 2),
    max_products INT,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS subscriptions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    subscription_number VARCHAR(100) UNIQUE NOT NULL,
    customer_id BIGINT NOT NULL,
    subscription_plan_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    next_billing_date DATE,
    monthly_amount DECIMAL(10, 2) NOT NULL,
    status ENUM('ACTIVE', 'PAUSED', 'CANCELLED', 'EXPIRED', 'PENDING_PAYMENT') NOT NULL,
    auto_renew BOOLEAN DEFAULT TRUE,
    paused BOOLEAN DEFAULT FALSE,
    pause_start_date DATE,
    pause_end_date DATE,
    wallet_balance DECIMAL(10, 2) DEFAULT 0,
    loyalty_points INT DEFAULT 0,
    coupon_code VARCHAR(50),
    discount_amount DECIMAL(10, 2) DEFAULT 0,
    products_json TEXT,
    created_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    FOREIGN KEY (subscription_plan_id) REFERENCES subscription_plans(id) ON DELETE CASCADE,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS daily_delivery_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    subscription_id BIGINT NOT NULL,
    customer_id BIGINT NOT NULL,
    delivery_boy_id BIGINT,
    delivery_date DATE NOT NULL,
    scheduled_time VARCHAR(10),
    actual_delivery_time TIMESTAMP,
    products_delivered_json TEXT,
    quantity_delivered DECIMAL(10, 2),
    delivery_status ENUM('PENDING', 'ASSIGNED', 'IN_TRANSIT', 'DELIVERED', 'FAILED', 'CANCELLED') NOT NULL,
    qr_code VARCHAR(500),
    qr_scanned BOOLEAN DEFAULT FALSE,
    qr_scan_time TIMESTAMP,
    customer_signature VARCHAR(500),
    delivery_notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (subscription_id) REFERENCES subscriptions(id) ON DELETE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    FOREIGN KEY (delivery_boy_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS receipts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
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

CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
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

CREATE TABLE IF NOT EXISTS inventory_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
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

CREATE TABLE IF NOT EXISTS inventory_usage (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    inventory_item_id BIGINT NOT NULL,
    usage_date DATE NOT NULL,
    quantity_used DECIMAL(10, 2) NOT NULL,
    purpose TEXT,
    used_by BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (inventory_item_id) REFERENCES inventory_items(id) ON DELETE CASCADE,
    FOREIGN KEY (used_by) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS expenses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
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

CREATE TABLE IF NOT EXISTS salaries (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
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

CREATE TABLE IF NOT EXISTS notification_queue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
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

CREATE TABLE IF NOT EXISTS system_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    setting_key VARCHAR(100) NOT NULL,
    setting_value TEXT,
    setting_type VARCHAR(50),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY unique_tenant_setting (tenant_id, setting_key)
);

CREATE TABLE IF NOT EXISTS activity_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    user_id BIGINT,
    module VARCHAR(100) NOT NULL,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100),
    entity_id BIGINT,
    details TEXT,
    ip_address VARCHAR(45),
    user_agent VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE INDEX idx_milk_entry_date ON milk_entries(entry_date);
CREATE INDEX idx_receipt_date ON receipts(receipt_date);
CREATE INDEX idx_receipt_customer ON receipts(customer_id);
CREATE INDEX idx_payment_date ON payments(payment_date);
CREATE INDEX idx_expense_date ON expenses(expense_date);
CREATE INDEX idx_subscription_customer ON subscriptions(customer_id);
CREATE INDEX idx_subscription_status ON subscriptions(status);
CREATE INDEX idx_delivery_date ON daily_delivery_logs(delivery_date);
CREATE INDEX idx_delivery_boy ON daily_delivery_logs(delivery_boy_id);
CREATE INDEX idx_activity_user ON activity_logs(user_id);
CREATE INDEX idx_activity_module ON activity_logs(module);

