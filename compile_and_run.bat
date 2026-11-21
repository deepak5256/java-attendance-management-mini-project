@echo off
REM ==============================================================================
REM Java Mini-Project Reference: 1-Click Compile and Run Script
REM ==============================================================================

echo [1/2] Compiling StudentAttendanceApp.java...
javac -cp ".;lib\*" StudentAttendanceApp.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed! Please check your code.
    pause
    exit /b %ERRORLEVEL%
)

echo [2/2] Launching Student Attendance Management System...
java -cp ".;lib\*" StudentAttendanceApp

pause
