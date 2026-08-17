package ccams.views;

import ccams.models.ReportData;
import ccams.services.ReportService;
import ccams.constants.AppColors;
import ccams.utils.IconLoader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class JasperReportView extends JPanel {
    private ReportService reportService;
    private JTable reportTable;
    private DefaultTableModel tableModel;
    private JTextField txtStartDate, txtEndDate;
    private JComboBox<String> cmbReportType;
    private JButton btnGenerate, btnPrint, btnExportPDF, btnRefresh;
    
    public JasperReportView() {
        reportService = new ReportService();
        initComponents();
        loadReportTypes();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(AppColors.BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top Panel - Controls
        JPanel topPanel = createControlPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Center Panel - Table
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        // Bottom Panel - Actions
        JPanel bottomPanel = createActionPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.CARD_BORDER),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Report Type
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("📊 Report Type:"), gbc);
        gbc.gridx = 1;
        cmbReportType = new JComboBox<>();
        cmbReportType.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbReportType.setPreferredSize(new Dimension(200, 35));
        cmbReportType.addActionListener(e -> updateDateFields());
        panel.add(cmbReportType, gbc);
        
        // Start Date
        gbc.gridx = 2;
        panel.add(new JLabel("📅 Start Date:"), gbc);
        gbc.gridx = 3;
        txtStartDate = new JTextField(12);
        txtStartDate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtStartDate.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        txtStartDate.setEnabled(false);
        panel.add(txtStartDate, gbc);
        
        // End Date
        gbc.gridx = 4;
        panel.add(new JLabel("📅 End Date:"), gbc);
        gbc.gridx = 5;
        txtEndDate = new JTextField(12);
        txtEndDate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtEndDate.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        txtEndDate.setEnabled(false);
        panel.add(txtEndDate, gbc);
        
        // Generate Button
        gbc.gridx = 6;
        btnGenerate = createStyledButton("Generate Report", AppColors.ACCENT, "generate");
        btnGenerate.addActionListener(e -> generateReport());
        panel.add(btnGenerate, gbc);
        
        return panel;
    }
    
    private void loadReportTypes() {
        cmbReportType.removeAllItems();
        cmbReportType.addItem("Summary Report");
        cmbReportType.addItem("Appointment Summary Report");
        cmbReportType.addItem("Weekly Report");
        cmbReportType.addItem("Monthly Report");
        cmbReportType.addItem("Annual Report");
        cmbReportType.addItem("Student Report");
        cmbReportType.addItem("Staff Report");
    }
    
    private void updateDateFields() {
        String selected = (String) cmbReportType.getSelectedItem();
        boolean enableDates = selected != null && 
            (selected.equals("Appointment Summary Report") ||
             selected.equals("Weekly Report") ||
             selected.equals("Monthly Report") ||
             selected.equals("Annual Report"));
        
        txtStartDate.setEnabled(enableDates);
        txtEndDate.setEnabled(enableDates);
        
        if (enableDates) {
            java.time.LocalDate now = java.time.LocalDate.now();
            txtStartDate.setText(now.minusDays(30).toString());
            txtEndDate.setText(now.toString());
        } else {
            txtStartDate.setText("");
            txtEndDate.setText("");
        }
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        String[] columns = {"ID", "Report Type", "Generated At", "Actions"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        reportTable = new JTable(tableModel);
        reportTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        reportTable.setRowHeight(35);
        reportTable.setSelectionBackground(new Color(220, 235, 250));
        reportTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        reportTable.getTableHeader().setBackground(AppColors.BG_LIGHT);
        reportTable.getTableHeader().setPreferredSize(new Dimension(0, 35));
        
        reportTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewReport();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(reportTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppColors.CARD_BORDER, 1));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, AppColors.CARD_BORDER));
        
        btnPrint = createStyledButton("🖨️ Print", new Color(0, 150, 0), "print");
        btnPrint.addActionListener(e -> printReport());
        panel.add(btnPrint);
        
        btnExportPDF = createStyledButton("📄 Export PDF", new Color(0, 120, 215), "pdf");
        btnExportPDF.addActionListener(e -> exportPDF());
        panel.add(btnExportPDF);
        
        btnRefresh = createStyledButton("🔄 Refresh", new Color(100, 100, 100), "refresh");
        btnRefresh.addActionListener(e -> loadReports());
        panel.add(btnRefresh);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color bgColor, String iconName) {
        ImageIcon icon = IconLoader.loadIcon(iconName, 16, 16);
        JButton button;
        if (icon != null) {
            button = new JButton(text, icon);
            button.setHorizontalTextPosition(SwingConstants.RIGHT);
            button.setIconTextGap(8);
        } else {
            button = new JButton(text);
        }
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(150, 38));
        return button;
    }
    
    // ============================================================
    // FIXED: generateReport() - Uses ReportData (not ReportModel)
    // ============================================================
    private void generateReport() {
        String reportType = (String) cmbReportType.getSelectedItem();
        if (reportType == null) {
            JOptionPane.showMessageDialog(this, "Please select a report type.");
            return;
        }
        
        try {
            ReportData report = null;
            
            switch (reportType) {
                case "Summary Report":
                    report = reportService.generateSummaryReport();
                    break;
                case "Appointment Summary Report":
                    String startStr = txtStartDate.getText().trim();
                    String endStr = txtEndDate.getText().trim();
                    if (startStr.isEmpty() || endStr.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Please enter both start and end dates.");
                        return;
                    }
                    Date startDate = Date.valueOf(startStr);
                    Date endDate = Date.valueOf(endStr);
                    report = reportService.generateAppointmentSummaryReport(startDate, endDate);
                    break;
                case "Weekly Report":
                    report = reportService.generateWeeklyReport();
                    break;
                case "Monthly Report":
                    report = reportService.generateMonthlyReport();
                    break;
                case "Annual Report":
                    report = reportService.generateAnnualReport();
                    break;
                case "Student Report":
                    report = reportService.generateStudentReport();
                    break;
                case "Staff Report":
                    report = reportService.generateStaffReport();
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Unknown report type.");
                    return;
            }
            
            if (report != null) {
                addReportToTable(report);
                showReportDialog(report);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void addReportToTable(ReportData report) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        tableModel.addRow(new Object[]{
            tableModel.getRowCount() + 1,
            report.getReportType(),
            sdf.format(new java.util.Date()),
            "📊 View"
        });
    }
    
    private void showReportDialog(ReportData report) {
        StringBuilder sb = new StringBuilder();
        sb.append("📊 ").append(report.getReportType()).append("\n\n");
        sb.append("=".repeat(50)).append("\n\n");
        
        if (report.getStartDate() != null && report.getEndDate() != null) {
            sb.append("Period: ").append(report.getStartDate()).append(" to ").append(report.getEndDate()).append("\n\n");
        }
        
        sb.append("📈 STATISTICS:\n");
        sb.append("-".repeat(40)).append("\n");
        
        // Show appointment stats
        if (report.getTotalAppointments() > 0 || 
            report.getReportType().contains("Appointment") || 
            report.getReportType().contains("Summary") || 
            report.getReportType().contains("Weekly") || 
            report.getReportType().contains("Monthly") || 
            report.getReportType().contains("Annual")) {
            
            sb.append("Total Appointments: ").append(report.getTotalAppointments()).append("\n");
            sb.append("  ├─ Pending: ").append(report.getPendingAppointments()).append("\n");
            sb.append("  ├─ Approved: ").append(report.getApprovedAppointments()).append("\n");
            sb.append("  ├─ Completed: ").append(report.getCompletedAppointments()).append("\n");
            sb.append("  └─ Cancelled: ").append(report.getCancelledAppointments()).append("\n\n");
        }
        
        // Show student stats
        if (report.getTotalStudents() > 0) {
            sb.append("Total Students: ").append(report.getTotalStudents()).append("\n");
        }
        
        // Show staff stats
        if (report.getTotalStaff() > 0) {
            sb.append("Total Staff: ").append(report.getTotalStaff()).append("\n");
            sb.append("  ├─ Nurses: ").append(report.getTotalNurses()).append("\n");
            sb.append("  └─ Psychologists: ").append(report.getTotalPsychologists()).append("\n");
        }
        
        sb.append("\n").append("=".repeat(50)).append("\n");
        sb.append("Generated by: Admin\n");
        sb.append("Generated at: ").append(new java.util.Date());
        
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        textArea.setEditable(false);
        textArea.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 400));
        
        JOptionPane.showMessageDialog(this, scrollPane, "📊 Report Generated", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void viewReport() {
        int selectedRow = reportTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a report to view.");
            return;
        }
        generateReport();
    }
    
    private void printReport() {
        int selectedRow = reportTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a report to print.");
            return;
        }
        JOptionPane.showMessageDialog(this, "🖨️ Printing report...\nThis would open the print dialog.");
    }
    
    private void exportPDF() {
        int selectedRow = reportTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a report to export.");
            return;
        }
        JOptionPane.showMessageDialog(this, "📄 Exporting to PDF...\nThis would generate a PDF file.");
    }
    
    private void loadReports() {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        tableModel.addRow(new Object[]{1, "Summary Report", sdf.format(new java.util.Date()), "📊 View"});
        tableModel.addRow(new Object[]{2, "Weekly Report", sdf.format(new java.util.Date()), "📊 View"});
        tableModel.addRow(new Object[]{3, "Monthly Report", sdf.format(new java.util.Date()), "📊 View"});
    }
}