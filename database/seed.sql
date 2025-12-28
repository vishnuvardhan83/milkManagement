-- Seed Data for Dairy Farm ERP
USE dairy_farm_erp;

-- Insert Roles
INSERT INTO roles (name, description) VALUES
('ROLE_ADMIN', 'Administrator with full access'),
('ROLE_MANAGER', 'Manager with management access'),
('ROLE_WORKER', 'Worker with limited access'),
('ROLE_CUSTOMER', 'Customer access')
ON DUPLICATE KEY UPDATE name=name;

-- Insert Default Admin User (password: admin123)
-- Password hash generated using BCrypt with cost 10
INSERT INTO users (username, email, password, full_name, phone, enabled) VALUES
('admin', 'admin@dairyfarm.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJZ2a', 'Admin User', '1234567890', TRUE),
('manager', 'manager@dairyfarm.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJZ2a', 'Manager User', '1234567891', TRUE),
('worker1', 'worker1@dairyfarm.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJZ2a', 'Worker One', '1234567892', TRUE)
ON DUPLICATE KEY UPDATE username=username;

-- Assign Roles to Users
INSERT INTO user_roles (user_id, role_id) VALUES
((SELECT id FROM users WHERE username='admin'), (SELECT id FROM roles WHERE name='ROLE_ADMIN')),
((SELECT id FROM users WHERE username='manager'), (SELECT id FROM roles WHERE name='ROLE_MANAGER')),
((SELECT id FROM users WHERE username='worker1'), (SELECT id FROM roles WHERE name='ROLE_WORKER'))
ON DUPLICATE KEY UPDATE user_id=user_id;

-- Insert Sample Animals
INSERT INTO animals (tag_number, name, animal_type, breed, date_of_birth, purchase_date, purchase_price, status, health_status) VALUES
('COW001', 'Lakshmi', 'COW', 'HOLSTEIN', '2020-01-15', '2020-01-20', 50000.00, 'ACTIVE', 'Healthy'),
('COW002', 'Ganga', 'COW', 'JERSEY', '2019-05-20', '2019-06-01', 45000.00, 'ACTIVE', 'Healthy'),
('BUFF001', 'Kali', 'BUFFALO', 'MURRAH', '2018-03-10', '2018-03-25', 60000.00, 'ACTIVE', 'Healthy'),
('BUFF002', 'Shyama', 'BUFFALO', 'NILI_RAVI', '2019-08-15', '2019-09-01', 55000.00, 'PREGNANT', 'Healthy')
ON DUPLICATE KEY UPDATE tag_number=tag_number;

-- Insert Sample Customers
INSERT INTO customers (name, address, mobile_number, email, daily_milk_quantity, balance, milk_type, delivery_status) VALUES
('Rajesh Kumar', '123 Main Street, City', '9876543210', 'rajesh@example.com', 5.00, 0.00, 'COW', 'ACTIVE'),
('Priya Sharma', '456 Park Avenue, City', '9876543211', 'priya@example.com', 3.00, 0.00, 'BUFFALO', 'ACTIVE'),
('Amit Patel', '789 Market Road, City', '9876543212', 'amit@example.com', 4.50, 0.00, 'BOTH', 'ACTIVE')
ON DUPLICATE KEY UPDATE mobile_number=mobile_number;

-- Insert Sample Inventory Items
INSERT INTO inventory_items (name, category, quantity, unit, cost_per_unit, total_cost, supplier_name, purchase_date, low_stock_threshold) VALUES
('Cattle Feed', 'FODDER', 1000.00, 'kg', 25.00, 25000.00, 'Feed Supplier Co.', '2024-01-01', 100.00),
('Antibiotic Injection', 'MEDICINE', 50.00, 'vials', 150.00, 7500.00, 'Pharma Ltd.', '2024-01-15', 10.00),
('Milk Cans', 'CANS', 20.00, 'pieces', 500.00, 10000.00, 'Equipment Supplier', '2024-01-10', 5.00),
('Packaging Bags', 'PACKAGING', 500.00, 'pieces', 2.00, 1000.00, 'Packaging Co.', '2024-01-20', 50.00)
ON DUPLICATE KEY UPDATE name=name;

-- Insert Sample Milk Entries (Today)
INSERT INTO milk_entries (animal_id, entry_date, morning_quantity, evening_quantity, total_quantity, quality_grade) VALUES
((SELECT id FROM animals WHERE tag_number='COW001'), CURDATE(), 8.50, 7.20, 15.70, 'A'),
((SELECT id FROM animals WHERE tag_number='COW002'), CURDATE(), 6.00, 5.50, 11.50, 'A'),
((SELECT id FROM animals WHERE tag_number='BUFF001'), CURDATE(), 10.00, 9.50, 19.50, 'A'),
((SELECT id FROM animals WHERE tag_number='BUFF002'), CURDATE(), 8.00, 7.50, 15.50, 'A')
ON DUPLICATE KEY UPDATE entry_date=entry_date;

-- Insert Sample Receipts
INSERT INTO receipts (receipt_number, customer_id, receipt_date, quantity_liters, milk_rate, total_amount, payment_status, paid_amount, pending_amount) VALUES
('RCP-20240101001', (SELECT id FROM customers WHERE mobile_number='9876543210'), CURDATE(), 5.00, 60.00, 300.00, 'PAID', 300.00, 0.00),
('RCP-20240101002', (SELECT id FROM customers WHERE mobile_number='9876543211'), CURDATE(), 3.00, 70.00, 210.00, 'PENDING', 0.00, 210.00),
('RCP-20240101003', (SELECT id FROM customers WHERE mobile_number='9876543212'), CURDATE(), 4.50, 65.00, 292.50, 'PARTIAL', 150.00, 142.50)
ON DUPLICATE KEY UPDATE receipt_number=receipt_number;

-- Insert Sample Expenses
INSERT INTO expenses (expense_date, category, description, amount, payment_method) VALUES
(CURDATE(), 'FEED', 'Cattle feed purchase', 5000.00, 'CASH'),
(CURDATE(), 'UTILITIES', 'Electricity bill', 2000.00, 'ONLINE'),
(CURDATE(), 'MAINTENANCE', 'Equipment repair', 1500.00, 'CASH')
ON DUPLICATE KEY UPDATE expense_date=expense_date;

