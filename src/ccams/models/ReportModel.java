package ccams.models;

import java.sql.Date;
import java.sql.Timestamp;

public class ReportModel {
    private int reportId;
    private String reportType;
    private Date startDate;
    private Date endDate;
    private String generatedBy;
    private String fileName;
    private String filePath;
    private Timestamp generatedAt;
    
    // Report data
    private int totalAppointments;
    private int pendingAppointments;
    private int approvedAppointments;
    private int completedAppointments;
    private int cancelledAppointments;
    private int totalStudents;
    private int totalStaff;
    private int totalNurses;
    private int totalPsychologists;
    
    public ReportModel() {}
    
    // Getters and Setters
    public int getReportId() { return reportId; }
    public void setReportId(int reportId) { this.reportId = reportId; }
    
    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    
    public String getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }
    
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    
    public Timestamp getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(Timestamp generatedAt) { this.generatedAt = generatedAt; }
    
    public int getTotalAppointments() { return totalAppointments; }
    public void setTotalAppointments(int totalAppointments) { this.totalAppointments = totalAppointments; }
    
    public int getPendingAppointments() { return pendingAppointments; }
    public void setPendingAppointments(int pendingAppointments) { this.pendingAppointments = pendingAppointments; }
    
    public int getApprovedAppointments() { return approvedAppointments; }
    public void setApprovedAppointments(int approvedAppointments) { this.approvedAppointments = approvedAppointments; }
    
    public int getCompletedAppointments() { return completedAppointments; }
    public void setCompletedAppointments(int completedAppointments) { this.completedAppointments = completedAppointments; }
    
    public int getCancelledAppointments() { return cancelledAppointments; }
    public void setCancelledAppointments(int cancelledAppointments) { this.cancelledAppointments = cancelledAppointments; }
    
    public int getTotalStudents() { return totalStudents; }
    public void setTotalStudents(int totalStudents) { this.totalStudents = totalStudents; }
    
    public int getTotalStaff() { return totalStaff; }
    public void setTotalStaff(int totalStaff) { this.totalStaff = totalStaff; }
    
    public int getTotalNurses() { return totalNurses; }
    public void setTotalNurses(int totalNurses) { this.totalNurses = totalNurses; }
    
    public int getTotalPsychologists() { return totalPsychologists; }
    public void setTotalPsychologists(int totalPsychologists) { this.totalPsychologists = totalPsychologists; }
}