package ccams.utils;

import java.util.regex.Pattern;

public class ValidationUtil {
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    private static final Pattern STUDENT_NUMBER_PATTERN = 
        Pattern.compile("^[0-9]{8,10}$");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[0-9]{10}$");
    
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean isValidStudentNumber(String studentNumber) {
        return studentNumber != null && 
               STUDENT_NUMBER_PATTERN.matcher(studentNumber).matches();
    }
    
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
    
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    public static boolean isValidDate(String date) {
        return date != null && date.matches("^\\d{4}-\\d{2}-\\d{2}$");
    }
}