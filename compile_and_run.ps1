# ==============================================================================
# Java Mini-Project Reference: Compile and Run Script (PowerShell)
# ==============================================================================

Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "  Java Mini-Project Reference: Build & Run Runner" -ForegroundColor Yellow
Write-Host "============================================================" -ForegroundColor Cyan

if (-not (Test-Path "bin")) {
    New-Item -ItemType Directory -Path "bin" | Out-Null
}

Write-Host "[1/2] Compiling Java source files..." -ForegroundColor Green
$javacArgs = @("-cp", ".;lib/*", "-d", "bin", "src/config/*.java", "src/model/*.java", "src/dao/*.java", "src/ui/*.java", "src/Main.java")
& javac @javacArgs

if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Compilation failed!" -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "[2/2] Launching Application..." -ForegroundColor Green
& java -cp "bin;lib/*" Main
