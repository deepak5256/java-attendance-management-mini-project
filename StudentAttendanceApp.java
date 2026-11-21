import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;

/**
 * ==============================================================================
 * Student Attendance Management System
 * Reference Mini-Project (Java Swing GUI + JDBC Database Connectivity)
 * ==============================================================================
 * 
 * Instructions for Students:
 * 1. Ensure PostgreSQL is running on your computer.
 * 2. Update the DB credentials below (URL, USER, PASS) if needed.
 * 3. Run using 'compile_and_run.bat' or in VS Code with lib/postgresql.jar referenced.
 */
public class StudentAttendanceApp extends JFrame {

    // ==========================================================================
    // 1. DATABASE CONFIGURATION
    // ==========================================================================
    private static final String URL  = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASS = "Admin123";

    /**
     * Establishes a database connection with explicit driver loading.
     * Throws an informative error if the driver JAR is missing from the classpath.
     */
    private static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                "PostgreSQL JDBC Driver (postgresql.jar) not found in Classpath!\n\n" +
                "How to solve this:\n" +
                "1. If running in VS Code: Check that '.vscode/settings.json' includes 'lib/**/*.jar',\n" +
                "   OR right-click 'lib/postgresql.jar' -> 'Add to Referenced Libraries'.\n" +
                "2. OR simply double-click 'compile_and_run.bat' from File Explorer."
            );
        }
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // ==========================================================================
    // 2. GUI COMPONENTS
    // ==========================================================================
    private JTextField txtId;
    private JTextField txtName;
    private JTextField txtCourse;
    private JTextField txtAttended;
    private JTextField txtTotal;
    private JLabel lblCalcPercentage;
    private JLabel lblStatusBadge;

    private JTable table;
    private DefaultTableModel tableModel;

    public StudentAttendanceApp() {
        // Window Setup
        setTitle("Student Attendance Management System — Reference Mini-Project");
        setSize(960, 580);
        setMinimumSize(new Dimension(850, 500));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // 1. Top Header Banner
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Center Content (Form on Left, Table on Right)
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setBorder(new EmptyBorder(5, 15, 5, 15));
        centerPanel.add(createFormPanel(), BorderLayout.WEST);
        centerPanel.add(createTablePanel(), BorderLayout.CENTER);
        add(centerPanel, BorderLayout.CENTER);

        // 3. Bottom Action Buttons
        add(createButtonPanel(), BorderLayout.SOUTH);

        // Auto-create database table if not present, then load data
        ensureTableExists();
        loadRecords();
    }

    // ==========================================================================
    // 3. GUI LAYOUT BUILDERS
    // ==========================================================================
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 41, 59)); // Slate Navy
        header.setBorder(new EmptyBorder(14, 20, 14, 20));

        JLabel title = new JLabel("Student Attendance Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Desktop GUI (Java Swing) & JDBC Relational Database Application");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(new Color(203, 213, 225));

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 2, 2));
        titleBox.setOpaque(false);
        titleBox.add(title);
        titleBox.add(subtitle);

        header.add(titleBox, BorderLayout.WEST);
        return header;
    }

    private JPanel createFormPanel() {
        JPanel container = new JPanel(new BorderLayout());
        container.setPreferredSize(new Dimension(360, 0));
        container.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                " Student Attendance Details ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                new Color(30, 41, 59)
        ));

        JPanel form = new JPanel(new GridLayout(7, 2, 8, 10));
        form.setBorder(new EmptyBorder(15, 12, 15, 12));

        txtId       = new JTextField(12);
        txtName     = new JTextField(12);
        txtCourse   = new JTextField("BCA301", 12);
        txtAttended = new JTextField("40", 12);
        txtTotal    = new JTextField("50", 12);

        lblCalcPercentage = new JLabel("80.00 %");
        lblCalcPercentage.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCalcPercentage.setForeground(new Color(16, 125, 65));

        lblStatusBadge = new JLabel("Eligible");
        lblStatusBadge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStatusBadge.setForeground(new Color(16, 125, 65));

        // Real-time calculation on key typed
        KeyAdapter calcAdapter = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateCalculatedFields();
            }
        };
        txtAttended.addKeyListener(calcAdapter);
        txtTotal.addKeyListener(calcAdapter);

        form.add(new JLabel("Attendance ID (PK):"));
        form.add(txtId);
        form.add(new JLabel("Student Name:"));
        form.add(txtName);
        form.add(new JLabel("Course Code:"));
        form.add(txtCourse);
        form.add(new JLabel("Attended Classes:"));
        form.add(txtAttended);
        form.add(new JLabel("Total Classes:"));
        form.add(txtTotal);
        form.add(new JLabel("Attendance %:"));
        form.add(lblCalcPercentage);
        form.add(new JLabel("Eligibility Status:"));
        form.add(lblStatusBadge);

        container.add(form, BorderLayout.NORTH);
        return container;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
                " Database Records (Click row to select) ",
                TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Segoe UI", Font.BOLD, 13),
                new Color(30, 41, 59)
        ));

        String[] columns = {"ID", "Student Name", "Course", "Attended", "Total", "Percentage", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false; // Cells are read-only
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(26);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(241, 245, 249));

        // Center align numeric columns
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(2).setCellRenderer(center);
        table.getColumnModel().getColumn(3).setCellRenderer(center);
        table.getColumnModel().getColumn(4).setCellRenderer(center);
        table.getColumnModel().getColumn(5).setCellRenderer(center);

        // Click row to populate form fields
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int r = table.getSelectedRow();
                txtId.setText(table.getValueAt(r, 0).toString());
                txtName.setText(table.getValueAt(r, 1).toString());
                txtCourse.setText(table.getValueAt(r, 2).toString());
                txtAttended.setText(table.getValueAt(r, 3).toString());
                txtTotal.setText(table.getValueAt(r, 4).toString());
                updateCalculatedFields();
            }
        });

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        btnPanel.setBackground(new Color(248, 250, 252));
        btnPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(226, 232, 240)));

        // Outlined buttons with high-contrast, perfectly visible text
        JButton btnInsert  = createButton("Insert / Save",  new Color(16, 125, 65));  // Dark Green
        JButton btnSearch  = createButton("Search by ID",  new Color(26, 86, 219));  // Dark Blue
        JButton btnUpdate  = createButton("Update Record", new Color(180, 83, 9));   // Dark Amber
        JButton btnDelete  = createButton("Delete Record", new Color(195, 34, 34));  // Dark Red
        JButton btnClear   = createButton("Clear Form",    new Color(71, 85, 105));  // Slate
        JButton btnRefresh = createButton("Reload Table",  new Color(79, 70, 229));  // Indigo

        btnInsert.addActionListener(e -> insertRecord());
        btnSearch.addActionListener(e -> searchRecord());
        btnUpdate.addActionListener(e -> updateRecord());
        btnDelete.addActionListener(e -> deleteRecord());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> loadRecords());

        btnPanel.add(btnInsert);
        btnPanel.add(btnSearch);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);
        btnPanel.add(btnRefresh);

        return btnPanel;
    }

    private JButton createButton(String text, Color accentColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(accentColor); // Crisp, high-contrast colored text
        btn.setBackground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor, 1),
                BorderFactory.createEmptyBorder(7, 14, 7, 14)
        ));
        return btn;
    }

    // ==========================================================================
    // 4. DATABASE CRUD OPERATIONS (PreparedStatement)
    // ==========================================================================

    private void ensureTableExists() {
        String sql = "CREATE TABLE IF NOT EXISTS student_attendance (" +
                     "attendance_id INT PRIMARY KEY, " +
                     "student_name VARCHAR(100) NOT NULL, " +
                     "course_code VARCHAR(20) NOT NULL, " +
                     "attended_classes INT NOT NULL, " +
                     "total_classes INT NOT NULL DEFAULT 50, " +
                     "status VARCHAR(30) NOT NULL)";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.err.println("Database Notice: " + e.getMessage());
        }
    }

    // INSERT (CREATE)
    private void insertRecord() {
        String sql = "INSERT INTO student_attendance " +
                     "(attendance_id, student_name, course_code, attended_classes, total_classes, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            if (txtId.getText().trim().isEmpty() || txtName.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Attendance ID and Student Name cannot be empty!", "Input Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int id = Integer.parseInt(txtId.getText().trim());
            String name = txtName.getText().trim();
            String course = txtCourse.getText().trim();
            int attended = Integer.parseInt(txtAttended.getText().trim());
            int total = Integer.parseInt(txtTotal.getText().trim());

            if (attended < 0 || total <= 0 || attended > total) {
                JOptionPane.showMessageDialog(this, "Attended classes must be between 0 and Total Classes!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String status = ((double) attended / total) * 100.0 >= 75.0 ? "Eligible" : "Attendance Shortage";

            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, course);
            pstmt.setInt(4, attended);
            pstmt.setInt(5, total);
            pstmt.setString(6, status);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Record saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            clearForm();
            loadRecords();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID, Attended Classes, and Total Classes must be valid numbers!", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "SQL Exception", JOptionPane.ERROR_MESSAGE);
        }
    }

    // SEARCH (READ)
    private void searchRecord() {
        String sql = "SELECT * FROM student_attendance WHERE attendance_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String idStr = txtId.getText().trim();
            if (idStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an Attendance ID to search.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int id = Integer.parseInt(idStr);
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    txtName.setText(rs.getString("student_name"));
                    txtCourse.setText(rs.getString("course_code"));
                    txtAttended.setText(String.valueOf(rs.getInt("attended_classes")));
                    txtTotal.setText(String.valueOf(rs.getInt("total_classes")));
                    updateCalculatedFields();

                    // Highlight row in table
                    for (int i = 0; i < table.getRowCount(); i++) {
                        if ((int) table.getValueAt(i, 0) == id) {
                            table.setRowSelectionInterval(i, i);
                            table.scrollRectToVisible(table.getCellRect(i, 0, true));
                            break;
                        }
                    }
                    JOptionPane.showMessageDialog(this, "Record found for ID: " + id, "Found", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No record found for ID: " + id, "Not Found", JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Attendance ID must be a number!", "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Search Error: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // UPDATE
    private void updateRecord() {
        String sql = "UPDATE student_attendance SET " +
                     "student_name = ?, course_code = ?, attended_classes = ?, total_classes = ?, status = ? " +
                     "WHERE attendance_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            int id = Integer.parseInt(txtId.getText().trim());
            String name = txtName.getText().trim();
            String course = txtCourse.getText().trim();
            int attended = Integer.parseInt(txtAttended.getText().trim());
            int total = Integer.parseInt(txtTotal.getText().trim());

            if (attended < 0 || total <= 0 || attended > total) {
                JOptionPane.showMessageDialog(this, "Attended classes must be between 0 and Total Classes!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String status = ((double) attended / total) * 100.0 >= 75.0 ? "Eligible" : "Attendance Shortage";

            pstmt.setString(1, name);
            pstmt.setString(2, course);
            pstmt.setInt(3, attended);
            pstmt.setInt(4, total);
            pstmt.setString(5, status);
            pstmt.setInt(6, id);

            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(this, "Record updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadRecords();
            } else {
                JOptionPane.showMessageDialog(this, "No record found with ID " + id + " to update.", "Not Found", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Update Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // DELETE
    private void deleteRecord() {
        String sql = "DELETE FROM student_attendance WHERE attendance_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String idStr = txtId.getText().trim();
            if (idStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter or select an Attendance ID to delete.", "Input Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int id = Integer.parseInt(idStr);
            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to delete record ID " + id + "?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;

            pstmt.setInt(1, id);
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                JOptionPane.showMessageDialog(this, "Record deleted successfully!", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadRecords();
            } else {
                JOptionPane.showMessageDialog(this, "No record found to delete for ID: " + id, "Not Found", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Delete Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // LOAD ALL RECORDS INTO JTABLE
    private void loadRecords() {
        tableModel.setRowCount(0);
        String sql = "SELECT attendance_id, student_name, course_code, attended_classes, total_classes, status " +
                     "FROM student_attendance ORDER BY attendance_id ASC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                int attended = rs.getInt("attended_classes");
                int total = rs.getInt("total_classes");
                double pct = total > 0 ? ((double) attended / total) * 100.0 : 0.0;

                tableModel.addRow(new Object[]{
                    rs.getInt("attendance_id"),
                    rs.getString("student_name"),
                    rs.getString("course_code"),
                    attended,
                    total,
                    String.format("%.2f %%", pct),
                    rs.getString("status")
                });
            }
        } catch (SQLException e) {
            System.err.println("Failed to load records: " + e.getMessage());
        }
    }

    // ==========================================================================
    // 5. HELPER METHODS
    // ==========================================================================
    private void updateCalculatedFields() {
        try {
            int attended = Integer.parseInt(txtAttended.getText().trim());
            int total = Integer.parseInt(txtTotal.getText().trim());
            if (total > 0 && attended >= 0 && attended <= total) {
                double pct = ((double) attended / total) * 100.0;
                lblCalcPercentage.setText(String.format("%.2f %%", pct));
                if (pct >= 75.0) {
                    lblStatusBadge.setText("Eligible");
                    lblStatusBadge.setForeground(new Color(16, 125, 65)); // Green
                    lblCalcPercentage.setForeground(new Color(16, 125, 65));
                } else {
                    lblStatusBadge.setText("Attendance Shortage");
                    lblStatusBadge.setForeground(new Color(195, 34, 34)); // Red
                    lblCalcPercentage.setForeground(new Color(195, 34, 34));
                }
            }
        } catch (NumberFormatException ignored) {}
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        txtCourse.setText("BCA301");
        txtAttended.setText("40");
        txtTotal.setText("50");
        updateCalculatedFields();
        table.clearSelection();
    }

    // ==========================================================================
    // 6. MAIN METHOD
    // ==========================================================================
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            new StudentAttendanceApp().setVisible(true);
        });
    }
}
