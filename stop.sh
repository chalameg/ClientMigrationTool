#!/bin/bash

# Stop all running servers

echo "🛑 Stopping Client Migration Tool servers..."

# Stop backend (Spring Boot on port 9091)
BACKEND_PID=$(lsof -ti:9091)
if [ ! -z "$BACKEND_PID" ]; then
    echo "Stopping backend (PID: $BACKEND_PID)..."
    kill $BACKEND_PID
    echo "✓ Backend stopped"
else
    echo "Backend not running"
fi

# Stop frontend (Vite on port 5454)
FRONTEND_PID=$(lsof -ti:5454)
if [ ! -z "$FRONTEND_PID" ]; then
    echo "Stopping frontend (PID: $FRONTEND_PID)..."
    kill $FRONTEND_PID
    echo "✓ Frontend stopped"
else
    echo "Frontend not running"
fi

echo ""
echo "✨ All servers stopped"

