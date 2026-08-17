package ccams.utils;

import com.formdev.flatlaf.FlatIntelliJLaf;
import javax.swing.UIManager;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;

public class ThemeManager {
    
    // Brand Colors
    public static final Color PRIMARY = new Color(26, 35, 53);
    public static final Color PRIMARY_LIGHT = new Color(45, 55, 80);
    public static final Color ACCENT = new Color(0, 120, 215);
    public static final Color SUCCESS = new Color(0, 150, 0);
    public static final Color WARNING = new Color(255, 165, 0);
    public static final Color DANGER = new Color(200, 0, 0);
    public static final Color INFO = new Color(0, 120, 215);
    
    public static void setupFlatLaf() {
        try {
            UIManager.setLookAndFeel(new FlatIntelliJLaf());
            
            // UI customizations
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 10);
            UIManager.put("Button.margin", new Insets(8, 16, 8, 16));
            UIManager.put("Table.showHorizontalLines", true);
            UIManager.put("Table.showVerticalLines", false);
            UIManager.put("Table.rowHeight", 32);
            UIManager.put("TabbedPane.tabHeight", 35);
            UIManager.put("TabbedPane.font", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("Label.font", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("Button.font", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("TextField.font", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("TextArea.font", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("Table.font", new Font("Segoe UI", Font.PLAIN, 13));
            UIManager.put("TableHeader.font", new Font("Segoe UI", Font.BOLD, 13));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}