package ccams.utils;

import javax.swing.*;
import java.awt.*;

public class ButtonIconHelper {
    
    public static void addIcon(JButton button, String icon, int fontSize) {
        button.setText(icon + " " + button.getText());
        button.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
    }
    
    public static JButton createButtonWithIcon(String text, String icon, Color bgColor) {
        JButton button = new JButton(icon + " " + text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
}
