package ccams.services;

import ccams.dao.AppointmentDAO;
import ccams.dao.StaffDAO;
import ccams.dao.StudentDAO;
import ccams.models.Appointment;
import ccams.models.ReportData;
import ccams.models.Staff;
import ccams.models.Student;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.Date;
import java.util.List;
import java.util.Map;

public class ReportService {
    
    private AppointmentDAO appointmentDAO;
    private StudentDAO studentDAO;
    private StaffDAO staffDAO;
    
    public ReportService() {
        this.appointmentDAO = new AppointmentDAO();
        this.studentDAO = new StudentDAO();
        this.staffDAO = new StaffDAO();
    }
    
    // Generate appointment report data
    public ReportData generateAppointmentReport(Date startDate, Date endDate) {
        ReportData report = new ReportData();
        report.setReportType("Appointment Report");
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        
        int total = appointmentDAO.getTotalAppointments();
        int pending = appointmentDAO.getAppointmentCountByStatus(1);
        int approved = appointmentDAO.getAppointmentCountByStatus(2);
        int completed = appointmentDAO.getAppointmentCountByStatus(3);
        int cancelled = appointmentDAO.getAppointmentCountByStatus(4);
        
        report.setTotalAppointments(total);
        report.setPendingAppointments(pending);
        report.setApprovedAppointments(approved);
        report.setCompletedAppointments(completed);
        report.setCancelledAppointments(cancelled);
        
        return report;
    }
    
    // Generate student report
    public ReportData generateStudentReport() {
        ReportData report = new ReportData();
        report.setReportType("Student Report");
        
        List<Student> students = studentDAO.getAllStudents();
        report.setTotalStudents(students.size());
        
        return report;
    }
    
    // Generate staff report
    public ReportData generateStaffReport() {
        ReportData report = new ReportData();
        report.setReportType("Staff Report");
        
        List<Staff> staffList = staffDAO.getAllStaff();
        int nurses = 0, psychologists = 0;
        
        for (Staff s : staffList) {
            if (s.getProfession().equalsIgnoreCase("NURSE")) {
                nurses++;
            } else if (s.getProfession().equalsIgnoreCase("PSYCHOLOGIST")) {
                psychologists++;
            }
        }
        
        report.setTotalStaff(staffList.size());
        report.setTotalNurses(nurses);
        report.setTotalPsychologists(psychologists);
        
        return report;
    }
    
    // Generate weekly report
    public ReportData generateWeeklyReport() {
        java.sql.Date endDate = new java.sql.Date(System.currentTimeMillis());
        java.sql.Date startDate = new java.sql.Date(endDate.getTime() - 7 * 24 * 60 * 60 * 1000L);
        
        ReportData report = generateAppointmentReport(startDate, endDate);
        report.setReportType("Weekly Report");
        
        return report;
    }
    
    // Generate monthly report
    public ReportData generateMonthlyReport() {
        java.sql.Date endDate = new java.sql.Date(System.currentTimeMillis());
        java.sql.Date startDate = new java.sql.Date(endDate.getTime() - 30 * 24 * 60 * 60 * 1000L);
        
        ReportData report = generateAppointmentReport(startDate, endDate);
        report.setReportType("Monthly Report");
        
        return report;
    }
    
    // ============================================================
    // NEW: Generate Annual Report
    // ============================================================
    public ReportData generateAnnualReport() {
        java.sql.Date endDate = new java.sql.Date(System.currentTimeMillis());
        java.sql.Date startDate = new java.sql.Date(endDate.getTime() - 365 * 24 * 60 * 60 * 1000L);
        
        ReportData report = generateAppointmentReport(startDate, endDate);
        report.setReportType("Annual Report");
        
        return report;
    }
    
    // ============================================================
    // NEW: Generate Appointment Summary Report (with date range)
    // ============================================================
    public ReportData generateAppointmentSummaryReport(Date startDate, Date endDate) {
        ReportData report = new ReportData();
        report.setReportType("Appointment Summary Report");
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        
        // Get appointments in date range
        List<Appointment> appointments = appointmentDAO.getAllAppointments();
        int total = 0, pending = 0, approved = 0, completed = 0, cancelled = 0;
        
        for (Appointment a : appointments) {
            if (a.getAppointmentDate() != null) {
                java.sql.Date aptDate = new java.sql.Date(a.getAppointmentDate().getTime());
                
                if ((startDate == null || !aptDate.before(startDate)) &&
                    (endDate == null || !aptDate.after(endDate))) {
                    
                    total++;
                    switch (a.getStatusId()) {
                        case 1: pending++; break;
                        case 2: approved++; break;
                        case 3: completed++; break;
                        case 4: cancelled++; break;
                        default: break;
                    }
                }
            }
        }
        
        report.setTotalAppointments(total);
        report.setPendingAppointments(pending);
        report.setApprovedAppointments(approved);
        report.setCompletedAppointments(completed);
        report.setCancelledAppointments(cancelled);
        
        return report;
    }
    
    // Generate summary report
    public ReportData generateSummaryReport() {
        ReportData report = new ReportData();
        report.setReportType("Summary Report");
        
        // 1. Appointment statistics using the optimized DAO counts
        int total = appointmentDAO.getTotalAppointments();
        int pending = appointmentDAO.getAppointmentCountByStatus(1);
        int approved = appointmentDAO.getAppointmentCountByStatus(2);
        int completed = appointmentDAO.getAppointmentCountByStatus(3);
        int cancelled = appointmentDAO.getAppointmentCountByStatus(4);
        
        report.setTotalAppointments(total);
        report.setPendingAppointments(pending);
        report.setApprovedAppointments(approved);
        report.setCompletedAppointments(completed);
        report.setCancelledAppointments(cancelled);
        
        // 2. Student statistics
        report.setTotalStudents(studentDAO.countStudents());
        
        // 3. Staff statistics
        int nurses = staffDAO.countStaffByProfession("NURSE");
        int psychologists = staffDAO.countStaffByProfession("PSYCHOLOGIST");
        
        report.setTotalNurses(nurses);
        report.setTotalPsychologists(psychologists);
        report.setTotalStaff(nurses + psychologists);
        
        // Weekly trends for bar chart
        Map<String, Integer> trends = appointmentDAO.getWeeklyTrends();
        report.setWeeklyTrends(trends);

        return report;
    }
}