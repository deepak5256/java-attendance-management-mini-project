@echo off
REM ==============================================================================
REM Java Mini-Project Reference: 1-Click Compile and Run Script (Windows CMD)
REM ==============================================================================

echo [1/3] Creating output directory 'bin'...
if not exist "bin" mkdir bin

echo [2/3] Compiling Java source files...
javac -cp ".;lib\*" -d bin src\config\*.java src\model\*.java src\dao\*.java src\ui\*.java src\Main.java

if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Compilation failed! Please check code or Java compiler.
    pause
    exit /b %ERRORLEVEL%
)

echo [3/3] Launching Student Attendance Management System...
java -cp "bin;lib\*" Main

pause
