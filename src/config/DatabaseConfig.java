package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Centralized Database Configuration and Connection Factory.
 * Supports PostgreSQL and MySQL with automatic table initialization.
 */
public class DatabaseConfig {

    // Database Connection Parameters (PostgreSQL Default)
    public static final String DB_TYPE = "POSTGRESQL"; // or "MYSQL"
    public static final String HOST    = "localhost";
    public static final int    PORT    = 5432;
    public static final String DB_NAME = "postgres";
    public static final String USER    = "postgres";
    public static final String PASS    = "Admin123";

    public static final String URL = "jdbc:postgresql://" + HOST + ":" + PORT + "/" + DB_NAME;

    /**
     * Establishes and returns a live database connection.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    /**
     * Tests database connectivity.
     * @return true if connected successfully, false otherwise.
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Automatically initializes the required table if it does not already exist.
     */
    public static void initializeDatabase() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS student_attendance (" +
                "attendance_id INT PRIMARY KEY, " +
                "student_name VARCHAR(100) NOT NULL, " +
                "course_code VARCHAR(20) NOT NULL, " +
                "attended_classes INT NOT NULL, " +
                "total_classes INT NOT NULL DEFAULT 50, " +
                "status VARCHAR(30) NOT NULL" +
                ")";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("[DatabaseConfig] Table 'student_attendance' verified/created successfully.");
        } catch (SQLException e) {
            System.err.println("[DatabaseConfig] Table initialization note: " + e.getMessage());
        }
    }
}
