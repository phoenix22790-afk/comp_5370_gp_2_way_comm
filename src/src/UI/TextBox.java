package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

public class TextBox implements UIElement, KeyListener {
    private final JPanel parent;
    private final StringBuilder text = new StringBuilder();
    private final Font font;
    private final int height, x, y;
    private boolean focused = false;
    private Consumer<String> action;

    public TextBox(JPanel parent, Font font, int width, int height, int x, int y, Consumer<String> action) {
        this.parent = parent;
        this.font = font;
        this.height = height;
        this.x = x;
        this.y = y;
        this.action = action;

        new ClickListener(parent, new Rectangle(x, y, width, height), () -> {
            focused = true;
            parent.repaint();
        });

        parent.addKeyListener(this);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (!focused) return;
        char c = e.getKeyChar();
        // clears if user presses enter and runs the Runnable
        if (c == '\n') {
            String message = text.toString();   // grab the text BEFORE clearing
            text.setLength(0);
            action.accept(message);
        }
        // guards against pressing backspace in an empty box
        else if (c == '\b') { if (!text.isEmpty()) text.setLength(text.length() - 1); }
        else if (!Character.isISOControl(c)) text.append(c);
        parent.repaint();
    }

    @Override public void keyPressed(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    @Override
    public void draw(Graphics g) {
        g.setFont(font);
        g.setColor(Color.BLACK);
        g.drawString(text.toString(), x, y + height - 4);
    }
}