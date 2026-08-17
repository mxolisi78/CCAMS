package ccams.utils;

import ccams.database.DatabaseConnection;

import java.security.MessageDigest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class HashPasswords {
    
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
    
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            
            String query = "SELECT user_id, username, password FROM users";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            
            String updateQuery = "UPDATE users SET password = ? WHERE user_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            
            int count = 0;
            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                
                // Only hash if not already hashed (length != 64)
                if (password.length() != 64) {
                    String hashed = hashPassword(password);
                    if (hashed != null) {
                        updateStmt.setString(1, hashed);
                        updateStmt.setInt(2, userId);
                        updateStmt.addBatch();
                        count++;
                        System.out.println("✅ Will hash password for: " + username);
                    }
                } else {
                    System.out.println("⏭️ Already hashed: " + username);
                }
            }
            
            if (count > 0) {
                int[] results = updateStmt.executeBatch();
                System.out.println("\n✅ Updated " + results.length + " passwords to hashed versions!");
            } else {
                System.out.println("\n✅ All passwords are already hashed!");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}