package ccams.views;

import ccams.models.Settings;
import ccams.services.SettingsService;
import ccams.constants.AppColors;
import ccams.utils.IconLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SettingsView extends JPanel {
    private SettingsService settingsService;
    private Settings settings;
    
    // General Settings
    private JTextField txtClinicName, txtClinicAddress, txtClinicPhone, txtClinicEmail, txtClinicHours;
    
    // Appointment Settings
    private JTextField txtAppointmentDuration, txtMaxAppointments, txtCancellationWindow, txtReminderHours;
    
    // Notification Settings
    private JCheckBox chkEmailNotifications, chkSMSNotifications, chkAppNotifications;
    
    // Security Settings
    private JCheckBox chkRequirePasswordChange, chkTwoFactorAuth;
    private JTextField txtPasswordExpiryDays, txtMaxLoginAttempts;
    
    private JButton btnSave, btnReset, btnBackup, btnRestore;
    
    public SettingsView() {
        settingsService = new SettingsService();
        settings = settingsService.getSettings();
        initComponents();
        loadSettings();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(15, 15));
        setBackground(AppColors.BG_LIGHT);
        setBorder(new EmptyBorder(0, 0, 0, 0));
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabbedPane.setBackground(Color.WHITE);
        
        // --- UPDATED: Using your new icons found in your Project pane ---
        addTabWithIcon(tabbedPane, "General", IconLoader.loadIcon("settings", 16, 16), createGeneralPanel());
        addTabWithIcon(tabbedPane, "Appointments", IconLoader.loadIcon("appointments", 16, 16), createAppointmentPanel());
        addTabWithIcon(tabbedPane, "Notifications", IconLoader.loadIcon("notification", 16, 16), createNotificationPanel());
        addTabWithIcon(tabbedPane, "Security", IconLoader.loadIcon("security", 16, 16), createSecurityPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
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
    
    private JPanel createGeneralPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(25, 30, 25, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        JLabel titleLabel = new JLabel("Clinic Information");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(AppColors.TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panel.add(createLabel("Clinic Name:"), gbc);
        gbc.gridx = 1;
        txtClinicName = createTextField(30);
        panel.add(txtClinicName, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(createLabel("Address:"), gbc);
        gbc.gridx = 1;
        txtClinicAddress = createTextField(30);
        panel.add(txtClinicAddress, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(createLabel("Phone:"), gbc);
        gbc.gridx = 1;
        txtClinicPhone = createTextField(30);
        panel.add(txtClinicPhone, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(createLabel("Email:"), gbc);
        gbc.gridx = 1;
        txtClinicEmail = createTextField(30);
        panel.add(txtClinicEmail, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel.add(createLabel("Operating Hours:"), gbc);
        gbc.gridx = 1;
        txtClinicHours = createTextField(30);
        panel.add(txtClinicHours, gbc);
        
        return panel;
    }
    
    private JPanel createAppointmentPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(25, 30, 25, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel titleLabel = new JLabel("Appointment Configuration");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(AppColors.TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        panel.add(createLabel("Appointment Duration (minutes):"), gbc);
        gbc.gridx = 1;
        txtAppointmentDuration = createTextField(15);
        panel.add(txtAppointmentDuration, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(createLabel("Max Appointments Per Day:"), gbc);
        gbc.gridx = 1;
        txtMaxAppointments = createTextField(15);
        panel.add(txtMaxAppointments, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(createLabel("Cancellation Window (hours):"), gbc);
        gbc.gridx = 1;
        txtCancellationWindow = createTextField(15);
        panel.add(txtCancellationWindow, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(createLabel("Reminder Hours Before Appointment:"), gbc);
        gbc.gridx = 1;
        txtReminderHours = createTextField(15);
        panel.add(txtReminderHours, gbc);
        
        return panel;
    }
    
    private JPanel createNotificationPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(25, 30, 25, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        JLabel titleLabel = new JLabel("Notification Settings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(AppColors.TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        chkEmailNotifications = new JCheckBox("Enable Email Notifications");
        chkEmailNotifications.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(chkEmailNotifications, gbc);
        
        gbc.gridy = 2;
        chkSMSNotifications = new JCheckBox("Enable SMS Notifications");
        chkSMSNotifications.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(chkSMSNotifications, gbc);
        
        gbc.gridy = 3;
        chkAppNotifications = new JCheckBox("Enable In-App Notifications");
        chkAppNotifications.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(chkAppNotifications, gbc);
        
        return panel;
    }
    
    private JPanel createSecurityPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(25, 30, 25, 30));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;
        
        JLabel titleLabel = new JLabel("Security Settings");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(AppColors.TEXT_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        chkRequirePasswordChange = new JCheckBox("Require Password Change");
        chkRequirePasswordChange.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(chkRequirePasswordChange, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(createLabel("Password Expiry (days):"), gbc);
        gbc.gridx = 1;
        txtPasswordExpiryDays = createTextField(15);
        panel.add(txtPasswordExpiryDays, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(createLabel("Max Login Attempts:"), gbc);
        gbc.gridx = 1;
        txtMaxLoginAttempts = createTextField(15);
        panel.add(txtMaxLoginAttempts, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        chkTwoFactorAuth = new JCheckBox("Enable Two-Factor Authentication");
        chkTwoFactorAuth.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(chkTwoFactorAuth, gbc);
        
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, AppColors.CARD_BORDER),
            new EmptyBorder(10, 0, 10, 0)
        ));
        
        // --- UPDATED: Using new icons and slightly wider buttons to prevent truncation ---
        btnSave = createStyledButton("Save Settings", AppColors.SUCCESS, "save");
        btnSave.addActionListener(e -> saveSettings());
        panel.add(btnSave);
        
        btnReset = createStyledButton("Reset to Default", new Color(255, 165, 0), "refresh");
        btnReset.addActionListener(e -> resetSettings());
        panel.add(btnReset);
        
        btnBackup = createStyledButton("Backup Settings", AppColors.ACCENT, "settings");
        btnBackup.addActionListener(e -> backupSettings());
        panel.add(btnBackup);
        
        btnRestore = createStyledButton("Restore Settings", new Color(123, 31, 162), "user");
        btnRestore.addActionListener(e -> restoreSettings());
        panel.add(btnRestore);
        
        return panel;
    }
    
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(AppColors.TEXT_SECONDARY);
        return label;
    }
    
    private JTextField createTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(AppColors.CARD_BORDER, 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        return field;
    }
    
    // Updated to include iconName and ensure icon and text don't overlap
    private JButton createStyledButton(String text, Color bgColor, String iconName) {
        JButton button;
        if (iconName != null && !iconName.isEmpty()) {
            button = new JButton(text, IconLoader.loadIcon(iconName, 16, 16));
            button.setHorizontalTextPosition(SwingConstants.RIGHT); // Puts text to the right of the icon
        } else {
            button = new JButton(text);
        }
        
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // INCREASED WIDTH to 160 to stop the text from getting cut off
        button.setPreferredSize(new Dimension(160, 38)); 
        return button;
    }
    
    private void loadSettings() {
        txtClinicName.setText(settings.getClinicName());
        txtClinicAddress.setText(settings.getClinicAddress());
        txtClinicPhone.setText(settings.getClinicPhone());
        txtClinicEmail.setText(settings.getClinicEmail());
        txtClinicHours.setText(settings.getClinicHours());
        
        txtAppointmentDuration.setText(String.valueOf(settings.getAppointmentDuration()));
        txtMaxAppointments.setText(String.valueOf(settings.getMaxAppointmentsPerDay()));
        txtCancellationWindow.setText(String.valueOf(settings.getCancellationWindow()));
        txtReminderHours.setText(String.valueOf(settings.getReminderHours()));
        
        chkEmailNotifications.setSelected(settings.isEnableEmailNotifications());
        chkSMSNotifications.setSelected(settings.isEnableSMSNotifications());
        chkAppNotifications.setSelected(settings.isEnableAppNotifications());
        
        chkRequirePasswordChange.setSelected(settings.isRequirePasswordChange());
        txtPasswordExpiryDays.setText(String.valueOf(settings.getPasswordExpiryDays()));
        txtMaxLoginAttempts.setText(String.valueOf(settings.getMaxLoginAttempts()));
        chkTwoFactorAuth.setSelected(settings.isEnableTwoFactorAuth());
    }
    
    private void saveSettings() {
        try {
            settings.setClinicName(txtClinicName.getText().trim());
            settings.setClinicAddress(txtClinicAddress.getText().trim());
            settings.setClinicPhone(txtClinicPhone.getText().trim());
            settings.setClinicEmail(txtClinicEmail.getText().trim());
            settings.setClinicHours(txtClinicHours.getText().trim());
            
            settings.setAppointmentDuration(Integer.parseInt(txtAppointmentDuration.getText().trim()));
            settings.setMaxAppointmentsPerDay(Integer.parseInt(txtMaxAppointments.getText().trim()));
            settings.setCancellationWindow(Integer.parseInt(txtCancellationWindow.getText().trim()));
            settings.setReminderHours(Integer.parseInt(txtReminderHours.getText().trim()));
            
            settings.setEnableEmailNotifications(chkEmailNotifications.isSelected());
            settings.setEnableSMSNotifications(chkSMSNotifications.isSelected());
            settings.setEnableAppNotifications(chkAppNotifications.isSelected());
            
            settings.setRequirePasswordChange(chkRequirePasswordChange.isSelected());
            settings.setPasswordExpiryDays(Integer.parseInt(txtPasswordExpiryDays.getText().trim()));
            settings.setMaxLoginAttempts(Integer.parseInt(txtMaxLoginAttempts.getText().trim()));
            settings.setEnableTwoFactorAuth(chkTwoFactorAuth.isSelected());
            
            if (settingsService.saveSettings()) {
                JOptionPane.showMessageDialog(this, "Settings saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save settings.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for numeric fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void resetSettings() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to reset all settings to default values?",
            "Confirm Reset",
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            settingsService.resetToDefaults();
            settings = settingsService.getSettings();
            loadSettings();
            JOptionPane.showMessageDialog(this, "Settings reset to default values!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void backupSettings() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Backup Settings");
        fileChooser.setSelectedFile(new java.io.File("ccams_settings_backup.properties"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File source = new java.io.File("config/settings.properties");
                java.io.File dest = fileChooser.getSelectedFile();
                java.nio.file.Files.copy(source.toPath(), dest.toPath(), 
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                JOptionPane.showMessageDialog(this, "Settings backed up successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error backing up settings: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void restoreSettings() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Restore Settings");
        
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                java.io.File source = fileChooser.getSelectedFile();
                java.io.File dest = new java.io.File("config/settings.properties");
                java.nio.file.Files.copy(source.toPath(), dest.toPath(), 
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                settingsService.loadSettings();
                settings = settingsService.getSettings();
                loadSettings();
                JOptionPane.showMessageDialog(this, "Settings restored successfully!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error restoring settings: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}