package ccams.models;

import java.sql.Timestamp;

public class Staff {
    private int staffId;
    private int userId;
    private String firstName;
    private String lastName;
    private String profession;
    private String specialization;
    private String email;
    private String phone;
    private String licenseNumber;
    private int yearsExperience;
    private boolean isAvailable;
    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Additional fields
    private String username;
    private String roleName;
    
    public Staff() {
        this.isAvailable = true;
        this.isActive = true;
    }
    
    // Getters and Setters
    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getFullName() { return firstName + " " + lastName; }
    
    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }
    
    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    
    public int getYearsExperience() { return yearsExperience; }
    public void setYearsExperience(int yearsExperience) { this.yearsExperience = yearsExperience; }
    
    public boolean isAvailable() { return isAvailable; }
    public void setAvailable(boolean available) { isAvailable = available; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getRoleName() { return roleName; }
    public void setRoleName(String roleName) { this.roleName = roleName; }
    
    @Override
    public String toString() {
        return getFullName() + " (" + profession + ")";
    }
}