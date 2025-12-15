-- Insert Default Users for Login
-- NOTE: Passwords must be hashed using BCrypt before inserting
-- Use the PasswordHashGenerator utility class to generate hashes
-- Or use the /api/auth/hash-password endpoint (if enabled)

USE milk_management;

-- Ensure roles exist (if not already inserted)
INSERT IGNORE INTO roles (name, description) VALUES
('ROLE_ADMIN', 'Administrator with full access'),
('ROLE_MANAGER', 'Manager with operational access'),
('ROLE_CUSTOMER', 'Customer with limited access');

-- ==========================================
-- IMPORTANT: Replace the password hashes below with actual BCrypt hashes
-- To generate hashes:
-- 1. Run: java -cp target/classes com.milkmanagement.util.PasswordHashGenerator
-- 2. Or use the Spring Boot application's password hashing endpoint
-- 3. Or use online BCrypt generator: https://bcrypt-generator.com/
-- ==========================================

-- Insert Admin User
-- Username: admin
-- Password: admin123 (REPLACE HASH BELOW)
-- Email: admin@milk.com
INSERT INTO users (username, email, password, enabled) 
VALUES ('admin', 'admin@milk.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pK5qO', TRUE)
ON DUPLICATE KEY UPDATE username=username;

-- Get admin user ID
SET @admin_user_id = (SELECT id FROM users WHERE username = 'admin' LIMIT 1);

-- Assign ROLE_ADMIN to admin
SET @admin_role_id = (SELECT id FROM roles WHERE name = 'ROLE_ADMIN' LIMIT 1);
INSERT INTO user_roles (user_id, role_id) 
VALUES (@admin_user_id, @admin_role_id)
ON DUPLICATE KEY UPDATE user_id=user_id;

-- Insert Customer User
-- Username: customer
-- Password: admin123 (REPLACE HASH BELOW)
-- Email: customer@milk.com
INSERT INTO users (username, email, password, enabled) 
VALUES ('customer', 'customer@milk.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pK5qO', TRUE)
ON DUPLICATE KEY UPDATE username=username;

-- Get customer user ID
SET @customer_user_id = (SELECT id FROM users WHERE username = 'customer' LIMIT 1);

-- Assign ROLE_CUSTOMER to customer
SET @customer_role_id = (SELECT id FROM roles WHERE name = 'ROLE_CUSTOMER' LIMIT 1);
INSERT INTO user_roles (user_id, role_id) 
VALUES (@customer_user_id, @customer_role_id)
ON DUPLICATE KEY UPDATE user_id=user_id;

-- Create customer record for the customer user
INSERT INTO customers (name, email, mobile_number, daily_milk_quantity, milk_type, delivery_status, created_by)
VALUES ('Customer User', 'customer@milk.com', '0000000000', 0.00, 'COW', 'ACTIVE', @customer_user_id)
ON DUPLICATE KEY UPDATE name=name;

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
