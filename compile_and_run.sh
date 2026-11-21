#!/usr/bin/env bash
# ==============================================================================
# Java Mini-Project Reference: Compile and Run Script (Linux/macOS)
# ==============================================================================

set -e
mkdir -p bin

echo "[1/2] Compiling Java source files..."
javac -cp ".:lib/*" -d bin src/config/*.java src/model/*.java src/dao/*.java src/ui/*.java src/Main.java

echo "[2/2] Launching Application..."
java -cp "bin:lib/*" Main
