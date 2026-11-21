#!/usr/bin/env bash
# ==============================================================================
# Java Mini-Project Reference: Compile and Run Script (Linux/macOS)
# ==============================================================================

set -e

echo "[1/2] Compiling StudentAttendanceApp.java..."
javac -cp ".:lib/*" StudentAttendanceApp.java

echo "[2/2] Launching Student Attendance Management System..."
java -cp ".:lib/*" StudentAttendanceApp
