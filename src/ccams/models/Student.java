package ccams.models;

import java.sql.Date;
import java.sql.Timestamp;

public class Student {
    private int studentId;
    private int userId;
    private String studentNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Date dateOfBirth;
    private String address;
    private String emergencyContact;
    private String emergencyPhone;
    private boolean isActive;  
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Additional fields for display
    private String username;
    private String roleName;
    
    // =====================================
    // NEW FIELD: Password
    // =====================================
    private String password; 
    
    public Student() {
        this.isActive = true;  // Default to active
    }
    
    public Student(String studentNumber, String firstName, String lastName, 
                   String email, String phone) {
        this.studentNumber = studentNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.isActive = true;
    }
    
    // Getters and Setters
    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getFullName() { return firstName + " " + lastName; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public Date getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(Date dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }
    
    public String getEmergencyPhone() { return emergencyPhone; }
    public void setEmergencyPhone(String emergencyPhone) { this.emergencyPhone = emergencyPhone; }
    
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
    
    // =====================================
    // NEW: Password Getter and Setter
    // =====================================
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    @Override
    public String toString() {
        return studentNumber + " - " + getFullName();
    }
}