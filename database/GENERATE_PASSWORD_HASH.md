# How to Generate Password Hashes for SQL Inserts

This guide explains how to generate BCrypt password hashes for inserting users directly into the database.

## Method 1: Using the API Endpoint (Recommended)

1. Start your Spring Boot application
2. Make a POST request to: `http://localhost:8080/api/auth/hash-password`
3. Request body:
```json
{
  "password": "admin123"
}
```

4. Response will include:
```json
{
  "password": "admin123",
  "hash": "$2a$10$...",
  "sqlExample": "INSERT INTO users ..."
}
```

5. Copy the hash and use it in your SQL INSERT statement

### Using cURL:
```bash
curl -X POST http://localhost:8080/api/auth/hash-password \
  -H "Content-Type: application/json" \
  -d '{"password":"admin123"}'
```

## Method 2: Using the PasswordHashGenerator Utility

1. Compile the project:
```bash
cd backend
mvn clean compile
```

2. Run the utility:
```bash
java -cp target/classes:target/dependency/* com.milkmanagement.util.PasswordHashGenerator
```

3. Copy the generated hash to your SQL file

## Method 3: Using Online BCrypt Generator

1. Visit: https://bcrypt-generator.com/
2. Enter your password
3. Set rounds to 10 (default)
4. Click "Generate Hash"
5. Copy the hash

## Example SQL Insert

After generating the hash, use it in your SQL:

```sql
-- Example: Insert user with hashed password
INSERT INTO users (username, email, password, enabled) 
VALUES ('admin', 'admin@milk.com', '$2a$10$YOUR_GENERATED_HASH_HERE', TRUE);

-- Then assign role
SET @user_id = LAST_INSERT_ID();
SET @role_id = (SELECT id FROM roles WHERE name = 'ROLE_ADMIN' LIMIT 1);
INSERT INTO user_roles (user_id, role_id) VALUES (@user_id, @role_id);
```

## Default Users

The file `insert_default_users.sql` contains default users with password `admin123`:
- **Admin**: username=`admin`, password=`admin123`
- **Customer**: username=`customer`, password=`admin123`

**Important**: Change these default passwords after first login in production!
