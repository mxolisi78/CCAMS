/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ccams.constants;

public class AppConstants {
    // Database
    public static final String DB_URL = "jdbc:mysql://localhost:3306/ccams_db";
    public static final String DB_USERNAME = "root";
    public static final String DB_PASSWORD = "your_password";
    
    // Roles
    public static final String ROLE_ADMIN = "ADMIN";
    public static final String ROLE_NURSE = "NURSE";
    public static final String ROLE_PSYCHOLOGIST = "PSYCHOLOGIST";
    public static final String ROLE_STUDENT = "STUDENT";
    
    // Appointment Status
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_APPROVED = "APPROVED";
    public static final String STATUS_COMPLETED = "COMPLETED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_RESCHEDULED = "RESCHEDULED";
    public static final String STATUS_NO_SHOW = "NO_SHOW";
    
    // Notification Types
    public static final String NOTIF_APPOINTMENT_CONFIRMATION = "APPOINTMENT_CONFIRMATION";
    public static final String NOTIF_REMINDER = "REMINDER";
    public static final String NOTIF_CANCELLATION = "CANCELLATION";
    public static final String NOTIF_RESCHEDULE = "RESCHEDULE";
    public static final String NOTIF_SYSTEM = "SYSTEM";
    
    // Date Formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String TIME_FORMAT = "HH:mm";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String DISPLAY_DATE_FORMAT = "dd MMM yyyy";
    public static final String DISPLAY_TIME_FORMAT = "hh:mm a";
    
    // File Paths
    public static final String REPORTS_DIR = "./reports/";
    public static final String LOGS_DIR = "./logs/";
    public static final String BACKUP_DIR = "./backup/";
    
    // Pagination
    public static final int PAGE_SIZE = 20;
    
    // Validation
    public static final int MIN_PASSWORD_LENGTH = 8;
    public static final int MAX_STUDENT_NUMBER_LENGTH = 10;
    public static final int MIN_STUDENT_NUMBER_LENGTH = 8;
}
