# 🔐 MySQL Password Issue - Fix Guide

## Current Error
```
Access denied for user 'root'@'localhost' (using password: YES)
```

This means the password is being sent, but it's **incorrect**.

## ✅ Solution Steps

### Step 1: Test MySQL Connection

Run the test script:
```bash
cd backend
./test-mysql-connection.sh
```

Or test manually:
```bash
mysql -u root -p
# Enter your password when prompted
```

### Step 2: Find Your Correct Password

If you don't remember your MySQL root password, you have a few options:

#### Option A: Check if MySQL has no password
```bash
mysql -u root
# If this works, MySQL has no password
# Then set: export DB_PASSWORD=
```

#### Option B: Reset MySQL Password

1. **Stop MySQL:**
   ```bash
   brew services stop mysql
   ```

2. **Start MySQL in safe mode:**
   ```bash
   mysqld_safe --skip-grant-tables &
   ```

3. **Connect without password:**
   ```bash
   mysql -u root
   ```

4. **Reset password:**
   ```sql
   USE mysql;
   ALTER USER 'root'@'localhost' IDENTIFIED BY 'your_new_password';
   FLUSH PRIVILEGES;
   EXIT;
   ```

5. **Restart MySQL normally:**
   ```bash
   brew services stop mysql
   brew services start mysql
   ```

6. **Test new password:**
   ```bash
   mysql -u root -p
   # Enter your_new_password
   ```

#### Option C: Use MySQL Workbench or phpMyAdmin
- These tools can help you reset the password through GUI

### Step 3: Update Application with Correct Password

Once you have the correct password:

**Method 1: Environment Variable (Recommended)**
```bash
export DB_PASSWORD=your_correct_password
cd backend
mvn spring-boot:run
```

**Method 2: Edit application.properties**
Edit `backend/src/main/resources/application.properties`:
```properties
spring.datasource.password=your_correct_password
```

**Method 3: Use startup script**
```bash
cd backend
./start-backend.sh your_correct_password
```

## 🔍 Quick Diagnostic Commands

```bash
# Check if MySQL is running
brew services list | grep mysql

# Check MySQL version
mysql --version

# Try connecting (will prompt for password)
mysql -u root -p

# Check MySQL users
mysql -u root -p -e "SELECT user, host FROM mysql.user WHERE user='root';"
```

## 💡 Common Issues

### Issue 1: MySQL Not Running
```bash
brew services start mysql
```

### Issue 2: Wrong Username
If your MySQL user is not 'root', update `application.properties`:
```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Issue 3: Different Port
If MySQL is on a different port (not 3306):
```properties
spring.datasource.url=jdbc:mysql://localhost:3307/dairy_farm_erp?...
```

## 🚀 After Fixing Password

Once you have the correct password set:

1. **Start Backend:**
   ```bash
   export DB_PASSWORD=your_correct_password
   cd backend
   mvn spring-boot:run
   ```

2. **Verify it works:**
   - Look for: "Started DairyFarmErpApplication"
   - Check: http://localhost:8080/actuator/health

3. **Start Frontend:**
   ```bash
   cd frontend
   ng serve
   ```

## 📝 Notes

- The password is case-sensitive
- Make sure there are no extra spaces
- If using special characters, you may need to URL-encode them
- Environment variables persist only in the current terminal session

