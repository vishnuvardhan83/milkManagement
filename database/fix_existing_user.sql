-- Fix Existing Admin User - Assign ROLE_ADMIN role
-- Run this if you already have an admin user but no role assigned

USE milk_management;

-- Check if roles exist, if not create them
INSERT IGNORE INTO roles (name, description) VALUES
('ROLE_ADMIN', 'Administrator with full access'),
('ROLE_MANAGER', 'Manager with operational access'),
('ROLE_CUSTOMER', 'Customer with limited access');

-- Get admin user ID (assuming user with id=1 is admin)
SET @admin_user_id = (SELECT id FROM users WHERE username = 'admin' LIMIT 1);

-- Get ROLE_ADMIN ID
SET @admin_role_id = (SELECT id FROM roles WHERE name = 'ROLE_ADMIN' LIMIT 1);

-- Check if role exists
SELECT @admin_role_id as admin_role_id, @admin_user_id as admin_user_id;

-- Assign role if both exist
-- If you get NULL for admin_role_id, the roles table is empty - run the roles insert first
INSERT INTO user_roles (user_id, role_id) 
SELECT @admin_user_id, @admin_role_id
WHERE @admin_user_id IS NOT NULL AND @admin_role_id IS NOT NULL
ON DUPLICATE KEY UPDATE user_id=user_id;

-- Verify the assignment
SELECT u.id, u.username, u.email, r.name as role 
FROM users u 
LEFT JOIN user_roles ur ON u.id = ur.user_id 
LEFT JOIN roles r ON ur.role_id = r.id 
WHERE u.username = 'admin';
