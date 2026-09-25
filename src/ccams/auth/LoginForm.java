package ccams.auth;

import ccams.dao.UserDAO;
import ccams.models.User;
import ccams.views.DashboardView;
import ccams.utils.ThemeManager;
import ccams.utils.IconLoader;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;

public class LoginForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;
    private UserDAO userDAO;
    private JCheckBox showPasswordCheckBox;
    
    // Placeholder text
    private final String USERNAME_PLACEHOLDER = "Enter username, student number or email";
    private final String PASSWORD_PLACEHOLDER = "Enter your password";
    
    // Background image
    private Image backgroundImage;
    
    public LoginForm() {
        setTitle("CCAMS - Campus Clinic Appointment Management System");
        setSize(680, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setUndecorated(true);
        
        // Load background image
        loadBackgroundImage();
        
        userDAO = new UserDAO();
        initComponents();
        updateLoginButtonState();
    }
    
    // ============================================================
    // LOAD BACKGROUND IMAGE - FIXED PATH
    // ============================================================
    private void loadBackgroundImage() {
        try {
            // Look in ccams.images package (where you placed it)
            URL url = getClass().getResource("/ccams/images/spu_campus.png");
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                backgroundImage = icon.getImage().getScaledInstance(680, 680, Image.SCALE_SMOOTH);
                System.out.println("✅ SPU Campus image loaded successfully from ccams.images!");
                return;
            }
            
            // Fallback to resources/images
            url = getClass().getResource("/resources/images/spu_campus.png");
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                backgroundImage = icon.getImage().getScaledInstance(680, 680, Image.SCALE_SMOOTH);
                System.out.println("✅ SPU Campus image loaded successfully from resources.images!");
                return;
            }
            
            // Try .jpg extension
            url = getClass().getResource("/ccams/images/spu_campus.jpg");
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                backgroundImage = icon.getImage().getScaledInstance(680, 680, Image.SCALE_SMOOTH);
                System.out.println("✅ SPU Campus image loaded successfully! (JPG)");
                return;
            }
            
            System.out.println("⚠️ SPU Campus image not found. Using default background.");
            
        } catch (Exception e) {
            System.out.println("⚠️ Could not load SPU Campus image: " + e.getMessage());
        }
    }
    
    private void initComponents() {
        // ============================================================
        // MAIN PANEL WITH BACKGROUND IMAGE
        // ============================================================
        JPanel mainPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                } else {
                    // Fallback gradient background
                    Graphics2D g2d = (Graphics2D) g;
                    GradientPaint gp = new GradientPaint(
                        0, 0, new Color(0, 80, 160),
                        0, getHeight(), new Color(26, 35, 53)
                    );
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ThemeManager.ACCENT, 2),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        
        // ============================================================
        // SEMI-TRANSPARENT OVERLAY PANEL
        // ============================================================
        JPanel overlayPanel = new JPanel(new BorderLayout());
        overlayPanel.setOpaque(false);
        
        // Header Panel (transparent)
        JPanel headerPanel = createHeaderPanel();
        overlayPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Center Panel (semi-transparent background)
        JPanel centerPanel = createLoginFormPanel();
        overlayPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Footer Panel (transparent)
        JPanel footerPanel = createFooterPanel();
        overlayPanel.add(footerPanel, BorderLayout.SOUTH);
        
        mainPanel.add(overlayPanel, BorderLayout.CENTER);
        
        add(mainPanel);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 150));
        
        // Close Button
        ImageIcon exitIcon = IconLoader.loadIcon("exit", 22, 22);
        JButton closeButton;
        if (exitIcon != null) {
            closeButton = new JButton(exitIcon);
        } else {
            closeButton = new JButton("✕");
            closeButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
            closeButton.setForeground(Color.WHITE);
        }
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setFocusPainted(false);
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeButton.setMargin(new Insets(0, 0, 0, 0));
        closeButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));
        closeButton.addActionListener(e -> System.exit(0));
        panel.add(closeButton, BorderLayout.EAST);
        
        // Logo and title
        JPanel titlePanel = new JPanel(new GridBagLayout());
        titlePanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        
        // Clinic Icon
        ImageIcon clinicIcon = IconLoader.loadIcon("clinic", 55, 55);
        JLabel iconLabel;
        if (clinicIcon != null) {
            iconLabel = new JLabel(clinicIcon);
        } else {
            iconLabel = new JLabel("🏥");
            iconLabel.setFont(new Font("Segoe UI", Font.PLAIN, 52));
        }
        iconLabel.setOpaque(false);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 15));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.insets = new Insets(0, 30, 0, 15);
        titlePanel.add(iconLabel, gbc);
        
        JLabel titleLabel = new JLabel("CCAMS");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 34));
        titleLabel.setForeground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        titlePanel.add(titleLabel, gbc);
        
        JLabel subtitleLabel = new JLabel("Campus Clinic Appointment Management System");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(255, 255, 255, 220));
        gbc.gridy = 1;
        titlePanel.add(subtitleLabel, gbc);
        
        panel.add(titlePanel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createLoginFormPanel() {
        // ============================================================
        // CREATE SEMI-TRANSPARENT PANEL FOR LOGIN FORM
        // ============================================================
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        
        // Add a semi-transparent background behind the form
        JPanel formBackground = new JPanel(new GridBagLayout());
        formBackground.setOpaque(true);
        formBackground.setBackground(new Color(255, 255, 255, 220));
        formBackground.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 150), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Welcome Label with icon
        ImageIcon userIcon = IconLoader.loadIcon("user", 30, 30);
        JLabel welcomeLabel;
        if (userIcon != null) {
            welcomeLabel = new JLabel("Welcome Back!", userIcon, SwingConstants.CENTER);
        } else {
            welcomeLabel = new JLabel("👋 Welcome Back!");
        }
        welcomeLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
        
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcomeLabel.setForeground(ThemeManager.PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        formBackground.add(welcomeLabel, gbc);
        
        JLabel descLabel = new JLabel("Please login to your account");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        descLabel.setForeground(Color.GRAY);
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 10, 25, 10);
        formBackground.add(descLabel, gbc);
        
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(8, 10, 8, 10);
        
        // Username Field
        gbc.gridy = 2;
        gbc.gridx = 0;
        ImageIcon usernameIcon = IconLoader.loadIcon("username", 24, 24);
        if (usernameIcon != null) {
            formBackground.add(new JLabel(usernameIcon), gbc);
        } else {
            formBackground.add(new JLabel("👤"), gbc);
        }
        
        gbc.gridx = 1;
        usernameField = new JTextField(35);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        usernameField.setPreferredSize(new Dimension(300, 45));
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        setPlaceholder(usernameField, USERNAME_PLACEHOLDER);
        usernameField.addKeyListener(new EnterKeyListener());
        usernameField.getDocument().addDocumentListener(new TextFieldListener());
        formBackground.add(usernameField, gbc);
        
        // Password Field
        gbc.gridx = 0;
        gbc.gridy = 3;
        ImageIcon passwordIcon = IconLoader.loadIcon("password", 24, 24);
        if (passwordIcon != null) {
            formBackground.add(new JLabel(passwordIcon), gbc);
        } else {
            formBackground.add(new JLabel("🔒"), gbc);
        }
        
        gbc.gridx = 1;
        passwordField = new JPasswordField(35);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        passwordField.setPreferredSize(new Dimension(300, 45));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        setPasswordPlaceholder(passwordField, PASSWORD_PLACEHOLDER);
        passwordField.addKeyListener(new EnterKeyListener());
        passwordField.getDocument().addDocumentListener(new TextFieldListener());
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(passwordField.getPassword()).equals(PASSWORD_PLACEHOLDER)) {
                    passwordField.setText("");
                    passwordField.setEchoChar('●');
                    passwordField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (passwordField.getPassword().length == 0) {
                    setPasswordPlaceholder(passwordField, PASSWORD_PLACEHOLDER);
                }
            }
        });
        formBackground.add(passwordField, gbc);
        
        // Show Password Checkbox
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.insets = new Insets(2, 10, 5, 10);
        gbc.anchor = GridBagConstraints.EAST;
        ImageIcon showIcon = IconLoader.loadIcon("show", 16, 16);
        if (showIcon != null) {
            showPasswordCheckBox = new JCheckBox("Show Password", showIcon);
        } else {
            showPasswordCheckBox = new JCheckBox("👁️ Show Password");
        }
        showPasswordCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        showPasswordCheckBox.setBackground(new Color(255, 255, 255, 0));
        showPasswordCheckBox.setForeground(Color.GRAY);
        showPasswordCheckBox.setCursor(new Cursor(Cursor.HAND_CURSOR));
        showPasswordCheckBox.addActionListener(e -> togglePasswordVisibility());
        formBackground.add(showPasswordCheckBox, gbc);
        
        // Status Label
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        statusLabel.setForeground(ThemeManager.DANGER);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 10, 5, 10);
        formBackground.add(statusLabel, gbc);
        
        // Login Button
        ImageIcon loginIcon = IconLoader.loadIcon("login", 22, 22);
        if (loginIcon != null) {
            loginButton = new JButton("Sign In", loginIcon);
        } else {
            loginButton = new JButton("🔐 Sign In");
        }
        loginButton.setHorizontalTextPosition(SwingConstants.RIGHT);
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 18));
        loginButton.setBackground(ThemeManager.ACCENT);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setPreferredSize(new Dimension(220, 50));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setEnabled(false);
        loginButton.addActionListener(e -> performLogin());
        gbc.gridy = 6;
        gbc.insets = new Insets(25, 10, 10, 10);
        formBackground.add(loginButton, gbc);
        
        // Forgot Password Section
        JPanel forgotPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        forgotPanel.setOpaque(false);
        
        ImageIcon forgotIcon = IconLoader.loadIcon("forgot", 16, 16);
        JLabel forgotLabel;
        if (forgotIcon != null) {
            forgotLabel = new JLabel("Forgot Password?", forgotIcon, SwingConstants.LEADING);
        } else {
            forgotLabel = new JLabel("🔑 Forgot Password?");
        }
        forgotLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        forgotLabel.setForeground(ThemeManager.ACCENT);
        forgotLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                showForgotPasswordDialog();
            }
            public void mouseEntered(MouseEvent evt) {
                forgotLabel.setForeground(new Color(0, 80, 180));
            }
            public void mouseExited(MouseEvent evt) {
                forgotLabel.setForeground(ThemeManager.ACCENT);
            }
        });
        forgotPanel.add(forgotLabel);
        
        JLabel separator = new JLabel("|");
        separator.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        separator.setForeground(Color.LIGHT_GRAY);
        forgotPanel.add(separator);
        
        ImageIcon emailIcon = IconLoader.loadIcon("email", 16, 16);
        JLabel emailLabel;
        if (emailIcon != null) {
            emailLabel = new JLabel("Email Support", emailIcon, SwingConstants.LEADING);
        } else {
            emailLabel = new JLabel("✉️ Email Support");
        }
        emailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailLabel.setForeground(ThemeManager.ACCENT);
        emailLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        emailLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                try {
                    Desktop.getDesktop().mail(new java.net.URI("mailto:support@ccams.com?subject=CCAMS%20Support%20Request"));
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(LoginForm.this, 
                        "📧 Email: support@ccams.com\n\nPlease send us an email for support.", 
                        "Email Support", 
                        JOptionPane.INFORMATION_MESSAGE);
                }
            }
            public void mouseEntered(MouseEvent evt) {
                emailLabel.setForeground(new Color(0, 80, 180));
            }
            public void mouseExited(MouseEvent evt) {
                emailLabel.setForeground(ThemeManager.ACCENT);
            }
        });
        forgotPanel.add(emailLabel);
        
        JLabel separator2 = new JLabel("|");
        separator2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        separator2.setForeground(Color.LIGHT_GRAY);
        forgotPanel.add(separator2);
        
        ImageIcon phoneIcon = IconLoader.loadIcon("phone", 16, 16);
        JLabel phoneLabel;
        if (phoneIcon != null) {
            phoneLabel = new JLabel("Phone Support", phoneIcon, SwingConstants.LEADING);
        } else {
            phoneLabel = new JLabel("📞 Phone Support");
        }
        phoneLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        phoneLabel.setForeground(ThemeManager.ACCENT);
        phoneLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        phoneLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JOptionPane.showMessageDialog(LoginForm.this, 
                    "📞 Phone: +27 53 123 4567\n\n" +
                    "🕐 Office Hours:\n" +
                    "Monday - Friday: 08:00 - 17:00\n" +
                    "Saturday: 09:00 - 13:00\n" +
                    "Sunday: Closed",
                    "Phone Support", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
            public void mouseEntered(MouseEvent evt) {
                phoneLabel.setForeground(new Color(0, 80, 180));
            }
            public void mouseExited(MouseEvent evt) {
                phoneLabel.setForeground(ThemeManager.ACCENT);
            }
        });
        forgotPanel.add(phoneLabel);
        
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(10, 10, 5, 10);
        formBackground.add(forgotPanel, gbc);
        
        // Add the form background to the main panel
        panel.add(formBackground, gbc);
        
        return panel;
    }
    
    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(0, 45));
        
        JLabel footerLabel = new JLabel("© 2024 Sol Plaatje University - CCAMS v1.0");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(255, 255, 255, 200));
        panel.add(footerLabel);
        
        return panel;
    }
    
    // ============================================================
    // FORGOT PASSWORD DIALOG
    // ============================================================
    private void showForgotPasswordDialog() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("🔑 Reset Password");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(ThemeManager.PRIMARY);
        panel.add(titleLabel, gbc);
        
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        JLabel infoLabel = new JLabel("Enter your email address to reset your password:");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        infoLabel.setForeground(Color.GRAY);
        panel.add(infoLabel, gbc);
        
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        JLabel emailIcon = new JLabel(IconLoader.loadIcon("email", 20, 20));
        if (emailIcon.getIcon() == null) {
            emailIcon = new JLabel("✉️");
        }
        panel.add(emailIcon, gbc);
        
        gbc.gridx = 1;
        JTextField emailField = new JTextField(25);
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        emailField.setText("Enter your email address");
        emailField.setForeground(Color.GRAY);
        emailField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (emailField.getText().equals("Enter your email address")) {
                    emailField.setText("");
                    emailField.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (emailField.getText().isEmpty()) {
                    emailField.setText("Enter your email address");
                    emailField.setForeground(Color.GRAY);
                }
            }
        });
        panel.add(emailField, gbc);
        
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        
        ImageIcon sendIcon = IconLoader.loadIcon("email", 16, 16);
        JButton resetBtn;
        if (sendIcon != null) {
            resetBtn = new JButton("Send Reset Link", sendIcon);
        } else {
            resetBtn = new JButton("📧 Send Reset Link");
        }
        resetBtn.setBackground(ThemeManager.ACCENT);
        resetBtn.setForeground(Color.WHITE);
        resetBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resetBtn.setFocusPainted(false);
        resetBtn.setBorderPainted(false);
        resetBtn.setPreferredSize(new Dimension(200, 40));
        resetBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        resetBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            if (email.isEmpty() || email.equals("Enter your email address")) {
                JOptionPane.showMessageDialog(panel, "⚠️ Please enter your email address.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(panel, 
                "✅ Password reset link has been sent to:\n\n" + email + 
                "\n\n📧 Please check your email inbox.", 
                "Password Reset", 
                JOptionPane.INFORMATION_MESSAGE);
            Window window = SwingUtilities.getWindowAncestor(panel);
            if (window != null) window.dispose();
        });
        panel.add(resetBtn, gbc);
        
        JOptionPane.showConfirmDialog(
            this, 
            panel, 
            "🔑 Reset Password", 
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
    }
    
    // ============================================================
    // PLACEHOLDER METHODS
    // ============================================================
    
    private void setPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                }
            }
        });
    }
    
    private void setPasswordPlaceholder(JPasswordField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(Color.GRAY);
        field.setEchoChar((char) 0);
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    field.setEchoChar('●');
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getPassword().length == 0) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                    field.setEchoChar((char) 0);
                }
            }
        });
    }
    
    // ============================================================
    // TOGGLE PASSWORD VISIBILITY
    // ============================================================
    
    private void togglePasswordVisibility() {
        if (showPasswordCheckBox.isSelected()) {
            passwordField.setEchoChar((char) 0);
            passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            ImageIcon hideIcon = IconLoader.loadIcon("hide", 16, 16);
            if (hideIcon != null) {
                showPasswordCheckBox.setIcon(hideIcon);
            }
            showPasswordCheckBox.setText("Hide Password");
        } else {
            passwordField.setEchoChar('●');
            ImageIcon showIcon = IconLoader.loadIcon("show", 16, 16);
            if (showIcon != null) {
                showPasswordCheckBox.setIcon(showIcon);
            }
            showPasswordCheckBox.setText("Show Password");
        }
    }
    
    // ============================================================
    // LISTENERS
    // ============================================================
    
    private class EnterKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER && loginButton.isEnabled()) {
                performLogin();
            }
        }
    }
    
    private class TextFieldListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) { updateLoginButtonState(); }
        @Override
        public void removeUpdate(DocumentEvent e) { updateLoginButtonState(); }
        @Override
        public void changedUpdate(DocumentEvent e) { updateLoginButtonState(); }
    }
    
    private void updateLoginButtonState() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        boolean hasUsername = !username.isEmpty() && !username.equals(USERNAME_PLACEHOLDER);
        boolean hasPassword = !password.isEmpty() && !password.equals(PASSWORD_PLACEHOLDER);
        boolean shouldBeEnabled = hasUsername && hasPassword;
        
        if (loginButton.isEnabled() != shouldBeEnabled) {
            loginButton.setEnabled(shouldBeEnabled);
            loginButton.setBackground(shouldBeEnabled ? ThemeManager.ACCENT : Color.GRAY);
        }
    }
    
    // ============================================================
    // LOGIN LOGIC
    // ============================================================
    
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || username.equals(USERNAME_PLACEHOLDER)) {
            statusLabel.setText("⚠️ Please enter your username, student number or email.");
            statusLabel.setForeground(ThemeManager.WARNING);
            return;
        }
        
        if (password.isEmpty() || password.equals(PASSWORD_PLACEHOLDER)) {
            statusLabel.setText("⚠️ Please enter your password.");
            statusLabel.setForeground(ThemeManager.WARNING);
            return;
        }
        
        loginButton.setEnabled(false);
        loginButton.setText("Signing in...");
        loginButton.setIcon(null);
        statusLabel.setText("🔄 Authenticating...");
        statusLabel.setForeground(ThemeManager.ACCENT);
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return userDAO.authenticateUser(username, password);
            }
            
            @Override
            protected void done() {
                try {
                    boolean authenticated = get();
                    
                    if (authenticated) {
                        User user = userDAO.getUserByUsernameOrEmail(username);
                        if (user != null) {
                            userDAO.updateLastLogin(user.getUserId());
                            statusLabel.setText("✅ Login successful!");
                            statusLabel.setForeground(ThemeManager.SUCCESS);
                            
                            Timer timer = new Timer(500, e -> openDashboard(user));
                            timer.setRepeats(false);
                            timer.start();
                        }
                    } else {
                        statusLabel.setText("❌ Invalid username or password.");
                        statusLabel.setForeground(ThemeManager.DANGER);
                        passwordField.setText("");
                        setPasswordPlaceholder(passwordField, PASSWORD_PLACEHOLDER);
                        usernameField.requestFocus();
                        
                        loginButton.setEnabled(true);
                        loginButton.setText("Sign In");
                        ImageIcon loginIcon = IconLoader.loadIcon("login", 22, 22);
                        if (loginIcon != null) {
                            loginButton.setIcon(loginIcon);
                        }
                        updateLoginButtonState();
                    }
                } catch (Exception e) {
                    statusLabel.setText("❌ Error: " + e.getMessage());
                    statusLabel.setForeground(ThemeManager.DANGER);
                    
                    loginButton.setEnabled(true);
                    loginButton.setText("Sign In");
                    ImageIcon loginIcon = IconLoader.loadIcon("login", 22, 22);
                    if (loginIcon != null) {
                        loginButton.setIcon(loginIcon);
                    }
                }
            }
        };
        worker.execute();
    }
    
    private void openDashboard(User user) {
        dispose();
        SwingUtilities.invokeLater(() -> {
            DashboardView dashboard = new DashboardView(user);
            dashboard.setVisible(true);
        });
    }
}