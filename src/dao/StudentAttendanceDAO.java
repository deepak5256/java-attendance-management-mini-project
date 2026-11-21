package dao;

import config.DatabaseConfig;
import model.StudentAttendance;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Student Attendance CRUD operations.
 * Implements strict PreparedStatement parameterization to prevent SQL Injection.
 */
public class StudentAttendanceDAO {

    /**
     * Inserts a new student attendance record into the database.
     */
    public boolean insert(StudentAttendance record) throws SQLException {
        String sql = "INSERT INTO student_attendance " +
                     "(attendance_id, student_name, course_code, attended_classes, total_classes, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, record.getAttendanceId());
            pstmt.setString(2, record.getStudentName());
            pstmt.setString(3, record.getCourseCode());
            pstmt.setInt(4, record.getAttendedClasses());
            pstmt.setInt(5, record.getTotalClasses());
            pstmt.setString(6, record.getStatus());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Searches for a record by its Primary Key (attendance_id).
     */
    public StudentAttendance findById(int id) throws SQLException {
        String sql = "SELECT attendance_id, student_name, course_code, attended_classes, total_classes, status " +
                     "FROM student_attendance WHERE attendance_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * Updates an existing student attendance record.
     */
    public boolean update(StudentAttendance record) throws SQLException {
        String sql = "UPDATE student_attendance SET " +
                     "student_name = ?, course_code = ?, attended_classes = ?, total_classes = ?, status = ? " +
                     "WHERE attendance_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, record.getStudentName());
            pstmt.setString(2, record.getCourseCode());
            pstmt.setInt(3, record.getAttendedClasses());
            pstmt.setInt(4, record.getTotalClasses());
            pstmt.setString(5, record.getStatus());
            pstmt.setInt(6, record.getAttendanceId());

            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Deletes a student attendance record by its Primary Key.
     */
    public boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM student_attendance WHERE attendance_id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * Retrieves all student attendance records sorted by ID.
     */
    public List<StudentAttendance> findAll() throws SQLException {
        List<StudentAttendance> list = new ArrayList<>();
        String sql = "SELECT attendance_id, student_name, course_code, attended_classes, total_classes, status " +
                     "FROM student_attendance ORDER BY attendance_id ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    /**
     * Searches records by student name or course code using wildcard matching.
     */
    public List<StudentAttendance> search(String keyword) throws SQLException {
        List<StudentAttendance> list = new ArrayList<>();
        String sql = "SELECT attendance_id, student_name, course_code, attended_classes, total_classes, status " +
                     "FROM student_attendance WHERE LOWER(student_name) LIKE ? OR LOWER(course_code) LIKE ? " +
                     "ORDER BY attendance_id ASC";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String term = "%" + keyword.toLowerCase().trim() + "%";
            pstmt.setString(1, term);
            pstmt.setString(2, term);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        }
        return list;
    }

    /**
     * Checks if a specific ID already exists.
     */
    public boolean existsById(int id) throws SQLException {
        String sql = "SELECT 1 FROM student_attendance WHERE attendance_id = ?";
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private StudentAttendance mapRow(ResultSet rs) throws SQLException {
        return new StudentAttendance(
                rs.getInt("attendance_id"),
                rs.getString("student_name"),
                rs.getString("course_code"),
                rs.getInt("attended_classes"),
                rs.getInt("total_classes"),
                rs.getString("status")
        );
    }
}
