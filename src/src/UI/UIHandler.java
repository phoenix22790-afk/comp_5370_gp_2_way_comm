package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class UIHandler extends JPanel {
    private CopyOnWriteArrayList<UIElement> elements = new CopyOnWriteArrayList<>(); //"CopyOnWrite" because technically the element list can be written to as the screen is updating and that produces errors.

    public UIHandler(Color background) {
        setBackground(background);
    }

    public void addWindow(Color color, int width, int height, int x, int y) {
        elements.add(new Window(color, width, height, x, y));
        repaint();
    }

    public void addText(String text, int size, Color color, int x, int y) {
        elements.add(new Text(text, new Font("Arial", Font.PLAIN, size), color, x, y));
        repaint();
    }

    public void addButton(Color color, int width, int height, int x, int y, Runnable onClick) {
        Button button = new Button(color, width, height, x, y);
        elements.add(button);
        new ClickListener(this, button.bounds(), onClick); // "this" passes itself to the method - could just shove this entire other class its calling on into this one but it looks prettier this way
        repaint();
    }


    public void addTextBox(int fontSize, int clickAreaWidth, int clickAreaHeight, int x, int y, Consumer<String> onEnter) {
        elements.add(new TextBox(this, new Font("Arial", Font.PLAIN, fontSize), clickAreaWidth, clickAreaHeight, x, y, onEnter));
        requestFocusInWindow();
        repaint();
    }

    public void clearWindow() {
        elements.clear();
        for (MouseListener l : getMouseListeners()) removeMouseListener(l);
        for (KeyListener l : getKeyListeners()) removeKeyListener(l);
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (UIElement element : elements) {
            element.draw(g);
        }
    }
}
