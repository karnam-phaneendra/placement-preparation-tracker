@echo off
title Placement Preparation Tracker - Localhost Web Server
cd /d "%~dp0"
echo ===============================================================================
echo   STARTING PLACEMENT PREPARATION TRACKER ON LOCALHOST
echo ===============================================================================
echo   URL: http://localhost:8080/
echo   Database: MySQL (placement_tracker on localhost:3306)
echo   Student: Karnam Phaneendra (PIN: 24A85A6102)
echo ===============================================================================

start http://localhost:8080/

java -cp "D:/24a85a6102/jar/jar files/mysql-connector-j-9.0.0.jar.zip;bin" com.placementtracker.web.WebServer 8080
pause
