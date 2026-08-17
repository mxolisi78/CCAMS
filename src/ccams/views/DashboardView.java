package ccams.views;

import ccams.auth.LoginForm;
import ccams.models.User;
import ccams.models.ReportData;
import ccams.services.ReportService;
import ccams.constants.AppColors;
import ccams.dao.AppointmentDAO;
import ccams.utils.IconLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import ccams.models.Appointment;

// JFreeChart imports
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.renderer.category.BarRenderer;

public class DashboardView extends JFrame {
    private User currentUser;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private ReportService reportService;
    private AppointmentDAO appointmentDAO;
    private Map<String, JButton> navButtons = new HashMap<>();
    private JLabel lblCurrentPage;
    private JPanel dashboardPanel;
    
    // Stats labels
    private JLabel lblTotalAppointments, lblPending, lblApproved, lblCompleted, lblCancelled;
    private JLabel lblTotalStudents, lblTotalStaff, lblTotalNurses, lblTotalPsychologists;
    
    public DashboardView(User user) {
        this.currentUser = user;
        this.reportService = new ReportService();
        this.appointmentDAO = new AppointmentDAO();
        setTitle("CCAMS - Campus Clinic Appointment Management System");
        setSize(1400, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        initComponents();
        refreshDashboard();
        showPage("Dashboard");
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(0, 0));
        
        // Left Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);
        
        // Main Content Area
        JPanel mainArea = new JPanel(new BorderLayout());
        mainArea.setBackground(AppColors.BG_WHITE);
        
        // Top Bar
        JPanel topBar = createTopBar();
        mainArea.add(topBar, BorderLayout.NORTH);
        
        // Content Panel with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(AppColors.BG_LIGHT);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create all pages and add them to card layout
        dashboardPanel = createDashboardPanel();
        contentPanel.add(dashboardPanel, "Dashboard");
        contentPanel.add(new StudentManagementView(), "Students");
        contentPanel.add(new StaffManagementView(), "Staff");
        contentPanel.add(new AppointmentManagementView(), "Appointments");
        contentPanel.add(new ScheduleManagementView(), "Schedules");
        contentPanel.add(new ReportsView(), "Reports");
        contentPanel.add(new SettingsView(), "Settings");
        
        // Nurse/Student pages
        contentPanel.add(new AppointmentManagementView(), "My Appointments");
        contentPanel.add(new ScheduleManagementView(), "Schedule");
        contentPanel.add(createProfilePanel(), "Profile");
        contentPanel.add(createBookAppointmentPanel(), "Book Appointment");
        
        mainArea.add(contentPanel, BorderLayout.CENTER);
        
        add(mainArea, BorderLayout.CENTER);
    }
    
    // ============================================================
    // CREATE PROFILE PANEL
    // ============================================================
    private JPanel createProfilePanel() {
        JPanel profilePanel = new JPanel(new BorderLayout(15, 15));
        profilePanel.setBackground(AppColors.BG_LIGHT);
        profilePanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("👤 User Profile");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(AppColors.TEXT_PRIMARY);
        profilePanel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel infoPanel = new JPanel(new GridBagLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.CARD_BORDER, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        infoPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        JLabel usernameValue = new JLabel(currentUser.getUsername());
        usernameValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        infoPanel.add(usernameValue, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        infoPanel.add(new JLabel("Role:"), gbc);
        gbc.gridx = 1;
        JLabel roleValue = new JLabel(currentUser.getRoleName());
        roleValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        infoPanel.add(roleValue, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        infoPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        JLabel statusValue = new JLabel(currentUser.isActive() ? "🟢 Active" : "🔴 Inactive");
        statusValue.setFont(new Font("Segoe UI", Font.BOLD, 14));
        statusValue.setForeground(currentUser.isActive() ? new Color(0, 150, 0) : new Color(200, 0, 0));
        infoPanel.add(statusValue, gbc);
        
        profilePanel.add(infoPanel, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> showPage("Dashboard"));
        btnPanel.add(closeBtn);
        profilePanel.add(btnPanel, BorderLayout.SOUTH);
        
        return profilePanel;
    }
    
    // ============================================================
    // CREATE BOOK APPOINTMENT PANEL
    // ============================================================
    private JPanel createBookAppointmentPanel() {
        JPanel bookingPanel = new JPanel(new BorderLayout(15, 15));
        bookingPanel.setBackground(AppColors.BG_LIGHT);
        bookingPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("📅 Book Appointment");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(AppColors.TEXT_PRIMARY);
        bookingPanel.add(titleLabel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.CARD_BORDER, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        centerPanel.add(new JLabel("Select Service:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> serviceCombo = new JComboBox<>(new String[]{"General Consultation", "Counselling", "Medical Check-up", "Follow-up"});
        serviceCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        centerPanel.add(serviceCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        centerPanel.add(new JLabel("Select Date:"), gbc);
        gbc.gridx = 1;
        JTextField dateField = new JTextField(15);
        dateField.setText(java.time.LocalDate.now().toString());
        dateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        centerPanel.add(dateField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        centerPanel.add(new JLabel("Select Time:"), gbc);
        gbc.gridx = 1;
        JComboBox<String> timeCombo = new JComboBox<>(new String[]{
            "09:00", "09:30", "10:00", "10:30", "11:00", "11:30", 
            "14:00", "14:30", "15:00", "15:30"
        });
        timeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        centerPanel.add(timeCombo, gbc);
        
        bookingPanel.add(centerPanel, BorderLayout.CENTER);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        btnPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JButton bookBtn = new JButton("✅ Book Appointment");
        bookBtn.setBackground(AppColors.SUCCESS);
        bookBtn.setForeground(Color.WHITE);
        bookBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bookBtn.setFocusPainted(false);
        bookBtn.setBorderPainted(false);
        bookBtn.setPreferredSize(new Dimension(180, 40));
        bookBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "✅ Appointment booked successfully!\n\n" +
                "Service: " + serviceCombo.getSelectedItem() + "\n" +
                "Date: " + dateField.getText() + "\n" +
                "Time: " + timeCombo.getSelectedItem(),
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            showPage("Dashboard");
        });
        btnPanel.add(bookBtn);
        
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setBackground(AppColors.DANGER);
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorderPainted(false);
        cancelBtn.setPreferredSize(new Dimension(120, 40));
        cancelBtn.addActionListener(e -> showPage("Dashboard"));
        btnPanel.add(cancelBtn);
        
        bookingPanel.add(btnPanel, BorderLayout.SOUTH);
        
        return bookingPanel;
    }
    
    // ============================================================
    // SIDEBAR WITH CLINIC LOGO AND USER AVATAR (PNG ICONS)
    // ============================================================
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(AppColors.BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(250, 0));
        sidebar.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        // ============================================================
        // LOGO SECTION - Using clinic.png
        // ============================================================
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setOpaque(false);
        logoPanel.setBorder(new EmptyBorder(0, 0, 30, 0));
        
        ImageIcon clinicIcon = IconLoader.loadSidebarIcon("clinic");
        
        JLabel logoLabel = new JLabel("CCAMS");
        if (clinicIcon != null) {
            logoLabel.setIcon(clinicIcon);
        }
        logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setIconTextGap(10);
        
        logoPanel.add(logoLabel);
        sidebar.add(logoPanel);
        
        // ============================================================
        // USER INFO SECTION - Using user.png
        // ============================================================
        JPanel userInfo = new JPanel(new GridBagLayout());
        userInfo.setOpaque(false);
        userInfo.setBorder(new EmptyBorder(0, 20, 30, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        
        ImageIcon userIconImage = IconLoader.loadSidebarIcon("user");
        
        JLabel userIcon;
        if (userIconImage != null) {
            userIcon = new JLabel(userIconImage);
        } else {
            userIcon = new JLabel("◉");
            userIcon.setFont(new Font("Segoe UI", Font.PLAIN, 32));
            userIcon.setForeground(new Color(150, 180, 220));
        }
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        userInfo.add(userIcon, gbc);
        
        JPanel userTextPanel = new JPanel(new GridLayout(2, 1));
        userTextPanel.setOpaque(false);
        
        JLabel userName = new JLabel(currentUser.getUsername());
        userName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userName.setForeground(Color.WHITE);
        userTextPanel.add(userName);
        
        JLabel userRole = new JLabel(currentUser.getRoleName());
        userRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userRole.setForeground(new Color(150, 180, 220));
        userTextPanel.add(userRole);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.insets = new Insets(5, 12, 0, 0);
        userInfo.add(userTextPanel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 12, 5, 0);
        JLabel emptyLabel = new JLabel(" ");
        userInfo.add(emptyLabel, gbc);
        
        sidebar.add(userInfo);
        
        // ============================================================
        // DIVIDER LINE
        // ============================================================
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(255, 255, 255, 30));
        separator.setMaximumSize(new Dimension(200, 1));
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(separator);
        sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        
        // ============================================================
        // NAVIGATION ITEMS
        // ============================================================
        String[] menuItems = getMenuItemsForRole(currentUser.getRoleName());
        for (String item : menuItems) {
            JButton navButton = createNavButton(item);
            sidebar.add(navButton);
            sidebar.add(Box.createRigidArea(new Dimension(0, 5)));
            navButtons.put(item, navButton);
        }
        
        sidebar.add(Box.createVerticalGlue());
        
        // ============================================================
        // LOGOUT BUTTON
        // ============================================================
        JButton logoutBtn = createLogoutButton();
        sidebar.add(logoutBtn);
        
        return sidebar;
    }
    
    // ============================================================
    // CREATE NAV BUTTON WITH PNG ICON
    // ============================================================
    private JButton createNavButton(String text) {
        String iconName = getIconNameForMenuItem(text);
        ImageIcon icon = IconLoader.loadSidebarIcon(iconName);
        
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(new Color(180, 200, 230));
        button.setBackground(AppColors.BG_SIDEBAR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(12, 25, 12, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setIconTextGap(12);
        
        if (icon != null) {
            button.setIcon(icon);
        }
        
        button.addActionListener(e -> {
            System.out.println("🔘 Navigation clicked: " + text);
            showPage(text);
            updateActiveButton(text);
        });
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!button.getBackground().equals(AppColors.PRIMARY_LIGHT)) {
                    button.setBackground(AppColors.PRIMARY_LIGHT);
                }
            }
            public void mouseExited(MouseEvent e) {
                if (!button.getBackground().equals(AppColors.PRIMARY_LIGHT)) {
                    button.setBackground(AppColors.BG_SIDEBAR);
                }
            }
        });
        
        return button;
    }
    
    private String getIconNameForMenuItem(String menuItem) {
        switch (menuItem) {
            case "Dashboard": return "dashboard";
            case "Students": return "students";
            case "Staff": return "staff";
            case "Appointments": return "appointments";
            case "Schedules": return "schedules";
            case "Reports": return "reports";
            case "Settings": return "settings";
            case "My Appointments": return "appointments";
            case "Schedule": return "schedules";
            case "Profile": return "user";
            case "Book Appointment": return "appointments";
            default: return "dashboard";
        }
    }
    
    // ============================================================
    // LOGOUT BUTTON WITH PNG ICON
    // ============================================================
    private JButton createLogoutButton() {
        ImageIcon icon = IconLoader.loadSidebarIcon("logout");
        JButton button = new JButton("Logout");
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setForeground(new Color(255, 150, 150));
        button.setBackground(AppColors.BG_SIDEBAR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorder(new EmptyBorder(12, 25, 12, 25));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setIconTextGap(12);
        
        if (icon != null) {
            button.setIcon(icon);
        }
        
        button.addActionListener(e -> logout());
        
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(45, 35, 35));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(AppColors.BG_SIDEBAR);
            }
        });
        
        return button;
    }
    
    private void updateActiveButton(String activePage) {
        for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
            JButton btn = entry.getValue();
            if (entry.getKey().equals(activePage)) {
                btn.setBackground(AppColors.PRIMARY_LIGHT);
                btn.setForeground(Color.WHITE);
            } else {
                btn.setBackground(AppColors.BG_SIDEBAR);
                btn.setForeground(new Color(180, 200, 230));
            }
        }
    }
    
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setPreferredSize(new Dimension(0, 65));
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.CARD_BORDER));
        
        JLabel pageTitle = new JLabel("Dashboard");
        pageTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        pageTitle.setForeground(AppColors.TEXT_PRIMARY);
        pageTitle.setBorder(new EmptyBorder(0, 25, 0, 0));
        topBar.add(pageTitle, BorderLayout.WEST);
        lblCurrentPage = pageTitle;
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        
        JLabel dateLabel = new JLabel(java.time.LocalDate.now().toString());
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        dateLabel.setForeground(AppColors.TEXT_SECONDARY);
        rightPanel.add(dateLabel);
        
        topBar.add(rightPanel, BorderLayout.EAST);
        
        return topBar;
    }
    
    private String[] getMenuItemsForRole(String role) {
        if (role == null) return new String[]{"Dashboard"};
        
        switch (role) {
            case "Administrator":
                return new String[]{"Dashboard", "Students", "Staff", "Appointments", 
                                   "Schedules", "Reports", "Settings"};
            case "Nurse":
                return new String[]{"Dashboard", "My Appointments", "Schedule", "Profile"};
            case "Psychologist":
                return new String[]{"Dashboard", "My Appointments", "Schedule", "Profile"};
            case "Student":
                return new String[]{"Dashboard", "Book Appointment", "My Appointments", "Profile"};
            default:
                return new String[]{"Dashboard"};
        }
    }
    
    // ============================================================
    // SHOW PAGE - HANDLES NAVIGATION
    // ============================================================
    private void showPage(String pageName) {
        System.out.println("📄 Showing page: " + pageName);
        cardLayout.show(contentPanel, pageName);
        if (lblCurrentPage != null) {
            lblCurrentPage.setText(pageName);
        }
        if (pageName.equals("Dashboard")) {
            refreshDashboard();
        }
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(AppColors.BG_LIGHT);
        
        JPanel statsPanel = createStatsPanel();
        panel.add(statsPanel, BorderLayout.NORTH);
        
        JPanel chartsPanel = createChartsPanel();
        panel.add(chartsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 5, 15, 15));
        panel.setBackground(AppColors.BG_LIGHT);
        
        lblTotalAppointments = createStatCard(panel, "Total Appointments", "0", AppColors.TEXT_PRIMARY, "appointments");
        lblPending = createStatCard(panel, "Pending", "0", AppColors.WARNING, "pending");
        lblApproved = createStatCard(panel, "Approved", "0", AppColors.ACCENT, "approved");
        lblCompleted = createStatCard(panel, "Completed", "0", AppColors.SUCCESS, "completed");
        lblCancelled = createStatCard(panel, "Cancelled", "0", AppColors.DANGER, "cancelled");
        
        lblTotalStudents = createStatCard(panel, "Students", "0", new Color(0, 150, 136), "user");
        lblTotalStaff = createStatCard(panel, "Staff", "0", new Color(123, 31, 162), "staff");
        lblTotalNurses = createStatCard(panel, "Nurses", "0", AppColors.ACCENT, "clinic");
        lblTotalPsychologists = createStatCard(panel, "Psychologists", "0", new Color(255, 87, 34), "clinic");
        
        JPanel emptyPanel = new JPanel();
        emptyPanel.setBackground(AppColors.BG_LIGHT);
        panel.add(emptyPanel);
        
        return panel;
    }
    
    private JLabel createStatCard(JPanel parent, String title, String value, Color color, String iconName) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.CARD_BORDER, 1),
            new EmptyBorder(15, 18, 15, 18)
        ));
        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        ImageIcon icon = IconLoader.loadStatIcon(iconName);
        JLabel iconLabel;
        if (icon != null) {
            iconLabel = new JLabel(icon);
        } else {
            iconLabel = new JLabel(getUnicodeForStat(iconName));
            iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 22));
            iconLabel.setForeground(color);
        }
        topPanel.add(iconLabel, BorderLayout.WEST);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        titleLabel.setForeground(AppColors.TEXT_SECONDARY);
        topPanel.add(titleLabel, BorderLayout.EAST);
        
        card.add(topPanel, BorderLayout.NORTH);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(valueLabel, BorderLayout.CENTER);
        
        parent.add(card);
        return valueLabel;
    }
    
    private String getUnicodeForStat(String iconName) {
        switch (iconName) {
            case "appointments": return "▣";
            case "pending": return "◒";
            case "approved": return "✓";
            case "completed": return "✔";
            case "cancelled": return "✕";
            case "students": return "◉";
            case "staff": return "◈";
            default: return "•";
        }
    }
    
    private JPanel createChartsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 15, 15));
        panel.setBackground(AppColors.BG_LIGHT);
        
        JPanel pieChartPanel = createPieChartPanel();
        panel.add(pieChartPanel);
        
        JPanel barChartPanel = createBarChartPanel();
        panel.add(barChartPanel);
        
        return panel;
    }
    
    private JPanel createPieChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.CARD_BORDER, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("Appointment Status Distribution");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        DefaultPieDataset dataset = new DefaultPieDataset();
        ReportData report = reportService.generateSummaryReport();
        dataset.setValue("Pending", report.getPendingAppointments());
        dataset.setValue("Approved", report.getApprovedAppointments());
        dataset.setValue("Completed", report.getCompletedAppointments());
        dataset.setValue("Cancelled", report.getCancelledAppointments());
        
        JFreeChart chart = ChartFactory.createPieChart("", dataset, true, true, false);
        
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{1}"));
        plot.setLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        plot.setShadowPaint(null);
        plot.setSectionPaint("Pending", AppColors.WARNING);
        plot.setSectionPaint("Approved", AppColors.ACCENT);
        plot.setSectionPaint("Completed", AppColors.SUCCESS);
        plot.setSectionPaint("Cancelled", AppColors.DANGER);
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 280));
        chartPanel.setBackground(Color.WHITE);
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createBarChartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.CARD_BORDER, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel("Weekly Appointment Trends");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        
        try {
            java.sql.Date today = new java.sql.Date(System.currentTimeMillis());
            java.sql.Date weekStart = new java.sql.Date(today.getTime() - 7 * 24 * 60 * 60 * 1000L);
            
            List<Appointment> appointments = appointmentDAO.getAppointmentsByDateRange(weekStart, today);
            
            int[] dayCounts = new int[7];
            String[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
            
            for (Appointment a : appointments) {
                if (a.getAppointmentDate() != null) {
                    java.util.Calendar cal = java.util.Calendar.getInstance();
                    cal.setTime(a.getAppointmentDate());
                    int dayOfWeek = cal.get(java.util.Calendar.DAY_OF_WEEK);
                    int index = (dayOfWeek + 5) % 7;
                    if (index >= 0 && index < 7) {
                        dayCounts[index]++;
                    }
                }
            }
            
            for (int i = 0; i < 7; i++) {
                dataset.addValue(dayCounts[i], "Appointments", dayNames[i]);
            }
        } catch (Exception e) {
            e.printStackTrace();
            dataset.addValue(12, "Appointments", "Mon");
            dataset.addValue(8, "Appointments", "Tue");
            dataset.addValue(15, "Appointments", "Wed");
            dataset.addValue(10, "Appointments", "Thu");
            dataset.addValue(6, "Appointments", "Fri");
            dataset.addValue(3, "Appointments", "Sat");
            dataset.addValue(2, "Appointments", "Sun");
        }
        
        JFreeChart chart = ChartFactory.createBarChart(
            "", "Day", "Appointments", dataset,
            PlotOrientation.VERTICAL, false, true, false
        );
        
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setDomainGridlinePaint(AppColors.CARD_BORDER);
        plot.setRangeGridlinePaint(AppColors.CARD_BORDER);
        plot.setOutlinePaint(null);
        
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, AppColors.ACCENT);
        
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        rangeAxis.setTickUnit(new org.jfree.chart.axis.NumberTickUnit(1));
        
        double maxValue = 0;
        for (int i = 0; i < dataset.getRowCount(); i++) {
            for (int j = 0; j < dataset.getColumnCount(); j++) {
                Number value = dataset.getValue(i, j);
                if (value != null && value.doubleValue() > maxValue) {
                    maxValue = value.doubleValue();
                }
            }
        }
        rangeAxis.setUpperBound(Math.max(5, maxValue + 2));
        
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryMargin(0.2);
        domainAxis.setTickLabelFont(new Font("Segoe UI", Font.PLAIN, 12));
        
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(400, 280));
        chartPanel.setBackground(Color.WHITE);
        panel.add(chartPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    public void refreshDashboard() {
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
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Logout Confirmation",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            new LoginForm().setVisible(true);
        }
    }
}