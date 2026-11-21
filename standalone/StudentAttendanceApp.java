import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

/**
 * Student Attendance Management System (Single-File Reference Edition)
 * Combines Java Swing GUI and JDBC PreparedStatement CRUD operations.
 */
public class StudentAttendanceApp extends JFrame {

    // Database Connection Parameters
    private static final String URL  = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASS = "Admin123";

    // Form Fields
    private JTextField txtId        = new JTextField(12);
    private JTextField txtName      = new JTextField(12);
    private JTextField txtCourse    = new JTextField(12);
    private JTextField txtAttended  = new JTextField(12);
    private JTextField txtTotal     = new JTextField(12);
    private JTextField txtStatus    = new JTextField(12);

    // Table
    private DefaultTableModel tableModel;
    private JTable table;

    public StudentAttendanceApp() {
        setTitle("Student Attendance Management System — Reference Project");
        setSize(920, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Top Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.CENTER));
        header.setBackground(new Color(30, 41, 59));
        JLabel title = new JLabel("Student Attendance Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(Color.WHITE);
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Center Split Panel (Form on Left, Table on Right)
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        // Form Panel
        JPanel formPanel = new JPanel(new GridLayout(6, 2, 8, 8));
        formPanel.setPreferredSize(new Dimension(340, 240));
        formPanel.setBorder(BorderFactory.createTitledBorder("Student Details"));

        formPanel.add(new JLabel("Attendance ID (PK):"));
        formPanel.add(txtId);
        formPanel.add(new JLabel("Student Name:"));
        formPanel.add(txtName);
        formPanel.add(new JLabel("Course Code:"));
        formPanel.add(txtCourse);
        formPanel.add(new JLabel("Attended Classes:"));
        formPanel.add(txtAttended);
        formPanel.add(new JLabel("Total Classes:"));
        formPanel.add(txtTotal);
        formPanel.add(new JLabel("Status (Eligible/Shortage):"));
        formPanel.add(txtStatus);

        centerPanel.add(formPanel, BorderLayout.WEST);

        // Table Panel
        String[] columns = {"ID", "Student Name", "Course", "Attended", "Total", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setRowHeight(24);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int r = table.getSelectedRow();
                txtId.setText(table.getValueAt(r, 0).toString());
                txtName.setText(table.getValueAt(r, 1).toString());
                txtCourse.setText(table.getValueAt(r, 2).toString());
                txtAttended.setText(table.getValueAt(r, 3).toString());
                txtTotal.setText(table.getValueAt(r, 4).toString());
                txtStatus.setText(table.getValueAt(r, 5).toString());
            }
        });
        centerPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Button Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        JButton btnInsert = new JButton("Insert / Save");
        JButton btnSearch = new JButton("Search by ID");
        JButton btnUpdate = new JButton("Update Record");
        JButton btnDelete = new JButton("Delete Record");
        JButton btnClear  = new JButton("Clear Form");
        JButton btnRefresh= new JButton("Refresh Table");

        btnPanel.add(btnInsert);
        btnPanel.add(btnSearch);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);
        btnPanel.add(btnRefresh);
        add(btnPanel, BorderLayout.SOUTH);

        // Action Listeners
        btnInsert.addActionListener(e -> insertRecord());
        btnSearch.addActionListener(e -> searchRecord());
        btnUpdate.addActionListener(e -> updateRecord());
        btnDelete.addActionListener(e -> deleteRecord());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> loadRecords());

        // Initialize Table in DB & Load Data
        ensureTableExists();
        loadRecords();
    }

    private void ensureTableExists() {
        String sql = "CREATE TABLE IF NOT EXISTS student_attendance (" +
                     "attendance_id INT PRIMARY KEY, " +
                     "student_name VARCHAR(100) NOT NULL, " +
                     "course_code VARCHAR(20) NOT NULL, " +
                     "attended_classes INT NOT NULL, " +
                     "total_classes INT NOT NULL DEFAULT 50, " +
                     "status VARCHAR(30) NOT NULL)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException ignored) {}
    }

    private void insertRecord() {
        String sql = "INSERT INTO student_attendance (attendance_id, student_name, course_code, attended_classes, total_classes, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (txtId.getText().trim().isEmpty() || txtName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter all required fields!", "Validation Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int attended = Integer.parseInt(txtAttended.getText().trim());
            int total = Integer.parseInt(txtTotal.getText().trim().isEmpty() ? "50" : txtTotal.getText().trim());
            String status = txtStatus.getText().trim();
            if (status.isEmpty()) {
                status = (attended * 100.0 / total) >= 75.0 ? "Eligible" : "Attendance Shortage";
            }

            pstmt.setInt(1, Integer.parseInt(txtId.getText().trim()));
            pstmt.setString(2, txtName.getText().trim());
            pstmt.setString(3, txtCourse.getText().trim());
            pstmt.setInt(4, attended);
            pstmt.setInt(5, total);
            pstmt.setString(6, status);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Record saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadRecords();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Numeric fields (ID, Attended, Total) must contain valid numbers!", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "SQL Exception", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchRecord() {
        String sql = "SELECT * FROM student_attendance WHERE attendance_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int id = Integer.parseInt(txtId.getText().trim());
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    txtName.setText(rs.getString("student_name"));
                    txtCourse.setText(rs.getString("course_code"));
                    txtAttended.setText(String.valueOf(rs.getInt("attended_classes")));
                    txtTotal.setText(String.valueOf(rs.getInt("total_classes")));
                    txtStatus.setText(rs.getString("status"));
                    JOptionPane.showMessageDialog(this, "Record found for ID: " + id, "Found", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No record found for ID: " + id, "Not Found", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Search Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateRecord() {
        String sql = "UPDATE student_attendance SET student_name=?, course_code=?, attended_classes=?, total_classes=?, status=? WHERE attendance_id=?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int attended = Integer.parseInt(txtAttended.getText().trim());
            int total = Integer.parseInt(txtTotal.getText().trim());
            String status = txtStatus.getText().trim();
            if (status.isEmpty()) {
                status = (attended * 100.0 / total) >= 75.0 ? "Eligible" : "Attendance Shortage";
            }

            pstmt.setString(1, txtName.getText().trim());
            pstmt.setString(2, txtCourse.getText().trim());
            pstmt.setInt(3, attended);
            pstmt.setInt(4, total);
            pstmt.setString(5, status);
            pstmt.setInt(6, Integer.parseInt(txtId.getText().trim()));

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(this, "Record updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadRecords();
            } else {
                JOptionPane.showMessageDialog(this, "No record found with that ID to update.", "Not Found", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Update Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteRecord() {
        String sql = "DELETE FROM student_attendance WHERE attendance_id = ?";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int id = Integer.parseInt(txtId.getText().trim());
            pstmt.setInt(1, id);
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(this, "Record deleted successfully!", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadRecords();
            } else {
                JOptionPane.showMessageDialog(this, "Record not found to delete.", "Not Found", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Delete Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadRecords() {
        tableModel.setRowCount(0);
        String sql = "SELECT * FROM student_attendance ORDER BY attendance_id ASC";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("attendance_id"),
                    rs.getString("student_name"),
                    rs.getString("course_code"),
                    rs.getInt("attended_classes"),
                    rs.getInt("total_classes"),
                    rs.getString("status")
                });
            }
        } catch (SQLException ignored) {}
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtCourse.setText("");
        txtAttended.setText("");
        txtTotal.setText("50");
        txtStatus.setText("");
        table.clearSelection();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new StudentAttendanceApp().setVisible(true);
        });
    }
}
