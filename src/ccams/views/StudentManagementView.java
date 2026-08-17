package ccams.views;

import ccams.dao.StudentDAO;
import ccams.models.Student;
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
import java.util.Calendar;
import java.util.List;

public class StudentManagementView extends JPanel {
    private StudentDAO studentDAO;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextField searchField;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh, btnSearch;
    private JPanel formPanel;
    private JTextField txtStudentNumber, txtFirstName, txtLastName, txtEmail, txtPhone;
    private JTextField txtAddress, txtEmergencyContact, txtEmergencyPhone;
    private JTextField txtDateOfBirth;
    private JPasswordField txtPassword;
    private JButton btnSave, btnCancel;
    private JButton btnDatePicker;  // Date picker button
    private int selectedStudentId = -1;
    private boolean isEditMode = false;
    
    public StudentManagementView() {
        studentDAO = new StudentDAO();
        initComponents();
        loadStudents();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(AppColors.BG_LIGHT);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Top Panel - Search
        JPanel topPanel = createSearchPanel();
        add(topPanel, BorderLayout.NORTH);
        
        // Center Panel - Table
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);
        
        // Bottom Panel - Form
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
        
        // Search Icon
        ImageIcon searchIcon = IconLoader.loadIcon("search", 16, 16);
        if (searchIcon != null) {
            panel.add(new JLabel(searchIcon));
        } else {
            panel.add(new JLabel("🔍"));
        }
        
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        panel.add(searchField);
        
        btnSearch = createStyledButton("Search", AppColors.ACCENT, "search");
        btnSearch.addActionListener(e -> searchStudents());
        panel.add(btnSearch);
        
        btnRefresh = createStyledButton("Refresh", new Color(100, 100, 100), "refresh");
        btnRefresh.addActionListener(e -> loadStudents());
        panel.add(btnRefresh);
        
        btnAdd = createStyledButton("Add Student", AppColors.SUCCESS, "add-schedule");
        btnAdd.addActionListener(e -> showAddForm());
        panel.add(btnAdd);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Table
        String[] columns = {"ID", "Student #", "First Name", "Last Name", "Email", "Phone", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        studentTable = new JTable(tableModel);
        studentTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        studentTable.setRowHeight(35);
        studentTable.setSelectionBackground(new Color(220, 235, 250));
        studentTable.setSelectionForeground(AppColors.TEXT_PRIMARY);
        studentTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        studentTable.getTableHeader().setBackground(AppColors.BG_LIGHT);
        studentTable.getTableHeader().setForeground(AppColors.TEXT_PRIMARY);
        studentTable.getTableHeader().setPreferredSize(new Dimension(0, 35));
        
        // Custom Table Header Renderer with Icons
        studentTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                switch (column) {
                    case 0: setIcon(IconLoader.loadIcon("id", 14, 14)); break;
                    case 1: setIcon(IconLoader.loadIcon("settings", 14, 14)); break;
                    case 2: setIcon(IconLoader.loadIcon("students", 14, 14)); break;
                    case 3: setIcon(IconLoader.loadIcon("students", 14, 14)); break;
                    case 4: setIcon(IconLoader.loadIcon("email", 14, 14)); break;
                    case 5: setIcon(IconLoader.loadIcon("phone", 14, 14)); break;
                    case 6: setIcon(IconLoader.loadIcon("status", 14, 14)); break;
                    default: setIcon(null);
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                setBackground(AppColors.BG_LIGHT);
                return this;
            }
        });
        
        rowSorter = new TableRowSorter<>(tableModel);
        studentTable.setRowSorter(rowSorter);
        
        studentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedStudent();
                } else {
                    selectStudent();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppColors.CARD_BORDER, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Action Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        btnEdit = createStyledButton("Edit", new Color(0, 120, 215), "edit");
        btnEdit.addActionListener(e -> editSelectedStudent());
        buttonPanel.add(btnEdit);
        
        btnDelete = createStyledButton("Delete", AppColors.DANGER, "delete");
        btnDelete.addActionListener(e -> deleteSelectedStudent());
        buttonPanel.add(btnDelete);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    // ============================================================
    // CREATE FORM PANEL WITH ALL ICONS
    // ============================================================
    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, AppColors.CARD_BORDER),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        // Form Title with Icon
        JLabel formTitle = new JLabel("👨‍🎓 Student Details");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(AppColors.TEXT_PRIMARY);
        panel.add(formTitle, BorderLayout.NORTH);
        
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Row 1 - Student Number & Password
        gbc.gridx = 0; gbc.gridy = row;
        fieldsPanel.add(createLabel("Student Number:"), gbc);
        gbc.gridx = 1;
        txtStudentNumber = createTextField();
        fieldsPanel.add(txtStudentNumber, gbc);
        
        gbc.gridx = 2;
        fieldsPanel.add(createLabel("Password:"), gbc);
        gbc.gridx = 3;
        txtPassword = new JPasswordField(15);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBorder(createTextFieldBorder());
        fieldsPanel.add(txtPassword, gbc);
        
        // Row 2 - First Name & Last Name
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        fieldsPanel.add(createLabel("First Name:"), gbc);
        gbc.gridx = 1;
        txtFirstName = createTextField();
        fieldsPanel.add(txtFirstName, gbc);
        
        gbc.gridx = 2;
        fieldsPanel.add(createLabel("Last Name:"), gbc);
        gbc.gridx = 3;
        txtLastName = createTextField();
        fieldsPanel.add(txtLastName, gbc);
        
        // Row 3 - Email & Phone
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        fieldsPanel.add(createLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtEmail = createTextField();
        fieldsPanel.add(txtEmail, gbc);
        
        gbc.gridx = 2;
        fieldsPanel.add(createLabel("Phone:"), gbc);
        gbc.gridx = 3;
        txtPhone = createTextField();
        fieldsPanel.add(txtPhone, gbc);
        
        // Row 4 - Date of Birth with Date Picker & Address
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        fieldsPanel.add(createLabel("Date of Birth:"), gbc);
        gbc.gridx = 1;
        
        JPanel dobPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        dobPanel.setBackground(Color.WHITE);
        
        txtDateOfBirth = new JTextField(12);
        txtDateOfBirth.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtDateOfBirth.setBorder(createTextFieldBorder());
        txtDateOfBirth.setToolTipText("Format: YYYY-MM-DD");
        dobPanel.add(txtDateOfBirth);
        
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
        btnDatePicker.setToolTipText("Select Date of Birth");
        btnDatePicker.addActionListener(e -> showDatePicker(txtDateOfBirth));
        dobPanel.add(btnDatePicker);
        
        fieldsPanel.add(dobPanel, gbc);
        
        gbc.gridx = 2;
        fieldsPanel.add(createLabel("Address:"), gbc);
        gbc.gridx = 3;
        txtAddress = createTextField();
        fieldsPanel.add(txtAddress, gbc);
        
        // Row 5 - Emergency Contact & Emergency Phone
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        fieldsPanel.add(createLabel("Emergency Contact:"), gbc);
        gbc.gridx = 1;
        txtEmergencyContact = createTextField();
        fieldsPanel.add(txtEmergencyContact, gbc);
        
        gbc.gridx = 2;
        fieldsPanel.add(createLabel("Emergency Phone:"), gbc);
        gbc.gridx = 3;
        txtEmergencyPhone = createTextField();
        fieldsPanel.add(txtEmergencyPhone, gbc);
        
        panel.add(fieldsPanel, BorderLayout.CENTER);
        
        // Buttons with Icons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        btnSave = createStyledButton("Save", AppColors.SUCCESS, "save");
        btnSave.addActionListener(e -> saveStudent());
        buttonPanel.add(btnSave);
        
        btnCancel = createStyledButton("Cancel", AppColors.DANGER, "cancelled");
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
        JDialog dateDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Select Date of Birth", true);
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
        
        // Title with Icon
        JLabel titleLabel = new JLabel("📅 Select Date of Birth");
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
            closeBtn.setBorderPainted(false);
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
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(AppColors.TEXT_SECONDARY);
        return label;
    }
    
    private JTextField createTextField() {
        JTextField field = new JTextField(15);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(createTextFieldBorder());
        return field;
    }
    
    private javax.swing.border.Border createTextFieldBorder() {
        return BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        );
    }
    
    private JButton createStyledButton(String text, Color bgColor, String iconName) {
        JButton button;
        if (iconName != null && !iconName.isEmpty()) {
            ImageIcon icon = IconLoader.loadIcon(iconName, 16, 16);
            if (icon != null) {
                button = new JButton(text, icon);
            } else {
                String emoji = getEmojiForIcon(iconName);
                button = new JButton(emoji + " " + text);
            }
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
        button.setPreferredSize(new Dimension(130, 35));
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
            case "cancelled": return "❌";
            default: return "";
        }
    }
    
    private void loadStudents() {
        tableModel.setRowCount(0);
        tableModel.fireTableDataChanged();
        
        List<Student> students = studentDAO.getAllStudents();
        System.out.println("Loaded " + students.size() + " students from database.");
        
        for (Student s : students) {
            String status = s.isActive() ? "<html><font color='green'>● Active</font></html>" : "<html><font color='red'>● Inactive</font></html>";
            tableModel.addRow(new Object[]{
                s.getStudentId(),
                s.getStudentNumber(),
                s.getFirstName(),
                s.getLastName(),
                s.getEmail(),
                s.getPhone(),
                status
            });
        }
        tableModel.fireTableDataChanged();
        studentTable.repaint();
        studentTable.revalidate();
    }
    
    private void searchStudents() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadStudents();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Student> students = studentDAO.searchStudents(searchTerm);
        for (Student s : students) {
            String status = s.isActive() ? "<html><font color='green'>● Active</font></html>" : "<html><font color='red'>● Inactive</font></html>";
            tableModel.addRow(new Object[]{
                s.getStudentId(),
                s.getStudentNumber(),
                s.getFirstName(),
                s.getLastName(),
                s.getEmail(),
                s.getPhone(),
                status
            });
        }
        tableModel.fireTableDataChanged();
        studentTable.repaint();
        studentTable.revalidate();
    }
    
    private void showAddForm() {
        isEditMode = false;
        selectedStudentId = -1;
        clearForm();
        txtPassword.setVisible(true);
        formPanel.setVisible(true);
        btnSave.setText("Add Student");
        btnSave.setIcon(IconLoader.loadIcon("add-schedule", 16, 16));
        txtStudentNumber.requestFocus();
    }
    
    private void editSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to edit.");
            return;
        }
        
        int modelRow = studentTable.convertRowIndexToModel(selectedRow);
        selectedStudentId = (int) tableModel.getValueAt(modelRow, 0);
        isEditMode = true;
        
        Student student = studentDAO.getStudentById(selectedStudentId);
        if (student != null) {
            txtStudentNumber.setText(student.getStudentNumber());
            txtStudentNumber.setEnabled(false);
            txtFirstName.setText(student.getFirstName());
            txtLastName.setText(student.getLastName());
            txtEmail.setText(student.getEmail());
            txtPhone.setText(student.getPhone());
            txtDateOfBirth.setText(student.getDateOfBirth() != null ? student.getDateOfBirth().toString() : "");
            txtAddress.setText(student.getAddress());
            txtEmergencyContact.setText(student.getEmergencyContact());
            txtEmergencyPhone.setText(student.getEmergencyPhone());
            txtPassword.setText("");
            txtPassword.setVisible(false);
            
            formPanel.setVisible(true);
            btnSave.setText("Update Student");
            btnSave.setIcon(IconLoader.loadIcon("edit", 16, 16));
        }
    }
    
    private void selectStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = studentTable.convertRowIndexToModel(selectedRow);
            selectedStudentId = (int) tableModel.getValueAt(modelRow, 0);
        }
    }
    
    private void deleteSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.");
            return;
        }
        
        int modelRow = studentTable.convertRowIndexToModel(selectedRow);
        int studentId = (int) tableModel.getValueAt(modelRow, 0);
        String studentName = (String) tableModel.getValueAt(modelRow, 2) + " " + 
                             (String) tableModel.getValueAt(modelRow, 3);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete student: " + studentName + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (studentDAO.deleteStudent(studentId)) {
                JOptionPane.showMessageDialog(this, "Student deleted successfully!");
                loadStudents();
                hideForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete student.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void saveStudent() {
        String studentNumber = txtStudentNumber.getText().trim();
        String firstName = txtFirstName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String dobStr = txtDateOfBirth.getText().trim();
        String address = txtAddress.getText().trim();
        String emergencyContact = txtEmergencyContact.getText().trim();
        String emergencyPhone = txtEmergencyPhone.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        if (studentNumber.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Student Number, First Name, and Last Name are required.");
            return;
        }
        
        if (!isEditMode && password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password is required for new students.");
            return;
        }
        
        if (!isEditMode && studentDAO.studentNumberExists(studentNumber)) {
            JOptionPane.showMessageDialog(this, "Student number already exists!");
            return;
        }
        
        Date dob = null;
        if (!dobStr.isEmpty()) {
            try {
                dob = Date.valueOf(dobStr);
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this, "Invalid date format. Use YYYY-MM-DD.");
                return;
            }
        }
        
        Student student = new Student();
        student.setStudentNumber(studentNumber);
        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setEmail(email);
        student.setPhone(phone);
        student.setDateOfBirth(dob);
        student.setAddress(address);
        student.setEmergencyContact(emergencyContact);
        student.setEmergencyPhone(emergencyPhone);
        
        boolean success;
        
        if (isEditMode) {
            student.setStudentId(selectedStudentId);
            success = studentDAO.updateStudent(student);
            if (success) {
                JOptionPane.showMessageDialog(this, "Student updated successfully!");
            }
        } else {
            success = studentDAO.registerStudent(student, password);
            if (success) {
                JOptionPane.showMessageDialog(this, "Student registered successfully!");
            }
        }
        
        if (success) {
            new Timer(300, e -> {
                loadStudents();
                hideForm();
                ((Timer)e.getSource()).stop();
            }).start();
        } else {
            JOptionPane.showMessageDialog(this, "Operation failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearForm() {
        txtStudentNumber.setText("");
        txtStudentNumber.setEnabled(true);
        txtFirstName.setText("");
        txtLastName.setText("");
        txtEmail.setText("");
        txtPhone.setText("");
        txtDateOfBirth.setText("");
        txtAddress.setText("");
        txtEmergencyContact.setText("");
        txtEmergencyPhone.setText("");
        txtPassword.setText("");
        txtPassword.setVisible(true);
    }
    
    private void hideForm() {
        formPanel.setVisible(false);
        clearForm();
        selectedStudentId = -1;
        isEditMode = false;
        studentTable.clearSelection();
    }
}