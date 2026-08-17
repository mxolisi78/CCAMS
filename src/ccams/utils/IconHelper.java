package ccams.utils;

public class IconHelper {
    
    // Navigation Icons - Unicode symbols that work everywhere
    public static String getNavIcon(String menuItem) {
        switch (menuItem) {
            case "Dashboard": return "◆";
            case "Students": return "◉";
            case "Staff": return "◈";
            case "Appointments": return "▣";
            case "Schedules": return "◑";
            case "Reports": return "◊";
            case "Settings": return "⚙";
            case "My Appointments": return "▸";
            case "Schedule": return "▸";
            case "Profile": return "▸";
            case "Book Appointment": return "▸";
            default: return "•";
        }
    }
    
    // Status Icons
    public static String getStatusIcon(String status) {
        switch (status) {
            case "Active": return "●";
            case "Inactive": return "○";
            case "Pending": return "◒";
            case "Approved": return "✓";
            case "Completed": return "✔";
            case "Cancelled": return "✕";
            case "Yes": return "✓";
            case "No": return "✗";
            default: return "•";
        }
    }
}