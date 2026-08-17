package ccams.models;

import java.sql.Timestamp;

public class Appointment {
    private int appointmentId;
    private int studentUserId;  // This maps to user_id in students table
    private int staffId;
    private Timestamp appointmentDate;
    private int statusId;
    private String notes;
    private String cancellationReason;
    private String rescheduleReason;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Additional fields for display (from joins)
    private String studentName;
    private String studentNumber;
    private String staffName;
    private String staffProfession;
    private String statusName;
    
    public Appointment() {}
    
    public Appointment(int studentUserId, int staffId, Timestamp appointmentDate, int statusId) {
        this.studentUserId = studentUserId;
        this.staffId = staffId;
        this.appointmentDate = appointmentDate;
        this.statusId = statusId;
    }
    
    // Getters and Setters
    public int getAppointmentId() { return appointmentId; }
    public void setAppointmentId(int appointmentId) { this.appointmentId = appointmentId; }
    
    public int getStudentUserId() { return studentUserId; }
    public void setStudentUserId(int studentUserId) { this.studentUserId = studentUserId; }
    
    public int getStaffId() { return staffId; }
    public void setStaffId(int staffId) { this.staffId = staffId; }
    
    public Timestamp getAppointmentDate() { return appointmentDate; }
    public void setAppointmentDate(Timestamp appointmentDate) { this.appointmentDate = appointmentDate; }
    
    public int getStatusId() { return statusId; }
    public void setStatusId(int statusId) { this.statusId = statusId; }
    
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    
    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
    
    public String getRescheduleReason() { return rescheduleReason; }
    public void setRescheduleReason(String rescheduleReason) { this.rescheduleReason = rescheduleReason; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
    
    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
    
    // Additional fields
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    
    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }
    
    public String getStaffName() { return staffName; }
    public void setStaffName(String staffName) { this.staffName = staffName; }
    
    public String getStaffProfession() { return staffProfession; }
    public void setStaffProfession(String staffProfession) { this.staffProfession = staffProfession; }
    
    public String getStatusName() { return statusName; }
    public void setStatusName(String statusName) { this.statusName = statusName; }
    
    @Override
    public String toString() {
        return appointmentDate + " - " + studentName;
    }
}