#!/bin/bash

# MySQL Password Reset Script for macOS
echo "🔐 MySQL Password Reset Helper"
echo "=============================="
echo ""

# Check if MySQL is running
if ! brew services list | grep -q "mysql.*started"; then
    echo "⚠️  MySQL is not running. Starting MySQL..."
    brew services start mysql
    sleep 3
fi

echo "This script will help you reset your MySQL root password."
echo ""
echo "Option 1: Reset password (recommended)"
echo "Option 2: Test current password"
echo "Option 3: Skip password (not recommended)"
echo ""
read -p "Choose option (1/2/3): " option

case $option in
    1)
        echo ""
        echo "📝 Resetting MySQL password..."
        echo ""
        read -sp "Enter NEW MySQL root password: " new_password
        echo ""
        read -sp "Confirm NEW password: " confirm_password
        echo ""
        
        if [ "$new_password" != "$confirm_password" ]; then
            echo "❌ Passwords don't match!"
            exit 1
        fi
        
        echo ""
        echo "🔄 Stopping MySQL..."
        brew services stop mysql
        
        echo "⏳ Waiting 2 seconds..."
        sleep 2
        
        echo "🚀 Starting MySQL in safe mode..."
        mysqld_safe --skip-grant-tables --skip-networking > /dev/null 2>&1 &
        MYSQL_PID=$!
        
        echo "⏳ Waiting for MySQL to start..."
        sleep 5
        
        echo "🔧 Resetting password..."
        mysql -u root <<EOF
USE mysql;
ALTER USER 'root'@'localhost' IDENTIFIED BY '$new_password';
FLUSH PRIVILEGES;
EXIT;
EOF
        
        echo "🛑 Stopping MySQL safe mode..."
        kill $MYSQL_PID 2>/dev/null
        sleep 2
        
        echo "🔄 Restarting MySQL normally..."
        brew services start mysql
        sleep 3
        
        echo ""
        echo "✅ Password reset complete!"
        echo ""
        echo "Test the new password:"
        echo "  mysql -u root -p"
        echo ""
        echo "Then set it in your application:"
        echo "  export DB_PASSWORD=$new_password"
        echo "  cd backend && mvn spring-boot:run"
        ;;
    2)
        echo ""
        read -sp "Enter current MySQL root password to test: " test_password
        echo ""
        mysql -u root -p"$test_password" -e "SELECT 'Connection successful!' AS status;" 2>&1
        
        if [ $? -eq 0 ]; then
            echo ""
            echo "✅ Password is correct!"
            echo ""
            echo "Set it in your application:"
            echo "  export DB_PASSWORD=$test_password"
            echo "  cd backend && mvn spring-boot:run"
        else
            echo ""
            echo "❌ Password is incorrect!"
            echo "   Run this script again and choose Option 1 to reset."
        fi
        ;;
    3)
        echo ""
        echo "⚠️  Skipping password (not recommended for production)"
        echo ""
        echo "This will try to connect without a password."
        echo "Set in application:"
        echo "  export DB_PASSWORD="
        echo "  cd backend && mvn spring-boot:run"
        ;;
    *)
        echo "Invalid option!"
        exit 1
        ;;
esac

