package ui;

import config.DatabaseConfig;
import dao.StudentAttendanceDAO;
import model.StudentAttendance;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.List;

/**
 * Modern Swing GUI for Student Attendance Management System.
 * Combines form controls, CRUD action triggers, live JTable, and real-time statistics.
 */
public class StudentAttendanceUI extends JFrame {

    // DAO Layer
    private final StudentAttendanceDAO dao = new StudentAttendanceDAO();

    // Form Components
    private JTextField txtId;
    private JTextField txtName;
    private JComboBox<String> cmbCourse;
    private JTextField txtAttended;
    private JTextField txtTotal;
    private JLabel lblCalcPercentage;
    private JLabel lblStatusBadge;

    // Table & Model
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField txtSearch;

    // Statistics Bar
    private JLabel lblStatTotal;
    private JLabel lblStatEligible;
    private JLabel lblStatShortage;
    private JLabel lblDbStatus;

    public StudentAttendanceUI() {
        setTitle("Student Attendance Management System — Java Swing & JDBC Reference");
        setSize(1050, 680);
        setMinimumSize(new Dimension(900, 580));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initUI();
        loadAllRecords();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 247, 250));

        // 1. Top Header Banner
        add(createHeaderPanel(), BorderLayout.NORTH);

        // 2. Center Split (Left: Input Form, Right: Data Table)
        JPanel mainContent = new JPanel(new BorderLayout(15, 15));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(0, 15, 10, 15));

        mainContent.add(createFormPanel(), BorderLayout.WEST);
        mainContent.add(createTablePanel(), BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        // 3. Bottom Summary / Status Bar
        add(createStatusBar(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(30, 41, 59)); // Slate Navy
        header.setBorder(new EmptyBorder(16, 20, 16, 20));

        JLabel title = new JLabel("Student Attendance Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(Color.WHITE);

        JLabel subtitle = new JLabel("Desktop GUI (Java Swing) & JDBC Relational Database Application");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(new Color(203, 213, 225));

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 2, 2));
        titleBox.setOpaque(false);
        titleBox.add(title);
        titleBox.add(subtitle);

        header.add(titleBox, BorderLayout.WEST);
        return header;
    }

    private JPanel createFormPanel() {
        JPanel container = new JPanel(new BorderLayout(10, 10));
        container.setPreferredSize(new Dimension(380, 0));
        container.setBackground(Color.WHITE);
        container.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Form Title
        JLabel formTitle = new JLabel("Manage Attendance Record");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(new Color(15, 23, 42));
        container.add(formTitle, BorderLayout.NORTH);

        // Input Fields Panel
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 4, 6, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtId = new JTextField(12);
        txtName = new JTextField(12);
        cmbCourse = new JComboBox<>(new String[]{"BCA301 - Java Programming", "BCA302 - Database Systems", "BCA303 - Operating Systems", "BCA304 - Computer Networks", "BCA305 - Software Engineering"});
        cmbCourse.setEditable(true);

        txtAttended = new JTextField("40", 12);
        txtTotal = new JTextField("50", 12);

        lblCalcPercentage = new JLabel("80.00 %");
        lblCalcPercentage.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblCalcPercentage.setForeground(new Color(16, 185, 129));

        lblStatusBadge = new JLabel("Eligible");
        lblStatusBadge.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblStatusBadge.setForeground(new Color(16, 185, 129));

        // Dynamic calculation listeners
        KeyAdapter calcAdapter = new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                updateCalculatedFields();
            }
        };
        txtAttended.addKeyListener(calcAdapter);
        txtTotal.addKeyListener(calcAdapter);

        int row = 0;
        addFormField(fieldsPanel, gbc, row++, "Attendance ID (PK):", txtId);
        addFormField(fieldsPanel, gbc, row++, "Student Name:", txtName);
        addFormField(fieldsPanel, gbc, row++, "Course Code:", cmbCourse);
        addFormField(fieldsPanel, gbc, row++, "Attended Classes:", txtAttended);
        addFormField(fieldsPanel, gbc, row++, "Total Classes:", txtTotal);
        addFormField(fieldsPanel, gbc, row++, "Attendance %:", lblCalcPercentage);
        addFormField(fieldsPanel, gbc, row++, "Eligibility Status:", lblStatusBadge);

        container.add(fieldsPanel, BorderLayout.CENTER);

        // Action Buttons Panel
        JPanel btnPanel = new JPanel(new GridLayout(3, 2, 8, 8));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton btnInsert = createStyledButton("Save / Insert", new Color(16, 185, 129)); // Green
        JButton btnSearch = createStyledButton("Search by ID", new Color(59, 130, 246));  // Blue
        JButton btnUpdate = createStyledButton("Update Record", new Color(245, 158, 11)); // Amber
        JButton btnDelete = createStyledButton("Delete Record", new Color(239, 68, 68));  // Red
        JButton btnClear  = createStyledButton("Clear Form", new Color(100, 116, 139));   // Slate
        JButton btnRefresh= createStyledButton("Reload Table", new Color(79, 70, 229));  // Indigo

        btnInsert.addActionListener(e -> onInsert());
        btnSearch.addActionListener(e -> onSearch());
        btnUpdate.addActionListener(e -> onUpdate());
        btnDelete.addActionListener(e -> onDelete());
        btnClear.addActionListener(e -> clearForm());
        btnRefresh.addActionListener(e -> loadAllRecords());

        btnPanel.add(btnInsert);
        btnPanel.add(btnSearch);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnClear);
        btnPanel.add(btnRefresh);

        container.add(btnPanel, BorderLayout.SOUTH);
        return container;
    }

    private void addFormField(JPanel p, GridBagConstraints gbc, int row, String labelText, Component comp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        p.add(comp, gbc);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));

        // Search Bar at Top of Table
        JPanel searchBar = new JPanel(new BorderLayout(8, 8));
        searchBar.setOpaque(false);
        JLabel searchLbl = new JLabel("Live Search (Name / Course):");
        searchLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));

        txtSearch = new JTextField();
        txtSearch.putClientProperty("JTextField.placeholderText", "Type student name or course code...");
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                onFilterSearch(txtSearch.getText().trim());
            }
        });

        searchBar.add(searchLbl, BorderLayout.WEST);
        searchBar.add(txtSearch, BorderLayout.CENTER);
        panel.add(searchBar, BorderLayout.NORTH);

        // JTable Configuration
        String[] columns = {"ID", "Student Name", "Course", "Attended", "Total", "Percentage", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Read-only cells
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.getTableHeader().setBackground(new Color(241, 245, 249));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Center align numeric columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        // Color renderer for status
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int r, int c) {
                Component comp = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, r, c);
                if ("Attendance Shortage".equals(value)) {
                    setForeground(new Color(220, 38, 38)); // Red
                    setFont(getFont().deriveFont(Font.BOLD));
                } else {
                    setForeground(new Color(16, 185, 129)); // Green
                    setFont(getFont().deriveFont(Font.BOLD));
                }
                return comp;
            }
        });

        // Click row to populate form
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                populateFormFromSelectedRow();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240)));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatusBar() {
        JPanel status = new JPanel(new BorderLayout(15, 5));
        status.setBackground(new Color(241, 245, 249));
        status.setBorder(new EmptyBorder(8, 20, 8, 20));

        // Stats Box
        JPanel statsBox = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        statsBox.setOpaque(false);

        lblStatTotal = new JLabel("Total Students: 0");
        lblStatTotal.setFont(new Font("Segoe UI", Font.BOLD, 12));

        lblStatEligible = new JLabel("Eligible: 0");
        lblStatEligible.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatEligible.setForeground(new Color(16, 185, 129));

        lblStatShortage = new JLabel("Shortage (<75%): 0");
        lblStatShortage.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStatShortage.setForeground(new Color(220, 38, 38));

        statsBox.add(lblStatTotal);
        statsBox.add(new JSeparator(SwingConstants.VERTICAL));
        statsBox.add(lblStatEligible);
        statsBox.add(lblStatShortage);

        // Connection Label
        lblDbStatus = new JLabel("DB: PostgreSQL (localhost:5432) | Ready");
        lblDbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblDbStatus.setForeground(new Color(100, 116, 139));

        status.add(statsBox, BorderLayout.WEST);
        status.add(lblDbStatus, BorderLayout.EAST);
        return status;
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(7, 10, 7, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // -------------------------------------------------------------
    // CRUD EVENT HANDLERS
    // -------------------------------------------------------------

    private void onInsert() {
        try {
            StudentAttendance record = extractFormRecord();
            if (record == null) return;

            if (dao.existsById(record.getAttendanceId())) {
                JOptionPane.showMessageDialog(this,
                        "Record with Attendance ID " + record.getAttendanceId() + " already exists!\nUse 'Update Record' instead.",
                        "Duplicate ID Detected", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean ok = dao.insert(record);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Attendance record successfully saved!", "Success", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadAllRecords();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "SQL Exception", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onSearch() {
        String idText = txtId.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an Attendance ID to search.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(idText);
            StudentAttendance rec = dao.findById(id);
            if (rec != null) {
                populateForm(rec);
                selectRowByAttendanceId(rec.getAttendanceId());
                JOptionPane.showMessageDialog(this, "Record found for ID: " + id, "Found", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "No record exists with Attendance ID: " + id, "Not Found", JOptionPane.WARNING_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Attendance ID must be a valid integer number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Search failed: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onUpdate() {
        try {
            StudentAttendance record = extractFormRecord();
            if (record == null) return;

            if (!dao.existsById(record.getAttendanceId())) {
                JOptionPane.showMessageDialog(this,
                        "No record exists with ID " + record.getAttendanceId() + " to update.",
                        "Record Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to update record ID " + record.getAttendanceId() + "?",
                    "Confirm Update", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            boolean ok = dao.update(record);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Record updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadAllRecords();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Update failed: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onDelete() {
        String idText = txtId.getText().trim();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select or enter the Attendance ID to delete.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int id = Integer.parseInt(idText);
            if (!dao.existsById(id)) {
                JOptionPane.showMessageDialog(this, "No record found with ID " + id + " to delete.", "Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to PERMANENTLY delete record ID " + id + "?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm != JOptionPane.YES_OPTION) return;

            boolean ok = dao.delete(id);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Record ID " + id + " deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                loadAllRecords();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Attendance ID must be a valid number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Delete failed: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onFilterSearch(String query) {
        if (query.isEmpty()) {
            loadAllRecords();
            return;
        }
        try {
            List<StudentAttendance> list = dao.search(query);
            renderTableData(list);
        } catch (SQLException ex) {
            System.err.println("Search filter error: " + ex.getMessage());
        }
    }

    // -------------------------------------------------------------
    // HELPERS & DATA BINDING
    // -------------------------------------------------------------

    private StudentAttendance extractFormRecord() {
        String idText = txtId.getText().trim();
        String name = txtName.getText().trim();
        String course = (String) cmbCourse.getSelectedItem();
        String attendedText = txtAttended.getText().trim();
        String totalText = txtTotal.getText().trim();

        if (idText.isEmpty() || name.isEmpty() || attendedText.isEmpty() || totalText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all mandatory fields!", "Validation Warning", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        int id, attended, total;
        try {
            id = Integer.parseInt(idText);
            if (id <= 0) {
                JOptionPane.showMessageDialog(this, "Attendance ID must be a positive integer.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return null;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Attendance ID must be a valid integer number.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        try {
            attended = Integer.parseInt(attendedText);
            total = Integer.parseInt(totalText);
            if (attended < 0 || total <= 0 || attended > total) {
                JOptionPane.showMessageDialog(this, "Attended classes must be between 0 and Total Classes (" + total + ").", "Invalid Value", JOptionPane.WARNING_MESSAGE);
                return null;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Classes Attended and Total Classes must be valid numbers.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }

        String courseCodeOnly = course != null ? course.split(" - ")[0] : "BCA301";
        String status = StudentAttendance.determineStatus(attended, total);

        return new StudentAttendance(id, name, courseCodeOnly, attended, total, status);
    }

    private void updateCalculatedFields() {
        try {
            int attended = Integer.parseInt(txtAttended.getText().trim());
            int total = Integer.parseInt(txtTotal.getText().trim());
            if (total > 0 && attended >= 0 && attended <= total) {
                double pct = ((double) attended / total) * 100.0;
                lblCalcPercentage.setText(String.format("%.2f %%", pct));
                String st = StudentAttendance.determineStatus(attended, total);
                lblStatusBadge.setText(st);
                if ("Eligible".equals(st)) {
                    lblStatusBadge.setForeground(new Color(16, 185, 129));
                    lblCalcPercentage.setForeground(new Color(16, 185, 129));
                } else {
                    lblStatusBadge.setForeground(new Color(220, 38, 38));
                    lblCalcPercentage.setForeground(new Color(220, 38, 38));
                }
            }
        } catch (NumberFormatException ignored) {}
    }

    private void loadAllRecords() {
        try {
            List<StudentAttendance> list = dao.findAll();
            renderTableData(list);
        } catch (SQLException ex) {
            lblDbStatus.setText("DB: Connection Failed (" + ex.getMessage() + ")");
            lblDbStatus.setForeground(Color.RED);
        }
    }

    private void renderTableData(List<StudentAttendance> list) {
        tableModel.setRowCount(0);
        int eligible = 0;
        int shortage = 0;

        for (StudentAttendance s : list) {
            tableModel.addRow(new Object[]{
                    s.getAttendanceId(),
                    s.getStudentName(),
                    s.getCourseCode(),
                    s.getAttendedClasses(),
                    s.getTotalClasses(),
                    String.format("%.2f%%", s.getPercentage()),
                    s.getStatus()
            });

            if ("Eligible".equalsIgnoreCase(s.getStatus())) eligible++;
            else shortage++;
        }

        lblStatTotal.setText("Total Students: " + list.size());
        lblStatEligible.setText("Eligible: " + eligible);
        lblStatShortage.setText("Shortage (<75%): " + shortage);
    }

    private void populateFormFromSelectedRow() {
        int r = table.getSelectedRow();
        if (r == -1) return;

        txtId.setText(table.getValueAt(r, 0).toString());
        txtName.setText(table.getValueAt(r, 1).toString());

        String course = table.getValueAt(r, 2).toString();
        for (int i = 0; i < cmbCourse.getItemCount(); i++) {
            if (cmbCourse.getItemAt(i).startsWith(course)) {
                cmbCourse.setSelectedIndex(i);
                break;
            }
        }

        txtAttended.setText(table.getValueAt(r, 3).toString());
        txtTotal.setText(table.getValueAt(r, 4).toString());
        updateCalculatedFields();
    }

    private void populateForm(StudentAttendance s) {
        txtId.setText(String.valueOf(s.getAttendanceId()));
        txtName.setText(s.getStudentName());
        for (int i = 0; i < cmbCourse.getItemCount(); i++) {
            if (cmbCourse.getItemAt(i).startsWith(s.getCourseCode())) {
                cmbCourse.setSelectedIndex(i);
                break;
            }
        }
        txtAttended.setText(String.valueOf(s.getAttendedClasses()));
        txtTotal.setText(String.valueOf(s.getTotalClasses()));
        updateCalculatedFields();
    }

    private void selectRowByAttendanceId(int id) {
        for (int i = 0; i < table.getRowCount(); i++) {
            if ((int) table.getValueAt(i, 0) == id) {
                table.setRowSelectionInterval(i, i);
                table.scrollRectToVisible(table.getCellRect(i, 0, true));
                break;
            }
        }
    }

    private void clearForm() {
        txtId.setText("");
        txtName.setText("");
        cmbCourse.setSelectedIndex(0);
        txtAttended.setText("40");
        txtTotal.setText("50");
        updateCalculatedFields();
        table.clearSelection();
    }
}
