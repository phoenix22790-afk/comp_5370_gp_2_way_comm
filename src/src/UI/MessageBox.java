package UI;

import javax.swing.*;
import java.awt.*;

public class MessageBox implements UIElement {
    private JPanel parent;
    private Font font;
    private int x, y;
    private Color color;
    private volatile String message = "";

    public MessageBox(JPanel parent, Color color, int size, int x, int y) {
        this.parent = parent;
        this.font = new Font("Arial", Font.PLAIN, size);
        this.color = color;
        this.x = x;
        this.y = y;
    }

    public void setMessage(String message) {
        this.message = message;
        parent.repaint();
    }

    @Override
    public void draw(Graphics g) {
        g.setFont(font);
        g.setColor(color);
        g.drawString(message, x, y);
    }
}