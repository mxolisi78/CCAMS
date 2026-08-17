package ccams;

import ccams.database.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.security.MessageDigest;

public class ResetPassword {
    
    // Copying the exact hash method from your UserDAO
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        String username = "admin";
        String plainPassword = "admin123";
        
        // 1. Generate the hash using the same method as your app
        String generatedHash = hashPassword(plainPassword);
        System.out.println("Generated Hash: " + generatedHash);

        // 2. Connect to DB and update
        String sql = "UPDATE users SET password = ? WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, generatedHash);
            pstmt.setString(2, username);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("=========================================");
                System.out.println("✅ SUCCESS! Password reset for '" + username + "'.");
                System.out.println("✅ New Password: " + plainPassword);
                System.out.println("=========================================");
            } else {
                System.out.println("❌ User '" + username + "' not found in database.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}