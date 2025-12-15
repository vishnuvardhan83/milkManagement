#!/bin/bash
# Script to start the Angular frontend

cd "$(dirname "$0")/frontend"

echo "========================================="
echo "Starting Milk Management Frontend"
echo "========================================="

# Check if node_modules exists
if [ ! -d "node_modules" ]; then
    echo "Installing dependencies..."
    npm install
    if [ $? -ne 0 ]; then
        echo "Error: Failed to install dependencies"
        exit 1
    fi
fi

# Check if Angular CLI is installed globally or locally
if ! command -v ng &> /dev/null && [ ! -f "node_modules/.bin/ng" ]; then
    echo "Installing Angular CLI..."
    npm install -g @angular/cli
    if [ $? -ne 0 ]; then
        echo "Error: Failed to install Angular CLI"
        exit 1
    fi
fi

echo ""
echo "Frontend will start on: http://localhost:4200"
echo "Press Ctrl+C to stop the server"
echo "========================================="
echo ""

# Use local ng if available, otherwise use global
if [ -f "node_modules/.bin/ng" ]; then
    ./node_modules/.bin/ng serve
else
    ng serve
fi
