# ==============================================================================
# Java Mini-Project Reference: Compile and Run Script (PowerShell)
# ==============================================================================

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  Student Attendance Management System — Runner" -ForegroundColor Yellow
Write-Host "============================================================" -ForegroundColor Cyan

Write-Host "[1/2] Compiling StudentAttendanceApp.java..." -ForegroundColor Green
& javac -cp ".;lib/*" StudentAttendanceApp.java

if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Compilation failed!" -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "[2/2] Launching Application..." -ForegroundColor Green
& java -cp ".;lib/*" StudentAttendanceApp
