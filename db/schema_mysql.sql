-- ==============================================================================
-- Java Mini-Project Reference: Student Attendance Management System
-- Database Schema: MySQL
-- Table: student_attendance
-- ==============================================================================

CREATE TABLE IF NOT EXISTS student_attendance (
    attendance_id INT PRIMARY KEY,
    student_name VARCHAR(100) NOT NULL,
    course_code VARCHAR(20) NOT NULL,
    attended_classes INT NOT NULL,
    total_classes INT NOT NULL DEFAULT 50,
    status VARCHAR(30) NOT NULL,
    INDEX idx_student_name (student_name),
    INDEX idx_course_code (course_code),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
