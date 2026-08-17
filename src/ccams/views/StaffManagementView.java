package ccams.views;

import ccams.dao.StaffDAO;
import ccams.models.Staff;
import ccams.constants.AppColors;
import ccams.utils.IconLoader; // <--- ADDED THIS IMPORT

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class StaffManagementView extends JPanel {
    private StaffDAO staffDAO;
    private JTable staffTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> rowSorter;
    private JTextField searchField;
    private JComboBox<String> professionFilter;
    private JButton btnAdd, btnEdit, btnDelete, btnRefresh, btnSearch;
    private JPanel formPanel;
    private JTextField txtFirstName, txtLastName, txtEmail, txtPhone;
    private JComboBox<String> cmbProfession;
    private JPasswordField txtPassword;
    private JButton btnSave, btnCancel;
    private int selectedStaffId = -1;
    private boolean isEditMode = false;
    
    public StaffManagementView() {
        staffDAO = new StaffDAO();
        initComponents();
        loadStaff();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(AppColors.BG_LIGHT);
        
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
        
        // Replaced emoji with Search Icon
        panel.add(new JLabel(IconLoader.loadIcon("search", 16, 16))); 
        searchField = new JTextField(15);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(createTextFieldBorder());
        panel.add(searchField);
        
        panel.add(new JLabel("Profession:"));
        professionFilter = new JComboBox<>(new String[]{"All", "NURSE", "PSYCHOLOGIST"});
        professionFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        professionFilter.addActionListener(e -> filterStaff());
        panel.add(professionFilter);
        
        btnSearch = createStyledButton("Search", AppColors.ACCENT, "search");
        btnSearch.addActionListener(e -> searchStaff());
        panel.add(btnSearch);
        
        // Replaced emoji with Refresh Icon
        btnRefresh = createStyledButton("Refresh", new Color(100, 100, 100), "refresh");
        btnRefresh.addActionListener(e -> {
            System.out.println("Refresh button clicked");
            loadStaff();
        });
        panel.add(btnRefresh);
        
        // Replaced emoji with Add Icon
        btnAdd = createStyledButton("Add Staff", AppColors.SUCCESS, "add-schedule");
        btnAdd.addActionListener(e -> showAddForm());
        panel.add(btnAdd);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Updated columns - removed Specialization and Available (not in database)
        String[] columns = {"ID", "Name", "Profession", "Email", "Phone", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        staffTable = new JTable(tableModel);
        staffTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        staffTable.setRowHeight(35);
        staffTable.setSelectionBackground(new Color(220, 235, 250));
        staffTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        staffTable.getTableHeader().setBackground(AppColors.BG_LIGHT);
        staffTable.getTableHeader().setPreferredSize(new Dimension(0, 35));
        
        // --- START: Custom Table Header Renderer for Icons ---
        staffTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                switch (column) {
                    case 0: setIcon(IconLoader.loadIcon("id", 14, 14)); break;       // ID (UPDATED)
                    case 1: setIcon(IconLoader.loadIcon("staff", 14, 14)); break;    // Name
                    case 2: setIcon(IconLoader.loadIcon("settings", 14, 14)); break; // Profession
                    case 3: setIcon(IconLoader.loadIcon("email", 14, 14)); break;    // Email (UPDATED)
                    case 4: setIcon(IconLoader.loadIcon("phone", 14, 14)); break;    // Phone (UPDATED)
                    case 5: setIcon(IconLoader.loadIcon("status", 14, 14)); break;   // Status (UPDATED)
                    default: setIcon(null);
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                setBackground(AppColors.BG_LIGHT);
                return this;
            }
        });
        // --- END: Custom Table Header Renderer ---
        
        rowSorter = new TableRowSorter<>(tableModel);
        staffTable.setRowSorter(rowSorter);
        
        staffTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedStaff();
                } else {
                    selectStaff();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(staffTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(AppColors.CARD_BORDER, 1));
        panel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        // Replaced emoji with Edit Icon
        btnEdit = createStyledButton("Edit", new Color(0, 120, 215), "edit");
        btnEdit.addActionListener(e -> editSelectedStaff());
        buttonPanel.add(btnEdit);
        
        // Replaced emoji with Delete Icon
        btnDelete = createStyledButton("Delete", AppColors.DANGER, "delete");
        btnDelete.addActionListener(e -> deleteSelectedStaff());
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
        
        JLabel formTitle = new JLabel("Staff Details");
        formTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        formTitle.setForeground(AppColors.TEXT_PRIMARY);
        panel.add(formTitle, BorderLayout.NORTH);
        
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        int row = 0;
        
        // Row 1
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
        
        // Row 2
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
        
        // Row 3
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        fieldsPanel.add(createLabel("Profession:"), gbc);
        gbc.gridx = 1;
        cmbProfession = new JComboBox<>(new String[]{"NURSE", "PSYCHOLOGIST"});
        cmbProfession.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbProfession.setBorder(createTextFieldBorder());
        fieldsPanel.add(cmbProfession, gbc);
        
        gbc.gridx = 2;
        fieldsPanel.add(createLabel("Password:"), gbc);
        gbc.gridx = 3;
        txtPassword = new JPasswordField(15);
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBorder(createTextFieldBorder());
        fieldsPanel.add(txtPassword, gbc);
        
        panel.add(fieldsPanel, BorderLayout.CENTER);
        
        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        // Replaced emoji with Save Icon
        btnSave = createStyledButton("Save", AppColors.SUCCESS, "save");
        btnSave.addActionListener(e -> saveStaff());
        buttonPanel.add(btnSave);
        
        // Replaced emoji with Cancel Icon
        btnCancel = createStyledButton("Cancel", AppColors.DANGER, "cancelled"); // Using cancelled.png as cancel icon
        btnCancel.addActionListener(e -> hideForm());
        buttonPanel.add(btnCancel);
        
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
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
    
    // Updated to accept an icon name and position correctly
    private JButton createStyledButton(String text, Color bgColor, String iconName) {
        JButton button;
        if (iconName != null && !iconName.isEmpty()) {
            button = new JButton(text, IconLoader.loadIcon(iconName, 16, 16));
            button.setHorizontalTextPosition(SwingConstants.RIGHT); // Puts text right of icon
            button.setIconTextGap(8); // Adds a gap between icon and text
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
    
    private void loadStaff() {
        System.out.println("=== loadStaff() called ===");
        tableModel.setRowCount(0);
        
        List<Staff> staffList = staffDAO.getAllStaff();
        
        System.out.println("Staff loaded from database: " + staffList.size() + " records");
        for (Staff s : staffList) {
            System.out.println("  - ID: " + s.getStaffId() + ", Name: " + s.getFullName() + ", Profession: " + s.getProfession());
        }
        
        for (Staff s : staffList) {
            // Replaced text emojis with HTML color codes for cleaner UI
            String status = s.isActive() ? "<html><font color='green'>● Active</font></html>" : "<html><font color='red'>● Inactive</font></html>";
            
            tableModel.addRow(new Object[]{
                s.getStaffId(),
                s.getFullName(),
                s.getProfession(),
                s.getEmail(),
                s.getPhone() != null ? s.getPhone() : "",
                status
            });
        }
        
        System.out.println("Table rows added: " + tableModel.getRowCount());
        System.out.println("=== loadStaff() finished ===");
    }
    
    private void filterStaff() {
        String profession = (String) professionFilter.getSelectedItem();
        if (profession == null || profession.equals("All")) {
            loadStaff();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Staff> staffList = staffDAO.getStaffByProfession(profession);
        for (Staff s : staffList) {
            String status = s.isActive() ? "<html><font color='green'>● Active</font></html>" : "<html><font color='red'>● Inactive</font></html>";
            tableModel.addRow(new Object[]{
                s.getStaffId(),
                s.getFullName(),
                s.getProfession(),
                s.getEmail(),
                s.getPhone() != null ? s.getPhone() : "",
                status
            });
        }
    }
    
    private void searchStaff() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadStaff();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Staff> staffList = staffDAO.searchStaff(searchTerm);
        for (Staff s : staffList) {
            String status = s.isActive() ? "<html><font color='green'>● Active</font></html>" : "<html><font color='red'>● Inactive</font></html>";
            tableModel.addRow(new Object[]{
                s.getStaffId(),
                s.getFullName(),
                s.getProfession(),
                s.getEmail(),
                s.getPhone() != null ? s.getPhone() : "",
                status
            });
        }
    }
    
    private void showAddForm() {
        isEditMode = false;
        selectedStaffId = -1;
        clearForm();
        txtPassword.setVisible(true);
        formPanel.setVisible(true);
        btnSave.setText("Add Staff");
        btnSave.setIcon(IconLoader.loadIcon("add-schedule", 16, 16)); // Update icon
        txtFirstName.requestFocus();
    }
    
    private void editSelectedStaff() {
        int selectedRow = staffTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a staff member to edit.");
            return;
        }
        
        int modelRow = staffTable.convertRowIndexToModel(selectedRow);
        selectedStaffId = (int) tableModel.getValueAt(modelRow, 0);
        isEditMode = true;
        
        Staff staff = staffDAO.getStaffById(selectedStaffId);
        if (staff != null) {
            txtFirstName.setText(staff.getFirstName());
            txtLastName.setText(staff.getLastName());
            txtEmail.setText(staff.getEmail());
            txtEmail.setEnabled(false);
            txtPhone.setText(staff.getPhone());
            cmbProfession.setSelectedItem(staff.getProfession());
            txtPassword.setText("");
            txtPassword.setVisible(false);
            
            formPanel.setVisible(true);
            btnSave.setText("Update Staff");
            btnSave.setIcon(IconLoader.loadIcon("edit", 16, 16)); // Update icon
        }
    }
    
    private void selectStaff() {
        int selectedRow = staffTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = staffTable.convertRowIndexToModel(selectedRow);
            selectedStaffId = (int) tableModel.getValueAt(modelRow, 0);
        }
    }
    
    private void deleteSelectedStaff() {
        int selectedRow = staffTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a staff member to delete.");
            return;
        }
        
        int modelRow = staffTable.convertRowIndexToModel(selectedRow);
        int staffId = (int) tableModel.getValueAt(modelRow, 0);
        String staffName = (String) tableModel.getValueAt(modelRow, 1);
        
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete staff member: " + staffName + "?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            if (staffDAO.deleteStaff(staffId)) {
                JOptionPane.showMessageDialog(this, "Staff member deleted successfully!");
                loadStaff();
                hideForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete staff member.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void saveStaff() {
        String firstName = txtFirstName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String profession = (String) cmbProfession.getSelectedItem();
        String password = new String(txtPassword.getPassword());
        
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "First Name, Last Name, and Email are required.");
            return;
        }
        
        if (!isEditMode && password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password is required for new staff.");
            return;
        }
        
        if (!isEditMode && staffDAO.emailExists(email)) {
            JOptionPane.showMessageDialog(this, "Email already exists!");
            return;
        }
        
        Staff staff = new Staff();
        staff.setFirstName(firstName);
        staff.setLastName(lastName);
        staff.setEmail(email);
        staff.setPhone(phone);
        staff.setProfession(profession);
        staff.setActive(true);
        
        boolean success;
        
        if (isEditMode) {
            staff.setStaffId(selectedStaffId);
            success = staffDAO.updateStaff(staff);
            if (success) {
                JOptionPane.showMessageDialog(this, "Staff updated successfully!");
            }
        } else {
            success = staffDAO.addStaff(staff, password);
            if (success) {
                JOptionPane.showMessageDialog(this, "Staff added successfully!");
            }
        }
        
        if (success) {
            loadStaff();
            hideForm();
        } else {
            JOptionPane.showMessageDialog(this, "Operation failed. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearForm() {
        txtFirstName.setText("");
        txtLastName.setText("");
        txtEmail.setText("");
        txtEmail.setEnabled(true);
        txtPhone.setText("");
        txtPassword.setText("");
        txtPassword.setVisible(true);
        cmbProfession.setSelectedIndex(0);
    }
    
    private void hideForm() {
        formPanel.setVisible(false);
        clearForm();
        selectedStaffId = -1;
        isEditMode = false;
        staffTable.clearSelection();
    }
}