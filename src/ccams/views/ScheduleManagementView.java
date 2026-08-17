package ccams.views;

import ccams.dao.ScheduleDAO;
import ccams.dao.StaffDAO;
import ccams.models.Schedule;
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
import java.sql.Date;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class ScheduleManagementView extends JPanel {
    private ScheduleDAO scheduleDAO;
    private StaffDAO staffDAO;
    private JTable scheduleTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextField searchField;
    private JComboBox<String> staffFilter;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh, btnSearch;
    private JPanel formPanel;
    
    // Form fields
    private JComboBox<String> cmbStaff;
    private JTextField txtDate;
    private JButton btnDatePicker;
    private JComboBox<String> cmbStartHour, cmbStartMinute, cmbEndHour, cmbEndMinute;
    private JCheckBox chkAvailable;
    private JButton btnSave, btnCancel;
    
    private int selectedScheduleId = -1;
    private boolean isEditMode = false;
    
    public ScheduleManagementView() {
        scheduleDAO = new ScheduleDAO();
        staffDAO = new StaffDAO();
        initComponents();
        loadStaffFilter(); 
        loadSchedules();
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
        
        // Search icon
        ImageIcon searchIcon = IconLoader.loadIcon("search", 16, 16);
        if (searchIcon != null) {
            panel.add(new JLabel(searchIcon));
        } else {
            panel.add(new JLabel("🔍"));
        }
        
        searchField = new JTextField(15);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        panel.add(searchField);
        
        btnSearch = createStyledButton("Search", AppColors.ACCENT, "search");
        btnSearch.addActionListener(e -> searchSchedules());
        panel.add(btnSearch);
        
        btnRefresh = createStyledButton("Refresh", new Color(100, 100, 100), "refresh");
        btnRefresh.addActionListener(e -> loadSchedules());
        panel.add(btnRefresh);
        
        panel.add(new JLabel("Filter Staff:"));
        staffFilter = new JComboBox<>();
        staffFilter.addItem("All");
        staffFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        staffFilter.addActionListener(e -> loadSchedules());
        panel.add(staffFilter);
        
        btnAdd = createStyledButton("Add Schedule", AppColors.SUCCESS, "add-schedule");
        btnAdd.addActionListener(e -> showAddForm());
        panel.add(btnAdd);
        
        return panel;
    }
    
    private void loadStaffFilter() {
        staffFilter.removeAllItems();
        staffFilter.addItem("All");
        List<Staff> staffList = staffDAO.getAllStaff();
        for (Staff s : staffList) {
            staffFilter.addItem(s.getFirstName() + " " + s.getLastName());
        }
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        String[] columns = {"ID", "Staff", "Date", "Start", "End", "Available"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        scheduleTable = new JTable(tableModel);
        scheduleTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        scheduleTable.setRowHeight(35);
        scheduleTable.setSelectionBackground(new Color(220, 235, 250));
        scheduleTable.setSelectionForeground(AppColors.TEXT_PRIMARY);
        scheduleTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        scheduleTable.getTableHeader().setBackground(AppColors.BG_LIGHT);
        scheduleTable.getTableHeader().setForeground(AppColors.TEXT_PRIMARY);
        scheduleTable.getTableHeader().setPreferredSize(new Dimension(0, 35));
        
        // Custom Header Renderer with Icons
        scheduleTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                ImageIcon icon = null;
                switch (column) {
                    case 0: icon = IconLoader.loadIcon("id", 14, 14); break;
                    case 1: icon = IconLoader.loadIcon("staff", 14, 14); break;
                    case 2: icon = IconLoader.loadIcon("date", 14, 14); break;
                    case 3: icon = IconLoader.loadIcon("schedules", 14, 14); break;
                    case 4: icon = IconLoader.loadIcon("schedules", 14, 14); break;
                    case 5: icon = IconLoader.loadIcon("status", 14, 14); break;
                    default: break;
                }
                
                if (icon != null) {
                    setIcon(icon);
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                setBackground(AppColors.BG_LIGHT);
                setFont(new Font("Segoe UI", Font.BOLD, 12));
                return this;
            }
        });
        
        rowSorter = new TableRowSorter<>(tableModel);
        scheduleTable.setRowSorter(rowSorter);
        
        scheduleTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedSchedule();
                } else {
                    selectSchedule();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppColors.CARD_BORDER, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Action Buttons with Icons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        btnEdit = createStyledButton("Edit", new Color(0, 120, 215), "edit");
        btnEdit.addActionListener(e -> editSelectedSchedule());
        buttonPanel.add(btnEdit);
        
        btnDelete = createStyledButton("Delete", AppColors.DANGER, "delete");
        btnDelete.addActionListener(e -> deleteSelectedSchedule());
        buttonPanel.add(btnDelete);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // ============================================================
    // FORM PANEL WITH DATE PICKER
    // ============================================================
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, AppColors.CARD_BORDER),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel formTitle = new JLabel("📅 Schedule Details");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(AppColors.TEXT_PRIMARY);
        panel.add(formTitle, BorderLayout.NORTH);
        
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Staff
        gbc.gridx = 0; gbc.gridy = row;
        fieldsPanel.add(createLabel("Staff:"), gbc);
        gbc.gridx = 1;
        cmbStaff = new JComboBox<>();
        cmbStaff.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbStaff.setPreferredSize(new Dimension(200, 30));
        fieldsPanel.add(cmbStaff, gbc);
        
        gbc.gridx = 2;
        fieldsPanel.add(createLabel("Date (YYYY-MM-DD):"), gbc);
        gbc.gridx = 3;
        
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        datePanel.setBackground(Color.WHITE);
        
        txtDate = new JTextField(12);
        txtDate.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDate.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        datePanel.add(txtDate);
        
        // Date Picker Button with Calendar Icon
        ImageIcon dateIcon = IconLoader.loadIcon("date", 20, 20);
        if (dateIcon != null) {
            btnDatePicker = new JButton(dateIcon);
        } else {
            btnDatePicker = new JButton("📅");
        }
        btnDatePicker.setBackground(Color.WHITE);
        btnDatePicker.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(6, 6, 6, 6)
        ));
        btnDatePicker.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDatePicker.setToolTipText("Select Date");
        btnDatePicker.addActionListener(e -> showDatePicker(txtDate));
        datePanel.add(btnDatePicker);
        
        fieldsPanel.add(datePanel, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        fieldsPanel.add(createLabel("Start Time:"), gbc);
        gbc.gridx = 1;
        JPanel startPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        startPanel.setBackground(Color.WHITE);
        cmbStartHour = createTimeCombo();
        startPanel.add(cmbStartHour);
        startPanel.add(new JLabel(" : "));
        cmbStartMinute = createMinuteCombo();
        startPanel.add(cmbStartMinute);
        fieldsPanel.add(startPanel, gbc);
        
        gbc.gridx = 2;
        fieldsPanel.add(createLabel("End Time:"), gbc);
        gbc.gridx = 3;
        JPanel endPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        endPanel.setBackground(Color.WHITE);
        cmbEndHour = createTimeCombo();
        endPanel.add(cmbEndHour);
        endPanel.add(new JLabel(" : "));
        cmbEndMinute = createMinuteCombo();
        endPanel.add(cmbEndMinute);
        fieldsPanel.add(endPanel, gbc);
        
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        fieldsPanel.add(createLabel("Available:"), gbc);
        gbc.gridx = 1;
        chkAvailable = new JCheckBox("Staff is available for bookings");
        chkAvailable.setBackground(Color.WHITE);
        chkAvailable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chkAvailable.setSelected(true);
        fieldsPanel.add(chkAvailable, gbc);
        
        panel.add(fieldsPanel, BorderLayout.CENTER);
        
        // Buttons with Icons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        btnSave = createStyledButton("Save", AppColors.SUCCESS, "save");
        btnSave.addActionListener(e -> saveSchedule());
        buttonPanel.add(btnSave);
        
        btnCancel = createStyledButton("Cancel", AppColors.DANGER, "cancel");
        btnCancel.addActionListener(e -> hideForm());
        buttonPanel.add(btnCancel);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // ============================================================
    // DATE PICKER METHODS
    // ============================================================
    
    private void showDatePicker(JTextField targetField) {
        // Get current date from text field
        String currentDate = targetField.getText().trim();
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
        
        // Header with Close Icon
        JPanel headerPanel = createDatePickerHeader(dateDialog);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Calendar body with Left/Right Icons
        JPanel calendarBody = createCalendarBody(cal, targetField, dateDialog);
        mainPanel.add(calendarBody, BorderLayout.CENTER);
        
        // Footer with Today/Clear Icons
        JPanel footerPanel = createDatePickerFooter(dateDialog, targetField);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        dateDialog.add(mainPanel);
        dateDialog.setVisible(true);
    }
    
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
            closeBtn.setBorderPainted(false);
            closeBtn.setContentAreaFilled(false);
        } else {
            closeBtn = new JButton("✕");
            closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
            closeBtn.setForeground(Color.WHITE);
            closeBtn.setBackground(AppColors.ACCENT);
        }
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dialog.dispose());
        panel.add(closeBtn, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createCalendarBody(Calendar cal, JTextField dateField, JDialog dialog) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Navigation Panel with Left/Right Icons
        JPanel navPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        navPanel.setBackground(Color.WHITE);
        
        // Left/Previous Button with Icon
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
        
        // Month/Year Label
        JLabel monthYearLabel = new JLabel();
        monthYearLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        monthYearLabel.setPreferredSize(new Dimension(150, 30));
        monthYearLabel.setHorizontalAlignment(SwingConstants.CENTER);
        navPanel.add(monthYearLabel);
        
        // Right/Next Button with Icon
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
        
        // Day buttons
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
        monthYearLabel.setText(String.format("%s %d", 
            new java.text.SimpleDateFormat("MMMM").format(cal.getTime()), 
            cal.get(Calendar.YEAR)));
        
        int firstDayOfMonth = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        
        Calendar today = Calendar.getInstance();
        int todayDay = today.get(Calendar.DAY_OF_MONTH);
        int todayMonth = today.get(Calendar.MONTH);
        int todayYear = today.get(Calendar.YEAR);
        
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
                
                if (currentDay == todayDay && cal.get(Calendar.MONTH) == todayMonth && cal.get(Calendar.YEAR) == todayYear) {
                    btn.setBackground(new Color(200, 230, 255));
                    btn.setBorder(BorderFactory.createLineBorder(new Color(0, 120, 215), 2));
                }
                
                if (currentDay == selectedDay && cal.get(Calendar.MONTH) == selectedMonth && cal.get(Calendar.YEAR) == selectedYear) {
                    btn.setBackground(AppColors.ACCENT);
                    btn.setForeground(Color.WHITE);
                    btn.setBorder(BorderFactory.createLineBorder(AppColors.ACCENT, 2));
                }
                
                for (ActionListener al : btn.getActionListeners()) {
                    btn.removeActionListener(al);
                }
                
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
    
    private JPanel createDatePickerFooter(JDialog dialog, JTextField targetField) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        // Today Button with Icon
        ImageIcon todayIcon = IconLoader.loadIcon("today", 16, 16);
        JButton todayBtn;
        if (todayIcon != null) {
            todayBtn = new JButton("Today", todayIcon);
            todayBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
            todayBtn.setIconTextGap(8);
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
        todayBtn.addActionListener(e -> {
            Calendar today = Calendar.getInstance();
            targetField.setText(String.format("%04d-%02d-%02d", 
                today.get(Calendar.YEAR), 
                today.get(Calendar.MONTH) + 1, 
                today.get(Calendar.DAY_OF_MONTH)));
            dialog.dispose();
        });
        panel.add(todayBtn);
        
        // Clear Button with Icon
        ImageIcon clearIcon = IconLoader.loadIcon("clear", 16, 16);
        JButton clearBtn;
        if (clearIcon != null) {
            clearBtn = new JButton("Clear", clearIcon);
            clearBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
            clearBtn.setIconTextGap(8);
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
        clearBtn.addActionListener(e -> {
            targetField.setText("");
            dialog.dispose();
        });
        panel.add(clearBtn);
        
        return panel;
    }
    
    private JComboBox<String> createTimeCombo() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setPreferredSize(new Dimension(60, 28));
        for (int i = 0; i < 24; i++) {
            combo.addItem(String.format("%02d", i));
        }
        return combo;
    }
    
    private JComboBox<String> createMinuteCombo() {
        JComboBox<String> combo = new JComboBox<>();
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setPreferredSize(new Dimension(60, 28));
        combo.addItem("00");
        combo.addItem("15");
        combo.addItem("30");
        combo.addItem("45");
        return combo;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(AppColors.TEXT_SECONDARY);
        return label;
    }
    
    private JButton createStyledButton(String text, Color bgColor, String iconName) {
        ImageIcon icon = IconLoader.loadIcon(iconName, 16, 16);
        JButton button;
        
        if (icon != null) {
            button = new JButton(text, icon);
            button.setHorizontalTextPosition(SwingConstants.RIGHT);
            button.setIconTextGap(8);
        } else {
            String emoji = getEmojiForIcon(iconName);
            button = new JButton(emoji + " " + text);
        }
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(140, 38));
        return button;
    }
    
    private String getEmojiForIcon(String iconName) {
        switch (iconName) {
            case "search": return "🔍";
            case "refresh": return "🔄";
            case "add-schedule": return "➕";
            case "edit": return "✏️";
            case "delete": return "🗑️";
            case "save": return "💾";
            case "cancel": return "❌";
            default: return "";
        }
    }
    
    private void loadStaffCombo() {
        java.awt.event.ActionListener[] listeners = cmbStaff.getActionListeners();
        for (java.awt.event.ActionListener al : listeners) {
            cmbStaff.removeActionListener(al);
        }

        cmbStaff.removeAllItems();
        List<Staff> staffList = staffDAO.getAllStaff();
        for (Staff s : staffList) {
            cmbStaff.addItem(s.getFirstName() + " " + s.getLastName());
        }

        for (java.awt.event.ActionListener al : listeners) {
            cmbStaff.addActionListener(al);
        }
    }
    
    // ============================================================
    // LOAD SCHEDULES WITH YES/NO ICONS (FIXED)
    // ============================================================
    private void loadSchedules() {
        tableModel.setRowCount(0);
        List<Schedule> schedules = scheduleDAO.getAllSchedules();
        
        String selectedStaff = (String) staffFilter.getSelectedItem();
        if (selectedStaff == null) {
            selectedStaff = "All";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        for (Schedule s : schedules) {
            if (!"All".equals(selectedStaff) && !s.getStaffName().equals(selectedStaff)) {
                continue;
            }
            
            String search = searchField.getText().trim().toLowerCase();
            if (!search.isEmpty()) {
                if (!s.getStaffName().toLowerCase().contains(search) && 
                    !s.getStaffProfession().toLowerCase().contains(search)) {
                    continue;
                }
            }
            
            String dateStr = s.getSpecificDate() != null ? s.getSpecificDate().toString() : "";
            String startStr = s.getStartTime() != null ? s.getStartTime().toString().substring(0, 5) : "";
            String endStr = s.getEndTime() != null ? s.getEndTime().toString().substring(0, 5) : "";
            
            // ============================================================
            // AVAILABILITY WITH ICONS (Colored HTML for Yes/No)
            // ============================================================
            String avail;
            if (s.isAvailable()) {
                avail = "<html><font color='green'>✅ Yes</font></html>";
            } else {
                avail = "<html><font color='red'>❌ No</font></html>";
            }
            
            tableModel.addRow(new Object[]{
                s.getScheduleId(),
                s.getStaffName(),
                dateStr,
                startStr,
                endStr,
                avail
            });
        }
        tableModel.fireTableDataChanged();
    }
    
    private void searchSchedules() {
        loadSchedules();
    }
    
    private void showAddForm() {
        isEditMode = false;
        selectedScheduleId = -1;
        clearForm();
        loadStaffCombo();
        formPanel.setVisible(true);
        btnSave.setText("💾 Add Schedule");
    }
    
    private void editSelectedSchedule() {
        int selectedRow = scheduleTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a schedule to edit.");
            return;
        }
        
        int modelRow = scheduleTable.convertRowIndexToModel(selectedRow);
        selectedScheduleId = (int) tableModel.getValueAt(modelRow, 0);
        isEditMode = true;
        
        Schedule schedule = scheduleDAO.getScheduleById(selectedScheduleId);
        if (schedule != null) {
            loadStaffCombo();
            cmbStaff.setSelectedItem(schedule.getStaffName());
            
            txtDate.setText(schedule.getSpecificDate() != null ? schedule.getSpecificDate().toString() : "");
            
            if (schedule.getStartTime() != null) {
                String[] parts = schedule.getStartTime().toString().split(":");
                cmbStartHour.setSelectedItem(parts[0]);
                cmbStartMinute.setSelectedItem(parts[1]);
            }
            
            if (schedule.getEndTime() != null) {
                String[] parts = schedule.getEndTime().toString().split(":");
                cmbEndHour.setSelectedItem(parts[0]);
                cmbEndMinute.setSelectedItem(parts[1]);
            }
            
            chkAvailable.setSelected(schedule.isAvailable());
            
            formPanel.setVisible(true);
            btnSave.setText("✏️ Update Schedule");
        }
    }
    
    private void selectSchedule() {
        int selectedRow = scheduleTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = scheduleTable.convertRowIndexToModel(selectedRow);
            selectedScheduleId = (int) tableModel.getValueAt(modelRow, 0);
        }
    }
    
    private void deleteSelectedSchedule() {
        int selectedRow = scheduleTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a schedule to delete.");
            return;
        }
        
        int modelRow = scheduleTable.convertRowIndexToModel(selectedRow);
        int scheduleId = (int) tableModel.getValueAt(modelRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this schedule?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (scheduleDAO.deleteSchedule(scheduleId)) {
                JOptionPane.showMessageDialog(this, "Schedule deleted successfully!");
                loadSchedules();
                hideForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete schedule.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void saveSchedule() {
        String staffName = (String) cmbStaff.getSelectedItem();
        String dateStr = txtDate.getText().trim();
        String startHour = (String) cmbStartHour.getSelectedItem();
        String startMin = (String) cmbStartMinute.getSelectedItem();
        String endHour = (String) cmbEndHour.getSelectedItem();
        String endMin = (String) cmbEndMinute.getSelectedItem();
        boolean available = chkAvailable.isSelected();
        
        if (staffName == null || dateStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Staff and Date are required.");
            return;
        }
        
        Date sqlDate;
        try {
            sqlDate = Date.valueOf(dateStr);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
            return;
        }
        
        Time startTime = Time.valueOf(startHour + ":" + startMin + ":00");
        Time endTime = Time.valueOf(endHour + ":" + endMin + ":00");
        
        int staffId = -1;
        List<Staff> staffList = staffDAO.getAllStaff();
        for (Staff s : staffList) {
            if ((s.getFirstName() + " " + s.getLastName()).equals(staffName)) {
                staffId = s.getStaffId();
                break;
            }
        }
        
        Schedule schedule = new Schedule();
        schedule.setStaffId(staffId);
        schedule.setSpecificDate(sqlDate);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);
        schedule.setAvailable(available);
        
        boolean success;
        
        if (isEditMode) {
            schedule.setScheduleId(selectedScheduleId);
            success = scheduleDAO.updateSchedule(schedule);
            if (success) {
                JOptionPane.showMessageDialog(this, "Schedule updated successfully!");
            }
        } else {
            success = scheduleDAO.addSchedule(schedule);
            if (success) {
                JOptionPane.showMessageDialog(this, "Schedule added successfully!");
            }
        }
        
        if (success) {
            SwingUtilities.invokeLater(() -> {
                loadSchedules();
                hideForm();
            });
        } else {
            JOptionPane.showMessageDialog(this, "Operation failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearForm() {
        txtDate.setText("");
        cmbStartHour.setSelectedIndex(0);
        cmbStartMinute.setSelectedIndex(0);
        cmbEndHour.setSelectedIndex(0);
        cmbEndMinute.setSelectedIndex(0);
        chkAvailable.setSelected(true);
    }
    
    private void hideForm() {
        formPanel.setVisible(false);
        clearForm();
        selectedScheduleId = -1;
        isEditMode = false;
        scheduleTable.clearSelection();
    }
}