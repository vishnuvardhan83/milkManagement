-- Insert Default Users for Login (Fixed Version)
-- This script handles existing users and ensures roles exist

USE milk_management;

-- Step 1: Ensure roles exist
INSERT IGNORE INTO roles (name, description) VALUES
('ROLE_ADMIN', 'Administrator with full access'),
('ROLE_MANAGER', 'Manager with operational access'),
('ROLE_CUSTOMER', 'Customer with limited access');

-- Step 2: Insert or Update Admin User
-- Username: admin
-- Password: admin123
-- Email: admin@milk.com
INSERT INTO users (username, email, password, enabled) 
VALUES ('admin', 'admin@milk.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pK5qO', TRUE)
ON DUPLICATE KEY UPDATE 
    email = VALUES(email),
    password = VALUES(password),
    enabled = VALUES(enabled);

-- Step 3: Get admin user ID and assign ROLE_ADMIN
SET @admin_user_id = (SELECT id FROM users WHERE username = 'admin' LIMIT 1);
SET @admin_role_id = (SELECT id FROM roles WHERE name = 'ROLE_ADMIN' LIMIT 1);

-- Only insert if both IDs exist
INSERT INTO user_roles (user_id, role_id) 
SELECT @admin_user_id, @admin_role_id
WHERE @admin_user_id IS NOT NULL AND @admin_role_id IS NOT NULL
ON DUPLICATE KEY UPDATE user_id=user_id;

-- Step 4: Insert or Update Customer User
-- Username: customer
-- Password: admin123
-- Email: customer@milk.com
INSERT INTO users (username, email, password, enabled) 
VALUES ('customer', 'customer@milk.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pK5qO', TRUE)
ON DUPLICATE KEY UPDATE 
    email = VALUES(email),
    password = VALUES(password),
    enabled = VALUES(enabled);

-- Step 5: Get customer user ID and assign ROLE_CUSTOMER
SET @customer_user_id = (SELECT id FROM users WHERE username = 'customer' LIMIT 1);
SET @customer_role_id = (SELECT id FROM roles WHERE name = 'ROLE_CUSTOMER' LIMIT 1);

-- Only insert if both IDs exist
INSERT INTO user_roles (user_id, role_id) 
SELECT @customer_user_id, @customer_role_id
WHERE @customer_user_id IS NOT NULL AND @customer_role_id IS NOT NULL
ON DUPLICATE KEY UPDATE user_id=user_id;

-- Step 6: Create customer record for the customer user
INSERT INTO customers (name, email, mobile_number, daily_milk_quantity, milk_type, delivery_status, created_by)
SELECT 'Customer User', 'customer@milk.com', '0000000000', 0.00, 'COW', 'ACTIVE', @customer_user_id
WHERE @customer_user_id IS NOT NULL
ON DUPLICATE KEY UPDATE name=name;

-- Verification Queries (run these to check):
-- SELECT u.id, u.username, u.email, r.name as role 
-- FROM users u 
-- LEFT JOIN user_roles ur ON u.id = ur.user_id 
-- LEFT JOIN roles r ON ur.role_id = r.id 
-- WHERE u.username IN ('admin', 'customer');

-- Summary of Login Credentials:
-- ==========================================
-- ADMIN USER:
--   Username: admin
--   Password: admin123
--   Email: admin@milk.com
--   Role: ROLE_ADMIN
--
-- CUSTOMER USER:
--   Username: customer
--   Password: admin123
--   Email: customer@milk.com
--   Role: ROLE_CUSTOMER
-- ==========================================
