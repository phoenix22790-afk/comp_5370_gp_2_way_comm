package UI;

import java.awt.*;

public interface UIElement {
    void draw(Graphics g);
}


record Window(Color color, int width, int height, int x, int y) implements UIElement {
    public void draw(Graphics g) {
        g.setColor(color);
        g.fillRect(x, y, width, height);
    }
}

record Button(Color color, int width, int height, int x, int y) implements UIElement {
    public void draw(Graphics g) {
        g.setColor(color);
        g.drawRect(x, y, width, height);
    }

    public Rectangle bounds() {
        return new Rectangle(x, y, width, height);
    }
}

record Text(String text, Font font, Color color, int x, int y) implements UIElement {
    public void draw(Graphics g) {
        g.setFont(font);
        g.setColor(color);
        g.drawString(text, x, y);
    }
}
