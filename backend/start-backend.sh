#!/bin/bash

# Backend Startup Script with Database Password Configuration
# Usage: ./start-backend.sh [mysql_password]

echo "🚀 Starting Dairy Farm ERP Backend..."
echo ""

# Check if password is provided as argument
if [ -n "$1" ]; then
    export DB_PASSWORD="$1"
    echo "✅ Using provided MySQL password"
elif [ -n "$DB_PASSWORD" ]; then
    echo "✅ Using MySQL password from environment variable DB_PASSWORD"
else
    echo "⚠️  MySQL password not set!"
    echo ""
    echo "Please provide your MySQL root password:"
    echo ""
    echo "Option 1: Set environment variable"
    echo "  export DB_PASSWORD=your_password"
    echo "  ./start-backend.sh"
    echo ""
    echo "Option 2: Pass as argument"
    echo "  ./start-backend.sh your_password"
    echo ""
    echo "Option 3: Edit application.properties directly"
    echo "  Edit: backend/src/main/resources/application.properties"
    echo "  Set: spring.datasource.password=your_password"
    echo ""
    read -sp "Enter MySQL root password (or press Enter to skip): " password
    echo ""
    if [ -n "$password" ]; then
        export DB_PASSWORD="$password"
        echo "✅ Password set from input"
    else
        echo "❌ No password provided. Backend may fail to start."
        echo "   You can set it later and restart."
    fi
fi

echo ""
echo "📦 Starting Spring Boot application..."
echo ""

# Start the backend
mvn spring-boot:run

