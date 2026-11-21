-- ==============================================================================
-- Java Mini-Project Reference: Student Attendance Management System
-- Database Schema: PostgreSQL
-- Table: student_attendance
-- ==============================================================================

CREATE TABLE IF NOT EXISTS student_attendance (
    attendance_id INT PRIMARY KEY,
    student_name VARCHAR(100) NOT NULL,
    course_code VARCHAR(20) NOT NULL,
    attended_classes INT NOT NULL,
    total_classes INT NOT NULL DEFAULT 50,
    status VARCHAR(30) NOT NULL
);

-- Indexes for efficient lookups
CREATE INDEX IF NOT EXISTS idx_student_name ON student_attendance(student_name);
CREATE INDEX IF NOT EXISTS idx_course_code ON student_attendance(course_code);
CREATE INDEX IF NOT EXISTS idx_status ON student_attendance(status);
