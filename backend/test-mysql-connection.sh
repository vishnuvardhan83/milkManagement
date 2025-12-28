#!/bin/bash

# MySQL Connection Test Script
echo "🔍 Testing MySQL Connection..."
echo ""

# Prompt for password
read -sp "Enter MySQL root password: " password
echo ""

# Test connection
mysql -u root -p"$password" -e "SELECT 'Connection successful!' AS status;" 2>&1

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ MySQL connection successful!"
    echo ""
    echo "You can now use this password in your application:"
    echo "  export DB_PASSWORD=$password"
    echo "  cd backend && mvn spring-boot:run"
else
    echo ""
    echo "❌ MySQL connection failed!"
    echo ""
    echo "Possible issues:"
    echo "1. Wrong password"
    echo "2. MySQL not running"
    echo "3. User doesn't have permissions"
    echo ""
    echo "To reset MySQL password:"
    echo "  mysql -u root -p"
    echo "  ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_password';"
    echo "  FLUSH PRIVILEGES;"
fi

