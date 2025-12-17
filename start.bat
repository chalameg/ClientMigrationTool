@echo off
REM Client Migration Tool - Startup Script for Windows
REM This script starts both backend and frontend servers

echo.
echo ================================
echo  Client Migration Tool
echo ================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Java not found. Please install Java 17+
    pause
    exit /b 1
)

REM Check if Node is installed
node --version >nul 2>&1
if errorlevel 1 (
    echo [ERROR] Node.js not found. Please install Node.js 18+
    pause
    exit /b 1
)

echo [1/4] Starting Backend...
echo.

REM Start Backend
cd backend
if not exist "target\" (
    echo Installing backend dependencies...
    call mvn clean install -DskipTests
)

start "Backend Server" cmd /k "mvn spring-boot:run"
cd ..

echo [2/4] Backend starting on http://localhost:9091
echo.

REM Wait a bit for backend
timeout /t 3 /nobreak >nul

echo [3/4] Starting Frontend...
echo.

REM Start Frontend
cd frontend
if not exist "node_modules\" (
    echo Installing frontend dependencies...
    call npm install
)

start "Frontend Server" cmd /k "npm run dev"
cd ..

echo [4/4] Frontend starting on http://localhost:5454
echo.

REM Wait for servers to start
timeout /t 5 /nobreak >nul

echo ================================
echo  Application Ready!
echo ================================
echo.
echo  Frontend: http://localhost:5454
echo  Backend:  http://localhost:9091
echo.
echo Two command windows have been opened:
echo  - Backend Server (Spring Boot)
echo  - Frontend Server (Vue.js)
echo.
echo Close those windows to stop the servers.
echo.
pause

