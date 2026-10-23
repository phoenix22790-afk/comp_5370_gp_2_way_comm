import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import UI.MessageBox;
import UI.UIHandler;

public class UI_App {
    static Color transparentColor = new Color(0, 0, 0, 0);

    static Color bgWindowColor = new Color(64, 64, 64);
    static Color fgWindowColor = new Color(255, 255, 255);

    static Color bgButtonColor1 = new Color(192, 192, 192);
    static Color bgButtonColor2 = new Color(96, 96, 96);
    static Color bgButtonColor3 = new Color(128, 128, 128);
    static Color fgButtonColor = new Color(0, 0, 0);

    static int fontSize = 20;

    static int largeElementHeight = 80;
    static int largeElementOffset = 45;

    static int smallElementHeight = 40;
    static int smallElementOffset = 25;

    public static MessageBox addStyledButton(UIHandler handle, String text, int width, int height, int size, int x, int y, int x2, int y2, Runnable method) {
        handle.addWindow(bgButtonColor1, width, height, x, y);
        handle.addWindow(bgButtonColor2, width - 2, height - 2, x + 2, y + 2);
        handle.addWindow(bgButtonColor3, width - 4, height - 4, x + 2, y + 2);
        MessageBox messageBox = handle.addMessageBox(fgButtonColor, size, x + x2, y + y2);
        messageBox.setMessage(text);
        handle.addButton(transparentColor, width, height, x, y, method);
        return messageBox;
    }

    public static MessageBox addStyledMessage(UIHandler handle, String text, int width, int height, int size, int x, int y, int x2, int y2) {
        handle.addWindow(bgButtonColor2, width, height, x, y);
        handle.addWindow(bgButtonColor1, width - 2, height - 2, x + 2, y + 2);
        handle.addWindow(bgButtonColor3, width - 4, height - 4, x + 2, y + 2);
        MessageBox messageBox = handle.addMessageBox(fgButtonColor, size, x + x2, y + y2);
        messageBox.setMessage(text);
        return messageBox;
    }

    public static MessageBox addStyledField(UIHandler handle, String text, int width, int height, int size, int x, int y, int x2, int y2, Runnable method) {
        handle.addWindow(bgButtonColor3, width, height, x, y);
        handle.addWindow(bgButtonColor1, width - 2, height - 2, x + 2, y + 2);
        handle.addWindow(bgButtonColor2, width - 4, height - 4, x + 2, y + 2);
        MessageBox messageBox = handle.addMessageBox(fgButtonColor, size, x + x2, y + y2); // TODO:
        messageBox.setMessage(text); // TODO:
        handle.addTextBox(fontSize, width, height, x, y, fgButtonColor, message -> {
            // TODO: maybe make method run here, with message as param?
            messageBox.setMessage(message);
        });
        // TODO:
        return messageBox;
    }

    public static void main(String[] args) throws Exception {
        UIHandler test = new UIHandler(bgWindowColor);
        JFrame frame = new JFrame();
        test.setPreferredSize(new Dimension(800, 500));
        frame.add(test);
        frame.pack();
        frame.setVisible(true);

        test.clearWindow();
        addStyledButton(test, "TEST MODE SWITCH", 200, largeElementHeight, fontSize, 0, 0, 10, largeElementOffset, () -> {
            // TODO: mode switch button
        });
        addStyledMessage(test, "TEST MODE INDICATOR", 200, largeElementHeight, fontSize, 200, 0, 10, largeElementOffset);
        addStyledMessage(test, "TEST CONN STATUS", 200, largeElementHeight, fontSize, 400, 0, 10, largeElementOffset);
        addStyledField(test, "TEST INPUT", 800, smallElementHeight, fontSize, 0, 500 - smallElementHeight,10, smallElementOffset, () -> {
            // TODO:
        });

        // Handle close
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // TODO: Handle safe disconnection here
                System.exit(0);
            }
        });
    }
}
