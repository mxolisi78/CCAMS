package ccams.views;

import ccams.dao.AppointmentDAO;
import ccams.dao.StaffDAO;
import ccams.dao.StudentDAO;
import ccams.models.Appointment;
import ccams.models.Student;
import ccams.models.Staff;
import ccams.constants.AppColors;
import ccams.utils.IconLoader;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.List;

public class AppointmentManagementView extends JPanel {
    private AppointmentDAO appointmentDAO;
    private StudentDAO studentDAO;
    private StaffDAO staffDAO;
    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh, btnSearch;
    private JPanel formPanel;
    
    // Form fields
    private JComboBox<String> cmbStudent;
    private JComboBox<String> cmbStaff;
    private JTextField txtDateTime;
    private JButton btnDatePicker;
    private JComboBox<String> cmbTimeHour, cmbTimeMinute;
    private JComboBox<String> cmbStatus;
    private JButton btnSave, btnCancel;
    
    private int selectedAppointmentId = -1;
    private boolean isEditMode = false;
    
    // Date format
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    public AppointmentManagementView() {
        appointmentDAO = new AppointmentDAO();
        studentDAO = new StudentDAO();
        staffDAO = new StaffDAO();
        initComponents();
        loadComboBoxes();
        loadAppointments();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(AppColors.BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        JPanel topPanel = createSearchPanel();
        add(topPanel, BorderLayout.NORTH);
        
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        formPanel = createFormPanel();
        add(formPanel, BorderLayout.SOUTH);
        
        formPanel.setVisible(false);
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, AppColors.CARD_BORDER),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        panel.add(new JLabel(IconLoader.loadIcon("search", 16, 16))); 
        searchField = new JTextField(15);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        panel.add(searchField);
        
        btnSearch = createStyledButton("Search", AppColors.ACCENT, "search");
        btnSearch.addActionListener(e -> searchAppointments());
        panel.add(btnSearch);
        
        btnRefresh = createStyledButton("Refresh", new Color(100, 100, 100), "refresh");
        btnRefresh.addActionListener(e -> loadAppointments());
        panel.add(btnRefresh);
        
        panel.add(new JLabel("Filter Status:"));
        statusFilter = new JComboBox<>();
        statusFilter.addItem("All");
        statusFilter.addItem("Pending");
        statusFilter.addItem("Approved");
        statusFilter.addItem("Completed");
        statusFilter.addItem("Cancelled");
        statusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusFilter.addActionListener(e -> loadAppointments());
        panel.add(statusFilter);
        
        btnAdd = createStyledButton("Book Appointment", AppColors.SUCCESS, "add-schedule");
        btnAdd.addActionListener(e -> showAddForm());
        panel.add(btnAdd);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        String[] columns = {"ID", "Student", "Staff", "Date", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        appointmentTable = new JTable(tableModel);
        appointmentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        appointmentTable.setRowHeight(35);
        appointmentTable.setSelectionBackground(new Color(220, 235, 250));
        appointmentTable.setSelectionForeground(AppColors.TEXT_PRIMARY);
        appointmentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        appointmentTable.getTableHeader().setBackground(AppColors.BG_LIGHT);
        appointmentTable.getTableHeader().setForeground(AppColors.TEXT_PRIMARY);
        appointmentTable.getTableHeader().setPreferredSize(new Dimension(0, 35));
        
        // Custom Table Header Renderer for Icons
        appointmentTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                switch (column) {
                    case 0: setIcon(IconLoader.loadIcon("dashboard", 14, 14)); break;
                    case 1: setIcon(IconLoader.loadIcon("students", 14, 14)); break;
                    case 2: setIcon(IconLoader.loadIcon("staff", 14, 14)); break;
                    case 3: setIcon(IconLoader.loadIcon("date", 14, 14)); break;
                    case 4: setIcon(IconLoader.loadIcon("settings", 14, 14)); break;
                    default: setIcon(null);
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                setBackground(AppColors.BG_LIGHT);
                return this;
            }
        });
        
        rowSorter = new TableRowSorter<>(tableModel);
        appointmentTable.setRowSorter(rowSorter);
        
        appointmentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedAppointment();
                } else {
                    selectAppointment();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppColors.CARD_BORDER, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        btnEdit = createStyledButton("Edit", new Color(0, 120, 215), "edit");
        btnEdit.addActionListener(e -> editSelectedAppointment());
        buttonPanel.add(btnEdit);
        
        btnDelete = createStyledButton("Cancel", AppColors.DANGER, "delete");
        btnDelete.addActionListener(e -> deleteSelectedAppointment());
        buttonPanel.add(btnDelete);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, AppColors.CARD_BORDER),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel formTitle = new JLabel("📅 Appointment Details");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(AppColors.TEXT_PRIMARY);
        panel.add(formTitle, BorderLayout.NORTH);
        
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Student
        gbc.gridx = 0; gbc.gridy = row;
        fieldsPanel.add(createLabel("Student:"), gbc);
        gbc.gridx = 1;
        cmbStudent = new JComboBox<>();
        cmbStudent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbStudent.setPreferredSize(new Dimension(180, 30));
        fieldsPanel.add(cmbStudent, gbc);
        
        gbc.gridx = 2;
        fieldsPanel.add(createLabel("Staff:"), gbc);
        gbc.gridx = 3;
        cmbStaff = new JComboBox<>();
        cmbStaff.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbStaff.setPreferredSize(new Dimension(180, 30));
        fieldsPanel.add(cmbStaff, gbc);
        
        // Date & Time with Date Picker
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        fieldsPanel.add(createLabel("Date:"), gbc);
        gbc.gridx = 1;
        
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        datePanel.setBackground(Color.WHITE);
        
        txtDateTime = new JTextField(12);
        txtDateTime.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDateTime.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        txtDateTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        txtDateTime.setEditable(true);
        datePanel.add(txtDateTime);
        
        // Date Picker Button with Calendar Icon
        btnDatePicker = new JButton(IconLoader.loadIcon("date", 20, 20));
        if (btnDatePicker.getIcon() == null) {
            btnDatePicker.setText("📅");
        }
        btnDatePicker.setBackground(Color.WHITE);
        btnDatePicker.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));
        btnDatePicker.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDatePicker.setToolTipText("Select Date");
        btnDatePicker.addActionListener(e -> showDatePicker());
        datePanel.add(btnDatePicker);
        
        fieldsPanel.add(datePanel, gbc);
        
        // Time
        gbc.gridx = 2;
        fieldsPanel.add(createLabel("Time:"), gbc);
        gbc.gridx = 3;
        
        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        timePanel.setBackground(Color.WHITE);
        
        cmbTimeHour = new JComboBox<>();
        cmbTimeHour.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbTimeHour.setPreferredSize(new Dimension(60, 30));
        for (int i = 8; i <= 17; i++) {
            cmbTimeHour.addItem(String.format("%02d", i));
        }
        timePanel.add(cmbTimeHour);
        
        timePanel.add(new JLabel(" : "));
        
        cmbTimeMinute = new JComboBox<>();
        cmbTimeMinute.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbTimeMinute.setPreferredSize(new Dimension(60, 30));
        cmbTimeMinute.addItem("00");
        cmbTimeMinute.addItem("15");
        cmbTimeMinute.addItem("30");
        cmbTimeMinute.addItem("45");
        timePanel.add(cmbTimeMinute);
        
        fieldsPanel.add(timePanel, gbc);
        
        // Status
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        fieldsPanel.add(createLabel("Status:"), gbc);
        gbc.gridx = 1;
        cmbStatus = new JComboBox<>();
        cmbStatus.addItem("Pending");
        cmbStatus.addItem("Approved");
        cmbStatus.addItem("Completed");
        cmbStatus.addItem("Cancelled");
        cmbStatus.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fieldsPanel.add(cmbStatus, gbc);
        
        panel.add(fieldsPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        btnSave = createStyledButton("Save", AppColors.SUCCESS, "save");
        btnSave.addActionListener(e -> saveAppointment());
        buttonPanel.add(btnSave);
        
        btnCancel = createStyledButton("Cancel", AppColors.DANGER, "cancel");
        btnCancel.addActionListener(e -> hideForm());
        buttonPanel.add(btnCancel);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // ============================================================
    // DATE PICKER - COMPLETE VERSION
    // ============================================================
    private void showDatePicker() {
        // Get current date from text field
        String currentDate = txtDateTime.getText().trim();
        Calendar cal = Calendar.getInstance();
        try {
            if (!currentDate.isEmpty()) {
                String[] parts = currentDate.split("-");
                if (parts.length == 3) {
                    cal.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, Integer.parseInt(parts[2]));
                }
            }
        } catch (Exception e) {
            cal = Calendar.getInstance();
        }
        
        // Create date picker dialog
        JDialog dateDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Select Date", true);
        dateDialog.setSize(350, 420);
        dateDialog.setLocationRelativeTo(this);
        dateDialog.setUndecorated(true);
        dateDialog.setBackground(Color.WHITE);
        dateDialog.getRootPane().setBorder(BorderFactory.createLineBorder(AppColors.ACCENT, 2));
        
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Header
        JPanel headerPanel = createDatePickerHeader(dateDialog);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Calendar body
        JPanel calendarBody = createCalendarBody(cal, txtDateTime, dateDialog);
        mainPanel.add(calendarBody, BorderLayout.CENTER);
        
        // Footer
        JPanel footerPanel = createDatePickerFooter(dateDialog);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        dateDialog.add(mainPanel);
        dateDialog.setVisible(true);
    }
    
    // ============================================================
    // DATE PICKER HEADER - With Close Icon
    // ============================================================
    private JPanel createDatePickerHeader(JDialog dialog) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(AppColors.ACCENT);
        panel.setPreferredSize(new Dimension(0, 40));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        
        JLabel titleLabel = new JLabel("📅 Select Date");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.WEST);
        
        // Close Button with Icon
        ImageIcon closeIcon = IconLoader.loadIcon("close", 20, 20);
        JButton closeBtn;
        if (closeIcon != null) {
            closeBtn = new JButton(closeIcon);
        } else {
            closeBtn = new JButton("✕");
            closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        }
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dialog.dispose());
        panel.add(closeBtn, BorderLayout.EAST);
        
        return panel;
    }
    
    // ============================================================
    // DATE PICKER CALENDAR BODY - With Left & Right Icons
    // ============================================================
    private JPanel createCalendarBody(Calendar cal, JTextField dateField, JDialog dialog) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Navigation Panel with Left & Right Icons
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        navPanel.setBackground(Color.WHITE);
        
        // LEFT BUTTON WITH ICON
        ImageIcon leftIcon = IconLoader.loadIcon("left", 20, 20);
        JButton prevBtn;
        if (leftIcon != null) {
            prevBtn = new JButton(leftIcon);
            prevBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        } else {
            prevBtn = new JButton("◀");
            prevBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        }
        prevBtn.setBackground(Color.WHITE);
        prevBtn.setBorder(BorderFactory.createLineBorder(AppColors.CARD_BORDER, 1));
        prevBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        prevBtn.setFocusPainted(false);
        prevBtn.setToolTipText("Previous Month");
        navPanel.add(prevBtn);
        
        JLabel monthYearLabel = new JLabel();
        monthYearLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        monthYearLabel.setPreferredSize(new Dimension(150, 30));
        monthYearLabel.setHorizontalAlignment(SwingConstants.CENTER);
        navPanel.add(monthYearLabel);
        
        // RIGHT BUTTON WITH ICON
        ImageIcon rightIcon = IconLoader.loadIcon("right", 20, 20);
        JButton nextBtn;
        if (rightIcon != null) {
            nextBtn = new JButton(rightIcon);
            nextBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        } else {
            nextBtn = new JButton("▶");
            nextBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        }
        nextBtn.setBackground(Color.WHITE);
        nextBtn.setBorder(BorderFactory.createLineBorder(AppColors.CARD_BORDER, 1));
        nextBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nextBtn.setFocusPainted(false);
        nextBtn.setToolTipText("Next Month");
        navPanel.add(nextBtn);
        
        panel.add(navPanel, BorderLayout.NORTH);
        
        // Days Grid
        JPanel gridPanel = new JPanel(new GridLayout(7, 7, 2, 2));
        gridPanel.setBackground(Color.WHITE);
        gridPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        // Day names
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : dayNames) {
            JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
            dayLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
            dayLabel.setForeground(AppColors.TEXT_SECONDARY);
            gridPanel.add(dayLabel);
        }
        
        // Day buttons - store in array for updating
        JButton[] dayButtons = new JButton[42];
        for (int i = 0; i < 42; i++) {
            JButton dayBtn = new JButton();
            dayBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            dayBtn.setBackground(Color.WHITE);
            dayBtn.setBorder(BorderFactory.createLineBorder(new Color(240, 240, 240), 1));
            dayBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            dayBtn.setFocusPainted(false);
            dayBtn.setPreferredSize(new Dimension(40, 35));
            gridPanel.add(dayBtn);
            dayButtons[i] = dayBtn;
        }
        
        panel.add(gridPanel, BorderLayout.CENTER);
        
        // Update calendar
        updateCalendarGrid(cal, monthYearLabel, dayButtons, dateField, dialog);
        
        // Navigation listeners
        prevBtn.addActionListener(e -> {
            cal.add(Calendar.MONTH, -1);
            updateCalendarGrid(cal, monthYearLabel, dayButtons, dateField, dialog);
        });
        
        nextBtn.addActionListener(e -> {
            cal.add(Calendar.MONTH, 1);
            updateCalendarGrid(cal, monthYearLabel, dayButtons, dateField, dialog);
        });
        
        return panel;
    }
    
    private void updateCalendarGrid(Calendar cal, JLabel monthYearLabel, JButton[] dayButtons, 
                                    JTextField dateField, JDialog dialog) {
        // Update month/year label
        monthYearLabel.setText(String.format("%s %d", 
            new java.text.SimpleDateFormat("MMMM").format(cal.getTime()), 
            cal.get(Calendar.YEAR)));
        
        // Get days in month
        int firstDayOfMonth = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        // Get current date
        Calendar today = Calendar.getInstance();
        int todayDay = today.get(Calendar.DAY_OF_MONTH);
        int todayMonth = today.get(Calendar.MONTH);
        int todayYear = today.get(Calendar.YEAR);
        
        // Get selected date
        int selectedDay = 0;
        int selectedMonth = -1;
        int selectedYear = -1;
        try {
            String dateStr = dateField.getText().trim();
            if (!dateStr.isEmpty()) {
                String[] parts = dateStr.split("-");
                if (parts.length == 3) {
                    selectedDay = Integer.parseInt(parts[2]);
                    selectedMonth = Integer.parseInt(parts[1]) - 1;
                    selectedYear = Integer.parseInt(parts[0]);
                }
            }
        } catch (Exception e) {
            // Ignore
        }
        
        // Update day buttons
        int day = 1;
        for (int i = 0; i < 42; i++) {
            JButton btn = dayButtons[i];
            
            if (i < firstDayOfMonth || i >= firstDayOfMonth + daysInMonth) {
                btn.setText("");
                btn.setEnabled(false);
                btn.setBackground(Color.WHITE);
                btn.setForeground(Color.GRAY);
                btn.setBorder(BorderFactory.createLineBorder(new Color(240, 240, 240), 1));
            } else {
                int currentDay = day;
                btn.setText(String.valueOf(currentDay));
                btn.setEnabled(true);
                btn.setBackground(Color.WHITE);
                btn.setForeground(Color.BLACK);
                btn.setBorder(BorderFactory.createLineBorder(new Color(240, 240, 240), 1));
                
                // Highlight today
                if (currentDay == todayDay && cal.get(Calendar.MONTH) == todayMonth && cal.get(Calendar.YEAR) == todayYear) {
                    btn.setBackground(new Color(200, 230, 255));
                    btn.setBorder(BorderFactory.createLineBorder(new Color(0, 120, 215), 2));
                }
                
                // Highlight selected date
                if (currentDay == selectedDay && cal.get(Calendar.MONTH) == selectedMonth && cal.get(Calendar.YEAR) == selectedYear) {
                    btn.setBackground(AppColors.ACCENT);
                    btn.setForeground(Color.WHITE);
                    btn.setBorder(BorderFactory.createLineBorder(AppColors.ACCENT, 2));
                }
                
                // Remove old listeners
                for (ActionListener al : btn.getActionListeners()) {
                    btn.removeActionListener(al);
                }
                
                // Add click listener
                final int finalDay = currentDay;
                final int finalMonth = cal.get(Calendar.MONTH);
                final int finalYear = cal.get(Calendar.YEAR);
                final JTextField field = dateField;
                final JDialog dialogRef = dialog;
                
                btn.addActionListener(e -> {
                    field.setText(String.format("%04d-%02d-%02d", finalYear, finalMonth + 1, finalDay));
                    dialogRef.dispose();
                });
                
                day++;
            }
        }
    }
    
    // ============================================================
    // DATE PICKER FOOTER - With Today and Clear Icons
    // ============================================================
    private JPanel createDatePickerFooter(JDialog dialog) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        // TODAY BUTTON WITH ICON
        ImageIcon todayIcon = IconLoader.loadIcon("today", 16, 16);
        JButton todayBtn;
        if (todayIcon != null) {
            todayBtn = new JButton("Today", todayIcon);
        } else {
            todayBtn = new JButton("✅ Today");
        }
        todayBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        todayBtn.setBackground(AppColors.ACCENT);
        todayBtn.setForeground(Color.WHITE);
        todayBtn.setFocusPainted(false);
        todayBtn.setBorderPainted(false);
        todayBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        todayBtn.setPreferredSize(new Dimension(100, 35));
        todayBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
        todayBtn.setIconTextGap(8);
        todayBtn.addActionListener(e -> {
            Calendar today = Calendar.getInstance();
            txtDateTime.setText(String.format("%04d-%02d-%02d", 
                today.get(Calendar.YEAR), 
                today.get(Calendar.MONTH) + 1, 
                today.get(Calendar.DAY_OF_MONTH)));
            dialog.dispose();
        });
        panel.add(todayBtn);
        
        // CLEAR BUTTON WITH ICON
        ImageIcon clearIcon = IconLoader.loadIcon("clear", 16, 16);
        JButton clearBtn;
        if (clearIcon != null) {
            clearBtn = new JButton("Clear", clearIcon);
        } else {
            clearBtn = new JButton("🗑️ Clear");
        }
        clearBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        clearBtn.setBackground(Color.LIGHT_GRAY);
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFocusPainted(false);
        clearBtn.setBorderPainted(false);
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.setPreferredSize(new Dimension(100, 35));
        clearBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
        clearBtn.setIconTextGap(8);
        clearBtn.addActionListener(e -> {
            txtDateTime.setText("");
            dialog.dispose();
        });
        panel.add(clearBtn);
        
        return panel;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(AppColors.TEXT_SECONDARY);
        return label;
    }
    
    private JButton createStyledButton(String text, Color bgColor, String iconName) {
        JButton button;
        if (iconName != null && !iconName.isEmpty()) {
            button = new JButton(text, IconLoader.loadIcon(iconName, 16, 16));
        } else {
            button = new JButton(text);
        }
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 35));
        return button;
    }
    
    private void loadComboBoxes() {
        cmbStudent.removeAllItems();
        List<Student> students = studentDAO.getAllStudents();
        for (Student s : students) {
            cmbStudent.addItem(s.getStudentNumber() + " - " + s.getFullName());
        }
        
        cmbStaff.removeAllItems();
        List<Staff> staffList = staffDAO.getAllStaff();
        System.out.println("Loaded " + staffList.size() + " staff members.");
        for (Staff s : staffList) {
            cmbStaff.addItem(s.getFullName() + " (" + s.getProfession() + ")");
        }
    }
    
    private void loadAppointments() {
        tableModel.setRowCount(0);
        List<Appointment> appointments = appointmentDAO.getAllAppointments();
        
        String selectedStatus = (String) statusFilter.getSelectedItem();
        if (selectedStatus == null) {
            selectedStatus = "All";
        }
        
        String search = searchField.getText().trim().toLowerCase();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        for (Appointment a : appointments) {
            if (!selectedStatus.equals("All") && !a.getStatusName().equals(selectedStatus)) continue;
            if (!search.isEmpty() && 
                !a.getStudentName().toLowerCase().contains(search) && 
                !a.getStaffName().toLowerCase().contains(search)) continue;
            
            String dateStr = a.getAppointmentDate() != null ? sdf.format(a.getAppointmentDate()) : "";
            String statusDisplay = a.getStatusName() != null ? a.getStatusName() : "Pending";
            
            tableModel.addRow(new Object[]{
                a.getAppointmentId(),
                a.getStudentName() != null ? a.getStudentName() : "Unknown",
                a.getStaffName() != null ? a.getStaffName() + " (" + a.getStaffProfession() + ")" : "Unknown Staff",
                dateStr,
                statusDisplay
            });
        }
        tableModel.fireTableDataChanged();
    }
    
    private void searchAppointments() { loadAppointments(); }
    
    private void showAddForm() {
        isEditMode = false;
        selectedAppointmentId = -1;
        clearForm();
        loadComboBoxes();
        cmbStatus.setSelectedItem("Pending");
        formPanel.setVisible(true);
        btnSave.setText("Book Appointment");
        btnSave.setIcon(IconLoader.loadIcon("add-schedule", 16, 16));
    }
    
    private void editSelectedAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to edit.");
            return;
        }
        int modelRow = appointmentTable.convertRowIndexToModel(selectedRow);
        selectedAppointmentId = (int) tableModel.getValueAt(modelRow, 0);
        isEditMode = true;
        
        Appointment appointment = appointmentDAO.getAppointmentById(selectedAppointmentId);
        if (appointment != null) {
            loadComboBoxes();
            
            String studentDisplay = appointment.getStudentNumber() + " - " + appointment.getStudentName();
            cmbStudent.setSelectedItem(studentDisplay);
            
            String targetStaff = appointment.getStaffName() + " (" + appointment.getStaffProfession() + ")";
            for (int i = 0; i < cmbStaff.getItemCount(); i++) {
                if (cmbStaff.getItemAt(i).equals(targetStaff)) {
                    cmbStaff.setSelectedIndex(i);
                    break;
                }
            }
            
            if (appointment.getAppointmentDate() != null) {
                String dateStr = sdf.format(appointment.getAppointmentDate());
                String[] parts = dateStr.split(" ");
                if (parts.length == 2) {
                    txtDateTime.setText(parts[0]);
                    String[] timeParts = parts[1].split(":");
                    if (timeParts.length >= 2) {
                        cmbTimeHour.setSelectedItem(timeParts[0]);
                        cmbTimeMinute.setSelectedItem(timeParts[1]);
                    }
                }
            }
            
            cmbStatus.setSelectedItem(appointment.getStatusName() != null ? appointment.getStatusName() : "Pending");
            
            formPanel.setVisible(true);
            btnSave.setText("Update Appointment");
            btnSave.setIcon(IconLoader.loadIcon("edit", 16, 16));
        }
    }
    
    private void selectAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = appointmentTable.convertRowIndexToModel(selectedRow);
            selectedAppointmentId = (int) tableModel.getValueAt(modelRow, 0);
        }
    }
    
    private void deleteSelectedAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to cancel.");
            return;
        }
        int modelRow = appointmentTable.convertRowIndexToModel(selectedRow);
        int appointmentId = (int) tableModel.getValueAt(modelRow, 0);
        
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to cancel this appointment?", "Confirm Cancel", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (appointmentDAO.cancelAppointment(appointmentId)) {
                JOptionPane.showMessageDialog(this, "Appointment cancelled successfully!");
                loadAppointments();
                hideForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to cancel appointment.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void saveAppointment() {
        String studentStr = (String) cmbStudent.getSelectedItem();
        String staffStr = (String) cmbStaff.getSelectedItem();
        String dateStr = txtDateTime.getText().trim();
        String hour = (String) cmbTimeHour.getSelectedItem();
        String minute = (String) cmbTimeMinute.getSelectedItem();
        String statusStr = (String) cmbStatus.getSelectedItem();
        
        if (studentStr == null || staffStr == null || dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student, Staff, and Date are required.");
            return;
        }
        
        int studentUserId = -1;
        for (Student s : studentDAO.getAllStudents()) {
            if ((s.getStudentNumber() + " - " + s.getFullName()).equals(studentStr)) {
                studentUserId = s.getUserId();
                break;
            }
        }
        
        int staffId = -1;
        for (Staff s : staffDAO.getAllStaff()) {
            if ((s.getFullName() + " (" + s.getProfession() + ")").equals(staffStr)) {
                staffId = s.getStaffId();
                break;
            }
        }
        
        int statusId = 1;
        switch(statusStr) {
            case "Pending": statusId = 1; break;
            case "Approved": statusId = 2; break;
            case "Completed": statusId = 3; break;
            case "Cancelled": statusId = 4; break;
        }
        
        // Combine date and time
        String dateTimeStr = dateStr + " " + hour + ":" + minute + ":00";
        Timestamp sqlTimestamp;
        try {
            sqlTimestamp = Timestamp.valueOf(dateTimeStr);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Invalid date/time format. Use YYYY-MM-DD.");
            return;
        }
        
        Appointment appointment = new Appointment();
        appointment.setStudentUserId(studentUserId);
        appointment.setStaffId(staffId);
        appointment.setAppointmentDate(sqlTimestamp);
        appointment.setStatusId(statusId);
        
        boolean success;
        if (isEditMode) {
            appointment.setAppointmentId(selectedAppointmentId);
            success = appointmentDAO.updateAppointment(appointment);
            if (success) JOptionPane.showMessageDialog(this, "Appointment updated successfully!");
        } else {
            success = appointmentDAO.bookAppointment(appointment);
            if (success) JOptionPane.showMessageDialog(this, "Appointment booked successfully!");
        }
        
        if (success) {
            SwingUtilities.invokeLater(() -> { loadAppointments(); hideForm(); });
        } else {
            JOptionPane.showMessageDialog(this, "Operation failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearForm() {
        txtDateTime.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        cmbTimeHour.setSelectedIndex(0);
        cmbTimeMinute.setSelectedIndex(0);
        cmbStatus.setSelectedIndex(0);
    }
    
    private void hideForm() {
        formPanel.setVisible(false);
        clearForm();
        selectedAppointmentId = -1;
        isEditMode = false;
        appointmentTable.clearSelection();
    }
}