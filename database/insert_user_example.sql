-- Example: Insert a new user for login
-- Password: user123 (hashed with BCrypt)

-- First, get the role ID (assuming ROLE_CUSTOMER exists)
SET @role_id = (SELECT id FROM roles WHERE name = 'ROLE_CUSTOMER' LIMIT 1);

-- Insert the user
INSERT INTO users (username, email, password, enabled) 
VALUES ('testuser', 'testuser@milk.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwy8pK5qO', TRUE);

-- Get the user ID
SET @user_id = LAST_INSERT_ID();

-- Assign role to user
INSERT INTO user_roles (user_id, role_id) 
VALUES (@user_id, @role_id);

-- Login Credentials:
-- Username: testuser
-- Password: user123
-- Email: testuser@milk.com
-- Role: ROLE_CUSTOMER
