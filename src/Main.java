import config.DatabaseConfig;
import ui.StudentAttendanceUI;

import javax.swing.*;

/**
 * Main Application Launcher for Student Attendance Management System.
 * Ensures GUI is created safely on the Swing Event Dispatch Thread (EDT).
 */
public class Main {
    public static void main(String[] args) {
        // 1. Attempt to apply modern System Look & Feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fallback to default Java Swing look and feel
        }

        // 2. Initialize Database and ensure table exists
        DatabaseConfig.initializeDatabase();

        // 3. Launch GUI safely on Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            StudentAttendanceUI app = new StudentAttendanceUI();
            app.setVisible(true);
        });
    }
}
