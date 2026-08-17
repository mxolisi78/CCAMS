package ccams;

import ccams.auth.LoginForm;
import ccams.database.DatabaseConnection;
import ccams.utils.ThemeManager;

import javax.swing.SwingUtilities;

public class CCAMS {
    public static void main(String[] args) {
        // Setup modern theme
        ThemeManager.setupFlatLaf();
        
        // Test database connection
        if (DatabaseConnection.testConnection()) {
            System.out.println("✓ Database connection successful!");
        } else {
            System.err.println("✗ Failed to connect to database. Please check MySQL.");
            return;
        }
        
        // Launch login form
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}