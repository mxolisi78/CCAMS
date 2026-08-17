package ccams.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GenerateIcons {
    
    private static final int SIZE = 32;
    // FIX: Updated to match your exact NetBeans folder structure
    private static final String OUTPUT_PATH = "src/ccams/resources/icons/";
    
    public static void main(String[] args) {
        // Create output directory
        new File(OUTPUT_PATH).mkdirs();
        
        // Generate each icon
        generateIcon("dashboard", new Color(0, 120, 215), "D");
        generateIcon("students", new Color(0, 150, 136), "S");
        generateIcon("staff", new Color(123, 31, 162), "T");
        generateIcon("appointments", new Color(255, 87, 34), "A");
        generateIcon("schedules", new Color(255, 165, 0), "C");
        generateIcon("reports", new Color(0, 150, 0), "R");
        generateIcon("settings", new Color(100, 100, 100), "G");
        generateIcon("logout", new Color(200, 0, 0), "L");
        generateIcon("user", new Color(0, 120, 215), "U");
        generateIcon("pending", new Color(255, 165, 0), "P");
        generateIcon("approved", new Color(0, 150, 0), "A");
        generateIcon("completed", new Color(0, 150, 0), "C");
        generateIcon("cancelled", new Color(200, 0, 0), "C");
        
        System.out.println("All icons generated in: " + OUTPUT_PATH);
    }
    
    private static void generateIcon(String name, Color color, String letter) {
        BufferedImage image = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Draw rounded rectangle background
        RoundRectangle2D rect = new RoundRectangle2D.Float(0, 0, SIZE, SIZE, 8, 8);
        g.setColor(color);
        g.fill(rect);
        
        // Add subtle shadow
        g.setColor(new Color(0, 0, 0, 30));
        RoundRectangle2D shadow = new RoundRectangle2D.Float(1, 1, SIZE, SIZE, 8, 8);
        g.fill(shadow);
        
        // Draw letter
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 18));
        FontMetrics fm = g.getFontMetrics();
        int x = (SIZE - fm.stringWidth(letter)) / 2;
        int y = (SIZE - fm.getHeight()) / 2 + fm.getAscent() + 1;
        g.drawString(letter, x, y);
        
        g.dispose();
        
        try {
            File outputFile = new File(OUTPUT_PATH + name + ".png");
            ImageIO.write(image, "PNG", outputFile);
            System.out.println("Generated: " + name + ".png");
        } catch (IOException e) {
            System.err.println("Error generating " + name + ".png: " + e.getMessage());
        }
    }
}