@echo off
title Placement Preparation Tracker - Desktop Application
cd /d "%~dp0"
echo ===============================================================================
echo   STARTING PLACEMENT PREPARATION TRACKER DESKTOP GUI
echo ===============================================================================

java -cp "D:/24a85a6102/jar/jar files/mysql-connector-j-9.0.0.jar.zip;bin" com.placementtracker.util.Main
pause
