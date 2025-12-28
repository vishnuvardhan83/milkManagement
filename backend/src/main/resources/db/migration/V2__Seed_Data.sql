INSERT INTO roles (name, description) VALUES
('ROLE_SUPERADMIN', 'Super Administrator with full access'),
('ROLE_FARM_ADMIN', 'Farm Administrator'),
('ROLE_WORKER', 'Farm Worker'),
('ROLE_DELIVERY_BOY', 'Delivery Personnel'),
('ROLE_CUSTOMER', 'Customer')
ON DUPLICATE KEY UPDATE name=name;

INSERT INTO farms (tenant_id, name, address, phone, email, subscription_enabled, delivery_enabled, active) VALUES
('default', 'Green Valley Dairy Farm', '123 Farm Road, City', '1234567890', 'info@greenvalleydairy.com', TRUE, TRUE, TRUE)
ON DUPLICATE KEY UPDATE tenant_id=tenant_id;

INSERT INTO users (tenant_id, username, email, password, full_name, phone, enabled) VALUES
('default', 'superadmin', 'superadmin@dairyfarm.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJZ2a', 'Super Admin', '1234567890', TRUE),
('default', 'admin', 'admin@dairyfarm.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJZ2a', 'Farm Admin', '1234567891', TRUE),
('default', 'worker1', 'worker1@dairyfarm.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJZ2a', 'Worker One', '1234567892', TRUE),
('default', 'delivery1', 'delivery1@dairyfarm.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iwK8pJZ2a', 'Delivery Boy One', '1234567893', TRUE)
ON DUPLICATE KEY UPDATE username=username;

INSERT INTO user_roles (user_id, role_id) VALUES
((SELECT id FROM users WHERE username='superadmin'), (SELECT id FROM roles WHERE name='ROLE_SUPERADMIN')),
((SELECT id FROM users WHERE username='admin'), (SELECT id FROM roles WHERE name='ROLE_FARM_ADMIN')),
((SELECT id FROM users WHERE username='worker1'), (SELECT id FROM roles WHERE name='ROLE_WORKER')),
((SELECT id FROM users WHERE username='delivery1'), (SELECT id FROM roles WHERE name='ROLE_DELIVERY_BOY'))
ON DUPLICATE KEY UPDATE user_id=user_id;

INSERT INTO product_catalog (tenant_id, name, product_type, description, unit, price_per_unit, subscription_price, available_for_subscription, available_for_daily_sale, active) VALUES
('default', 'Fresh Cow Milk', 'MILK', 'Pure fresh cow milk', 'Liter', 60.00, 55.00, TRUE, TRUE, TRUE),
('default', 'Fresh Buffalo Milk', 'MILK', 'Pure fresh buffalo milk', 'Liter', 70.00, 65.00, TRUE, TRUE, TRUE),
('default', 'Homemade Curd', 'CURD', 'Fresh homemade curd', 'Kg', 80.00, 75.00, TRUE, TRUE, TRUE),
('default', 'Pure Ghee', 'GHEE', 'Pure desi ghee', 'Kg', 600.00, 580.00, TRUE, TRUE, TRUE),
('default', 'Fresh Paneer', 'PANEER', 'Fresh homemade paneer', 'Kg', 350.00, 330.00, TRUE, TRUE, TRUE),
('default', 'Butter', 'BUTTER', 'Fresh butter', 'Kg', 450.00, 430.00, TRUE, TRUE, TRUE)
ON DUPLICATE KEY UPDATE name=name;

INSERT INTO subscription_plans (tenant_id, name, description, plan_type, monthly_price, duration_days, products_json, active) VALUES
('default', 'Basic Milk Pass', '1L cow milk per day', 'BASIC_MILK_PASS', 1650.00, 30, '{"products":[{"id":1,"quantity":1,"unit":"Liter"}]}', TRUE),
('default', 'Family Pack', '3L milk + weekly curd', 'FAMILY_PACK', 4500.00, 30, '{"products":[{"id":1,"quantity":3,"unit":"Liter"},{"id":3,"quantity":0.5,"unit":"Kg","frequency":"weekly"}]}', TRUE),
('default', 'Premium Package', 'Custom selection of products', 'PREMIUM', 5000.00, 30, '{"products":[]}', TRUE)
ON DUPLICATE KEY UPDATE name=name;

