package ccams.views;

import ccams.models.ReportData;
import ccams.services.ReportService;
import ccams.utils.IconLoader; // <--- ADDED IMPORT
import ccams.constants.AppColors;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Date;
import java.time.LocalDate;

public class ReportsView extends JPanel {
    private ReportService reportService;
    
    private JTabbedPane tabbedPane;
    
    // Summary tab
    private JLabel lblTotalAppointments, lblPending, lblApproved, lblCompleted, lblCancelled;
    private JLabel lblTotalStudents, lblTotalStaff, lblTotalNurses, lblTotalPsychologists;
    
    // Appointment report tab
    private JTextField txtStartDate, txtEndDate;
    private JButton btnGenerateAppointmentReport;
    private JTable appointmentReportTable;
    private DefaultTableModel appointmentTableModel;
    
    // Student report tab
    private JButton btnGenerateStudentReport;
    private JTable studentReportTable;
    private DefaultTableModel studentTableModel;
    
    // Staff report tab
    private JButton btnGenerateStaffReport;
    private JTable staffReportTable;
    private DefaultTableModel staffTableModel;
    
    public ReportsView() {
        reportService = new ReportService();
        initComponents();
        loadSummary();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        tabbedPane = new JTabbedPane();
        
        // Summary Tab - Using dashboard icon
        addTabWithIcon(tabbedPane, "Summary", IconLoader.loadIcon("dashboard", 16, 16), createSummaryPanel());
        
        // Appointment Report Tab - Using appointments icon
        addTabWithIcon(tabbedPane, "Appointment Report", IconLoader.loadIcon("appointments", 16, 16), createAppointmentReportPanel());
        
        // Student Report Tab - Using students icon
        addTabWithIcon(tabbedPane, "Student Report", IconLoader.loadIcon("students", 16, 16), createStudentReportPanel());
        
        // Staff Report Tab - Using staff icon
        addTabWithIcon(tabbedPane, "Staff Report", IconLoader.loadIcon("staff", 16, 16), createStaffReportPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    // Helper method to add an icon to a JTabbedPane tab
    private void addTabWithIcon(JTabbedPane tabbedPane, String title, Icon icon, Component component) {
        JPanel tabPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        tabPanel.setOpaque(false);
        JLabel iconLabel = new JLabel(icon);
        JLabel titleLabel = new JLabel(" " + title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabPanel.add(iconLabel);
        tabPanel.add(titleLabel);
        tabbedPane.addTab(title, component);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, tabPanel);
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Header
        JLabel headerLabel = new JLabel("Clinic Summary Report");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        panel.add(headerLabel, BorderLayout.NORTH);
        
        // Stats Panel
        JPanel statsPanel = new JPanel(new GridLayout(3, 3, 10, 10));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Statistics"));
        
        // Row 1 - Appointments
        statsPanel.add(createStatCard("Total Appointments", "0", new Color(0, 80, 160)));
        statsPanel.add(createStatCard("Pending", "0", new Color(255, 165, 0)));
        statsPanel.add(createStatCard("Approved", "0", new Color(0, 120, 215)));
        statsPanel.add(createStatCard("Completed", "0", new Color(0, 150, 0)));
        statsPanel.add(createStatCard("Cancelled", "0", new Color(200, 0, 0)));
        statsPanel.add(new JPanel()); // Empty for spacing
        
        // Row 2 - Students & Staff
        statsPanel.add(createStatCard("Total Students", "0", new Color(0, 150, 136)));
        statsPanel.add(createStatCard("Total Staff", "0", new Color(123, 31, 162)));
        statsPanel.add(createStatCard("Nurses", "0", new Color(0, 120, 215)));
        statsPanel.add(createStatCard("Psychologists", "0", new Color(255, 87, 34)));
        statsPanel.add(new JPanel());
        statsPanel.add(new JPanel());
        
        // Store references for updating
        Component[] components = statsPanel.getComponents();
        lblTotalAppointments = (JLabel) ((JPanel) components[0]).getComponent(1);
        lblPending = (JLabel) ((JPanel) components[1]).getComponent(1);
        lblApproved = (JLabel) ((JPanel) components[2]).getComponent(1);
        lblCompleted = (JLabel) ((JPanel) components[3]).getComponent(1);
        lblCancelled = (JLabel) ((JPanel) components[4]).getComponent(1);
        lblTotalStudents = (JLabel) ((JPanel) components[6]).getComponent(1);
        lblTotalStaff = (JLabel) ((JPanel) components[7]).getComponent(1);
        lblTotalNurses = (JLabel) ((JPanel) components[8]).getComponent(1);
        lblTotalPsychologists = (JLabel) ((JPanel) components[9]).getComponent(1);
        
        panel.add(statsPanel, BorderLayout.CENTER);
        
        // Refresh Button - Using refresh icon
        JButton refreshBtn = createStyledButton("Refresh Summary", AppColors.ACCENT, "refresh");
        refreshBtn.addActionListener(e -> loadSummary());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(refreshBtn);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(Color.GRAY);
        card.add(titleLabel, BorderLayout.NORTH);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(valueLabel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createAppointmentReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Filter Panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filter"));
        
        filterPanel.add(new JLabel("Start Date (YYYY-MM-DD):"));
        txtStartDate = new JTextField(12);
        txtStartDate.setText(LocalDate.now().minusDays(30).toString());
        filterPanel.add(txtStartDate);
        
        filterPanel.add(new JLabel("End Date (YYYY-MM-DD):"));
        txtEndDate = new JTextField(12);
        txtEndDate.setText(LocalDate.now().toString());
        filterPanel.add(txtEndDate);
        
        // Using appointments icon for Generate Button
        btnGenerateAppointmentReport = createStyledButton("Generate Report", AppColors.ACCENT, "appointments");
        btnGenerateAppointmentReport.addActionListener(e -> generateAppointmentReport());
        filterPanel.add(btnGenerateAppointmentReport);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"ID", "Student", "Staff", "Date", "Time", "Service", "Status"};
        appointmentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        appointmentReportTable = new JTable(appointmentTableModel);
        appointmentReportTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        appointmentReportTable.setRowHeight(30);
        styleTableHeaders(appointmentReportTable);
        
        JScrollPane scrollPane = new JScrollPane(appointmentReportTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStudentReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // Using students icon for Generate Button
        btnGenerateStudentReport = createStyledButton("Generate Student Report", AppColors.ACCENT, "students");
        btnGenerateStudentReport.addActionListener(e -> generateStudentReport());
        topPanel.add(btnGenerateStudentReport);
        panel.add(topPanel, BorderLayout.NORTH);
        
        String[] columns = {"ID", "Student #", "Name", "Email", "Phone", "Registered"};
        studentTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentReportTable = new JTable(studentTableModel);
        studentReportTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        studentReportTable.setRowHeight(30);
        styleTableHeaders(studentReportTable);
        
        JScrollPane scrollPane = new JScrollPane(studentReportTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStaffReportPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // Using staff icon for Generate Button
        btnGenerateStaffReport = createStyledButton("Generate Staff Report", AppColors.ACCENT, "staff");
        btnGenerateStaffReport.addActionListener(e -> generateStaffReport());
        topPanel.add(btnGenerateStaffReport);
        panel.add(topPanel, BorderLayout.NORTH);
        
        String[] columns = {"ID", "Name", "Profession", "Specialization", "Email", "Phone", "Available"};
        staffTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        staffReportTable = new JTable(staffTableModel);
        staffReportTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        staffReportTable.setRowHeight(30);
        styleTableHeaders(staffReportTable);
        
        JScrollPane scrollPane = new JScrollPane(staffReportTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    // --- FIXED: Helper method to add specific icons to table headers ---
    private void styleTableHeaders(JTable table) {
        table.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Assign specific icons based on the column index
                switch (column) {
                    case 0: setIcon(IconLoader.loadIcon("id", 14, 14)); break;          // ID
                    case 1: setIcon(IconLoader.loadIcon("students", 14, 14)); break;   // Student # / Student
                    case 2: setIcon(IconLoader.loadIcon("staff", 14, 14)); break;      // Staff
                    case 3: setIcon(IconLoader.loadIcon("date", 14, 14)); break;       // Date
                    case 4: setIcon(IconLoader.loadIcon("phone", 14, 14)); break;      // Phone / Time
                    case 5: setIcon(IconLoader.loadIcon("email", 14, 14)); break;      // Email / Service
                    case 6: setIcon(IconLoader.loadIcon("status", 14, 14)); break;     // Status / Available
                    default: setIcon(null);
                }
                
                setHorizontalAlignment(SwingConstants.CENTER);
                return this;
            }
        });
    }
    
    // Updated to accept an icon name and align correctly
    private JButton createStyledButton(String text, Color bgColor, String iconName) {
        JButton button;
        if (iconName != null && !iconName.isEmpty()) {
            button = new JButton(text, IconLoader.loadIcon(iconName, 16, 16));
            button.setHorizontalTextPosition(SwingConstants.RIGHT); // Puts text right of icon
            button.setIconTextGap(8); // Adds a gap
        } else {
            button = new JButton(text);
        }
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(160, 35));
        return button;
    }
    
    private void loadSummary() {
        ReportData report = reportService.generateSummaryReport();
        
        lblTotalAppointments.setText(String.valueOf(report.getTotalAppointments()));
        lblPending.setText(String.valueOf(report.getPendingAppointments()));
        lblApproved.setText(String.valueOf(report.getApprovedAppointments()));
        lblCompleted.setText(String.valueOf(report.getCompletedAppointments()));
        lblCancelled.setText(String.valueOf(report.getCancelledAppointments()));
        lblTotalStudents.setText(String.valueOf(report.getTotalStudents()));
        lblTotalStaff.setText(String.valueOf(report.getTotalStaff()));
        lblTotalNurses.setText(String.valueOf(report.getTotalNurses()));
        lblTotalPsychologists.setText(String.valueOf(report.getTotalPsychologists()));
    }
    
    private void generateAppointmentReport() {
        try {
            String startStr = txtStartDate.getText().trim();
            String endStr = txtEndDate.getText().trim();
            
            if (startStr.isEmpty() || endStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both start and end dates.");
                return;
            }
            
            Date startDate = Date.valueOf(startStr);
            Date endDate = Date.valueOf(endStr);
            
            ReportData report = reportService.generateAppointmentReport(startDate, endDate);
            
            appointmentTableModel.setRowCount(0);
            // For now, show summary stats
            appointmentTableModel.addRow(new Object[]{
                "Total: " + report.getTotalAppointments(),
                "Pending: " + report.getPendingAppointments(),
                "Approved: " + report.getApprovedAppointments(),
                "Completed: " + report.getCompletedAppointments(),
                "Cancelled: " + report.getCancelledAppointments(),
                "", ""
            });
            
            JOptionPane.showMessageDialog(this, 
                "Report Generated!\n\n" +
                "Total Appointments: " + report.getTotalAppointments() + "\n" +
                "Pending: " + report.getPendingAppointments() + "\n" +
                "Approved: " + report.getApprovedAppointments() + "\n" +
                "Completed: " + report.getCompletedAppointments() + "\n" +
                "Cancelled: " + report.getCancelledAppointments(),
                "Report Generated", JOptionPane.INFORMATION_MESSAGE);
                
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage());
        }
    }
    
    private void generateStudentReport() {
        try {
            studentTableModel.setRowCount(0);
            ReportData report = reportService.generateStudentReport();
            
            JOptionPane.showMessageDialog(this, 
                "Student Report Generated!\n\n" +
                "Total Students: " + report.getTotalStudents(),
                "Student Report", JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating student report: " + e.getMessage());
        }
    }
    
    private void generateStaffReport() {
        try {
            staffTableModel.setRowCount(0);
            ReportData report = reportService.generateStaffReport();
            
            JOptionPane.showMessageDialog(this, 
                "Staff Report Generated!\n\n" +
                "Total Staff: " + report.getTotalStaff() + "\n" +
                "Nurses: " + report.getTotalNurses() + "\n" +
                "Psychologists: " + report.getTotalPsychologists(),
                "Staff Report", JOptionPane.INFORMATION_MESSAGE);
                
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating staff report: " + e.getMessage());
        }
    }
}