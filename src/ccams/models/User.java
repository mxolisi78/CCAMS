package ccams.models;

import java.sql.Timestamp;

public class User {
    private int userId;
    private String username;
    private String password;
    private int roleId;
    private String roleName;
    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp lastLogin;
    
    public User() {
        this.isActive = true;
    }
    
    public User(int userId, String username, String roleName) {
        this.userId = userId;
        this.username = username;
        this.roleName = roleName;
        this.isActive = true;
    }
    
    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public int getRoleId() { return roleId; }
    public void setRoleId(int roleId) { this.roleId = roleId; }
    
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    
    public String getRole() { return roleName; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getLastLogin() { return lastLogin; }
    public void setLastLogin(Timestamp lastLogin) { this.lastLogin = lastLogin; }
    
    @Override
    public String toString() {
        return username + " (" + roleName + ")";
    }
}