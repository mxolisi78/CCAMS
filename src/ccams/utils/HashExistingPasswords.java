package ccams.utils;

import ccams.database.DatabaseConnection;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class HashExistingPasswords {
    
    public static void main(String[] args) {
        hashAllPasswords();
    }
    
    private static String hashPassword(String password) {
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
    
    public static void hashAllPasswords() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            // Get all users with their current passwords
            String selectQuery = "SELECT user_id, username, password FROM users";
            PreparedStatement selectStmt = conn.prepareStatement(selectQuery);
            ResultSet rs = selectStmt.executeQuery();
            
            Map<Integer, String> updates = new HashMap<>();
            
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                String currentPassword = rs.getString("password");
                
                // Check if password is already hashed (64 characters = SHA-256)
                if (currentPassword.length() != 64) {
                    String hashed = hashPassword(currentPassword);
                    if (hashed != null) {
                        updates.put(userId, hashed);
                        System.out.println("✅ Will hash password for: " + username);
                        System.out.println("   Original: " + currentPassword);
                        System.out.println("   Hashed: " + hashed);
                    }
                } else {
                    System.out.println("⏭️ Password already hashed for: " + username);
                }
            }
            
            // Update passwords
            String updateQuery = "UPDATE users SET password = ? WHERE user_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            
            int count = 0;
            for (Map.Entry<Integer, String> entry : updates.entrySet()) {
                updateStmt.setString(1, entry.getValue());
                updateStmt.setInt(2, entry.getKey());
                updateStmt.addBatch();
                count++;
            }
            
            if (count > 0) {
                int[] results = updateStmt.executeBatch();
                System.out.println("\n✅ Updated " + results.length + " passwords to hashed versions!");
            } else {
                System.out.println("\n✅ No passwords needed updating.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}