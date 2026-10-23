import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

import UI.MessageBox;
import UI.UIHandler;

public class UI_App {
    public static void main(String[] args) throws Exception {
        UIHandler test = new UIHandler();
        JFrame frame = new JFrame();
        test.setPreferredSize(new Dimension(800, 500));
        frame.add(test);
        frame.pack();
        frame.setVisible(true);

        // Header Bar

        // mode switch
        MessageBox modeButtonBox = test.addStyledButton("TEST MODE SWITCH", 200, UIHandler.largeElementHeight, UIHandler.fontSize, 0, 0, 10, UIHandler.largeElementOffset, () -> {
            // TODO: mode switch button
        });
        // mode indicator
        MessageBox modeBox = test.addStyledMessage("TEST MODE INDICATOR", 200, UIHandler.largeElementHeight, UIHandler.fontSize, 200, 0, 10, UIHandler.largeElementOffset);
        // connection status
        MessageBox statusBox = test.addStyledMessage("TEST CONN STATUS", 200, UIHandler.largeElementHeight, UIHandler.fontSize, 400, 0, 10, UIHandler.largeElementOffset);

        // Responses

        // response box
        MessageBox responseBox = test.addMessageBox(Color.WHITE, 800, 0, UIHandler.largeElementHeight);

        // Message Input Bar

        // input box
        test.addStyledField(800, UIHandler.smallElementHeight, UIHandler.fontSize, 0, 500 - UIHandler.smallElementHeight, new Consumer<String>() {
            public void accept(String str) {
                // TODO: do something
            }
        });

        // Window Handler

        // close handler
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                // TODO: Handle safe disconnection here
                System.exit(0);
            }
        });
    }
}
