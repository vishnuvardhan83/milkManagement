#!/bin/bash

# Start Backend Script
# This script helps you start the backend with proper MySQL password configuration

echo "🚀 Starting Dairy Farm ERP Backend"
echo "=================================="
echo ""

# Navigate to project root
cd "$(dirname "$0")"

# Check if backend directory exists
if [ ! -d "backend" ]; then
    echo "❌ Error: backend directory not found!"
    echo "   Current directory: $(pwd)"
    echo "   Please run this script from the project root directory."
    exit 1
fi

echo "✅ Found backend directory"
echo ""

# Check if MySQL password is set
if [ -z "$DB_PASSWORD" ]; then
    echo "⚠️  MySQL password not set in environment variable DB_PASSWORD"
    echo ""
    echo "Please set your MySQL password:"
    echo ""
    read -sp "Enter MySQL root password: " password
    echo ""
    if [ -n "$password" ]; then
        export DB_PASSWORD="$password"
        echo "✅ Password set for this session"
    else
        echo "⚠️  No password provided. Backend may fail if MySQL requires a password."
    fi
else
    echo "✅ Using MySQL password from DB_PASSWORD environment variable"
fi

echo ""
echo "📦 Starting Spring Boot application..."
echo "   Working directory: $(pwd)/backend"
echo ""

# Navigate to backend and start
cd backend
mvn spring-boot:run

