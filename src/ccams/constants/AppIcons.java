package ccams.constants;

import ccams.utils.IconManager;

import javax.swing.*;

public class AppIcons {
    
    // Navigation Icons (String constants for use in text)
    public static final String DASHBOARD = "◆";
    public static final String STUDENTS = "◉";
    public static final String STAFF = "◈";
    public static final String APPOINTMENTS = "▣";
    public static final String SCHEDULES = "◑";
    public static final String REPORTS = "◊";
    public static final String SETTINGS = "⚙";
    public static final String LOGOUT = "↻";
    
    // Action Icons
    public static final String ADD = "✚";
    public static final String EDIT = "✎";
    public static final String DELETE = "✖";
    public static final String SEARCH = "⌕";
    public static final String REFRESH = "↻";
    public static final String SAVE = "⬇";
    public static final String CANCEL = "✗";
    public static final String APPROVE = "✓";
    public static final String COMPLETE = "✔";
    public static final String RESCHEDULE = "⟳";
    public static final String BOOK = "◆";
    
    // Status Icons
    public static final String ACTIVE = "●";
    public static final String INACTIVE = "○";
    public static final String PENDING = "◒";
    public static final String APPROVED = "✓";
    public static final String COMPLETED = "✔";
    public static final String CANCELLED = "✕";
    public static final String YES = "✓";
    public static final String NO = "✗";
    
    // Login Icons
    public static final String LOGIN_LOGO = "✦";
    public static final String USER = "◉";
    public static final String LOCK = "🔒";
    public static final String WELCOME = "✦";
    public static final String LOGIN_BUTTON = "✓";
    
    /**
     * Get navigation icon for a menu item using IconManager
     */
    public static String getNavIcon(String menuItem) {
        return IconManager.getNavIcon(menuItem);
    }
    
    /**
     * Get status icon using IconManager
     */
    public static String getStatusIcon(String status) {
        return IconManager.getStatusIcon(status);
    }
    
    /**
     * Get action icon using IconManager
     */
    public static String getActionIcon(String action) {
        return IconManager.getActionIcon(action);
    }
}