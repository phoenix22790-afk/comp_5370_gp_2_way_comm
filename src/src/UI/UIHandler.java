package UI;

import UI.ClickListener;
import UI.TextBox;
import UI.UIElement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class UIHandler extends JPanel {
    private CopyOnWriteArrayList<UIElement> elements = new CopyOnWriteArrayList<>(); //"CopyOnWrite" because technically the element list can be written to as the screen is updating and that produces errors.

    // Styled colors and constants

    public static Color transparentColor = new Color(0, 0, 0, 0);

    public static Color bgWindowColor = new Color(64, 64, 64);
    public static Color fgWindowColor = new Color(255, 255, 255);

    public static Color bgButtonColor1 = new Color(192, 192, 192);
    public static Color bgButtonColor2 = new Color(96, 96, 96);
    public static Color bgButtonColor3 = new Color(128, 128, 128);
    public static Color fgButtonColor = new Color(0, 0, 0);

    public static int fontSize = 20;

    public static int largeElementHeight = 80;
    public static int largeElementOffset = 45;

    public static int smallElementHeight = 40;
    public static int smallElementOffset = 25;

    public UIHandler(Color background) {
        setBackground(background);
    }

    public UIHandler() {
        setBackground(bgWindowColor);
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

    public void addTextBox(int fontSize, int clickAreaWidth, int clickAreaHeight, int x, int y, Color color, Consumer<String> onEnter) {
        elements.add(new TextBox(this, new Font("Arial", Font.PLAIN, fontSize), clickAreaWidth, clickAreaHeight, x, y, color, onEnter));
        requestFocusInWindow();
        repaint();
    }

    public MessageBox addMessageBox(Color color, int size, int x, int y){
        MessageBox save = new MessageBox(this, color, size, x, y);
        elements.add(save);
        repaint();
        return save;
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

    // Styled components

    public MessageBox addStyledButton(String text, int width, int height, int size, int x, int y, int x2, int y2, Runnable method) {
        addWindow(bgButtonColor1, width, height, x, y);
        addWindow(bgButtonColor2, width - 2, height - 2, x + 2, y + 2);
        addWindow(bgButtonColor3, width - 4, height - 4, x + 2, y + 2);
        MessageBox messageBox = addMessageBox(fgButtonColor, size, x + x2, y + y2);
        messageBox.setMessage(text);
        addButton(transparentColor, width, height, x, y, method);
        return messageBox;
    }

    public MessageBox addStyledMessage(String text, int width, int height, int size, int x, int y, int x2, int y2) {
        addWindow(bgButtonColor2, width, height, x, y);
        addWindow(bgButtonColor1, width - 2, height - 2, x + 2, y + 2);
        addWindow(bgButtonColor3, width - 4, height - 4, x + 2, y + 2);
        MessageBox messageBox = addMessageBox(fgButtonColor, size, x + x2, y + y2);
        messageBox.setMessage(text);
        return messageBox;
    }

    public void addStyledField(int width, int height, int size, int x, int y, Consumer<String> method) {
        addWindow(bgButtonColor3, width, height, x, y);
        addWindow(bgButtonColor1, width - 2, height - 2, x + 2, y + 2);
        addWindow(bgButtonColor2, width - 4, height - 4, x + 2, y + 2);
        addTextBox(fontSize, width, height, x, y, fgButtonColor, method);
    }
}
