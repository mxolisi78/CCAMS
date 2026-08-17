package ccams.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

public class IconGenerator {
    
    /**
     * Generate a colored circle icon with a letter
     */
    public static ImageIcon generateCircleIcon(String letter, Color color, int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Draw circle
        g.setColor(color);
        g.fillOval(0, 0, size, size);
        
        // Draw letter
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, size / 2));
        FontMetrics fm = g.getFontMetrics();
        int x = (size - fm.stringWidth(letter)) / 2;
        int y = (size - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(letter, x, y);
        
        g.dispose();
        return new ImageIcon(image);
    }
    
    /**
     * Generate a colored rounded square icon with a letter
     */
    public static ImageIcon generateRoundedIcon(String letter, Color color, int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        RoundRectangle2D rect = new RoundRectangle2D.Float(2, 2, size-4, size-4, 8, 8);
        g.setColor(color);
        g.fill(rect);
        
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, size / 2));
        FontMetrics fm = g.getFontMetrics();
        int x = (size - fm.stringWidth(letter)) / 2;
        int y = (size - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(letter, x, y);
        
        g.dispose();
        return new ImageIcon(image);
    }
    
    /**
     * Generate a hospital/clinic icon
     */
    public static ImageIcon generateHospitalIcon(int size) {
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Background circle
        g.setColor(new Color(0, 80, 160));
        g.fillOval(2, 2, size-4, size-4);
        
        // White cross
        g.setColor(Color.WHITE);
        int crossSize = size / 3;
        int centerX = size / 2 - crossSize / 2;
        int centerY = size / 2 - crossSize / 2;
        
        // Vertical bar
        g.fillRect(centerX + crossSize / 3, centerY, crossSize / 3, crossSize);
        // Horizontal bar
        g.fillRect(centerX, centerY + crossSize / 3, crossSize, crossSize / 3);
        
        g.dispose();
        return new ImageIcon(image);
    }
}