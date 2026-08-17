package ccams.utils;

public class IconManager {
    
    // Navigation icons using Unicode symbols (works everywhere)
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
    
    // Status icons
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
    
    // Action icons
    public static String getActionIcon(String action) {
        switch (action) {
            case "Add": return "✚";
            case "Edit": return "✎";
            case "Delete": return "✖";
            case "Search": return "⌕";
            case "Refresh": return "↻";
            case "Save": return "⬇";
            case "Cancel": return "✗";
            case "Approve": return "✓";
            case "Complete": return "✔";
            case "Reschedule": return "⟳";
            case "Book": return "◆";
            default: return "•";
        }
    }
}