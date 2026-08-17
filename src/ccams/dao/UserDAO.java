package ccams.dao;

import ccams.database.DatabaseConnection;
import ccams.models.User;
import ccams.utils.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    
    public User getUserByUsername(String username) {
        String query = "SELECT u.*, r.role_name FROM users u " +
                      "LEFT JOIN roles r ON u.role_id = r.role_id " +
                      "WHERE u.username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRoleId(rs.getInt("role_id"));
                user.setRoleName(rs.getString("role_name"));
                
                try {
                    user.setActive(rs.getBoolean("is_active"));
                } catch (SQLException e) {
                    user.setActive(true);
                }
                
                user.setCreatedAt(rs.getTimestamp("created_at"));
                
                try {
                    user.setLastLogin(rs.getTimestamp("last_login"));
                } catch (SQLException e) {
                    user.setLastLogin(null);
                }
                
                return user;
            }
            return null;
            
        } catch (SQLException e) {
            System.err.println("✗ Error getting user: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    // ============================================================
    // AUTHENTICATE USING PASSWORDUTIL
    // ============================================================
    public boolean authenticateUser(String username, String password) {
        System.out.println("🔍 Attempting login for: " + username);
        
        // First try: username
        String query = "SELECT password, is_active FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                boolean isActive = rs.getBoolean("is_active");
                
                if (!isActive) {
                    System.out.println("❌ User account is deactivated: " + username);
                    return false;
                }
                
                // ============================================================
                // USE PASSWORDUTIL TO VERIFY
                // ============================================================
                boolean match = PasswordUtil.verifyPassword(password, storedPassword);
                System.out.println("Password match for user '" + username + "': " + match);
                return match;
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Authentication error (username): " + e.getMessage());
            e.printStackTrace();
        }
        
        // Second try: staff email
        try {
            String emailQuery = "SELECT u.password, u.is_active FROM users u " +
                               "LEFT JOIN staff s ON u.user_id = s.user_id " +
                               "WHERE s.email = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(emailQuery)) {
                
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    boolean isActive = rs.getBoolean("is_active");
                    
                    if (!isActive) {
                        System.out.println("❌ User account is deactivated: " + username);
                        return false;
                    }
                    
                    boolean match = PasswordUtil.verifyPassword(password, storedPassword);
                    System.out.println("Password match for staff email '" + username + "': " + match);
                    return match;
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Authentication error (staff email): " + e.getMessage());
            e.printStackTrace();
        }
        
        // Third try: student email
        try {
            String studentEmailQuery = "SELECT u.password, u.is_active FROM users u " +
                                      "LEFT JOIN students s ON u.user_id = s.user_id " +
                                      "WHERE s.email = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(studentEmailQuery)) {
                
                pstmt.setString(1, username);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    boolean isActive = rs.getBoolean("is_active");
                    
                    if (!isActive) {
                        System.out.println("❌ User account is deactivated: " + username);
                        return false;
                    }
                    
                    boolean match = PasswordUtil.verifyPassword(password, storedPassword);
                    System.out.println("Password match for student email '" + username + "': " + match);
                    return match;
                }
            }
        } catch (SQLException e) {
            System.err.println("✗ Authentication error (student email): " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("❌ User not found: " + username);
        return false;
    }
    
    public User getUserByUsernameOrEmail(String login) {
        User user = getUserByUsername(login);
        if (user != null) {
            return user;
        }
        
        String staffQuery = "SELECT u.*, r.role_name FROM users u " +
                           "LEFT JOIN staff s ON u.user_id = s.user_id " +
                           "LEFT JOIN roles r ON u.role_id = r.role_id " +
                           "WHERE s.email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(staffQuery)) {
            
            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error getting staff by email: " + e.getMessage());
            e.printStackTrace();
        }
        
        String studentQuery = "SELECT u.*, r.role_name FROM users u " +
                             "LEFT JOIN students s ON u.user_id = s.user_id " +
                             "LEFT JOIN roles r ON u.role_id = r.role_id " +
                             "WHERE s.email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(studentQuery)) {
            
            pstmt.setString(1, login);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToUser(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error getting student by email: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setRoleId(rs.getInt("role_id"));
        user.setRoleName(rs.getString("role_name"));
        
        try {
            user.setActive(rs.getBoolean("is_active"));
        } catch (SQLException e) {
            user.setActive(true);
        }
        
        user.setCreatedAt(rs.getTimestamp("created_at"));
        
        try {
            user.setLastLogin(rs.getTimestamp("last_login"));
        } catch (SQLException e) {
            user.setLastLogin(null);
        }
        
        return user;
    }
    
    public void updateLastLogin(int userId) {
        String query = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            System.out.println("Updated last_login for user_id: " + userId);
            
        } catch (SQLException e) {
            System.err.println("✗ Error updating last login: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public String getRoleName(int roleId) {
        String query = "SELECT role_name FROM roles WHERE role_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, roleId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("role_name");
            }
            return null;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<String> getAllRoles() {
        List<String> roles = new ArrayList<>();
        String query = "SELECT role_name FROM roles ORDER BY role_id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                roles.add(rs.getString("role_name"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }
    
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT u.*, r.role_name FROM users u " +
                      "LEFT JOIN roles r ON u.role_id = r.role_id " +
                      "ORDER BY u.username";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}