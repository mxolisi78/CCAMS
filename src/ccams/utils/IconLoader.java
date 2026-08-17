package ccams.utils;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

public class IconLoader {
    
    // FIX: Added "ccams/" to the path to match your package structure
    private static final String ICON_PATH = "/ccams/resources/icons/";
    
    /**
     * Load a PNG icon from resources
     */
    public static ImageIcon loadIcon(String name, int width, int height) {
        try {
            String path = ICON_PATH + name + ".png";
            URL url = IconLoader.class.getResource(path);
            
            if (url != null) {
                ImageIcon icon = new ImageIcon(url);
                Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
            System.err.println("Icon not found: " + name + " at path: " + path);
            
            // Fallback: generate a colored circle icon
            return IconGenerator.generateCircleIcon(
                name.substring(0, 1).toUpperCase(),
                getDefaultColor(name),
                Math.max(width, height)
            );
            
        } catch (Exception e) {
            System.err.println("Error loading icon: " + name);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Load a PNG icon with default size (16x16)
     */
    public static ImageIcon loadIcon(String name) {
        return loadIcon(name, 16, 16);
    }
    
    /**
     * Load sidebar icon (20x20)
     */
    public static ImageIcon loadSidebarIcon(String name) {
        return loadIcon(name, 20, 20);
    }
    
    /**
     * Load button icon (16x16)
     */
    public static ImageIcon loadButtonIcon(String name) {
        return loadIcon(name, 16, 16);
    }
    
    /**
     * Load large icon for stats (24x24)
     */
    public static ImageIcon loadStatIcon(String name) {
        return loadIcon(name, 24, 24);
    }
    
    /**
     * Get default color for icon fallback
     */
    private static Color getDefaultColor(String name) {
        switch (name) {
            case "dashboard": return new Color(0, 120, 215);
            case "students": return new Color(0, 150, 136);
            case "staff": return new Color(123, 31, 162);
            case "appointments": return new Color(255, 87, 34);
            case "schedules": return new Color(255, 165, 0);
            case "reports": return new Color(0, 150, 0);
            case "settings": return new Color(100, 100, 100);
            case "logout": return new Color(200, 0, 0);
            case "user": return new Color(0, 120, 215);
            case "clinic": return new Color(0, 80, 160);
            default: return Color.GRAY;
        }
    }
}