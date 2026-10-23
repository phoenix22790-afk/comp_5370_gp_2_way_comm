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

        // Status Bar

        // mode indicator
        MessageBox modeBox = test.addStyledMessage("TEST MODE INDICATOR", 300, UIHandler.largeElementHeight, UIHandler.fontSize, 0, 0, 10, UIHandler.largeElementOffset);
        // connection status
        MessageBox statusBox = test.addStyledMessage("TEST CONN STATUS", 500, UIHandler.largeElementHeight, UIHandler.fontSize, 300, 0, 10, UIHandler.largeElementOffset);

        // Connection Bar

        // mode switch
        MessageBox modeButtonBox = test.addStyledButton("Change Mode", 160, UIHandler.smallElementHeight, UIHandler.fontSize, 0, UIHandler.largeElementHeight, 10, UIHandler.smallElementOffset, () -> {
            // TODO: mode switch button
        });
        // address input
        test.addStyledMessage("Addr:", 80, UIHandler.smallElementHeight, UIHandler.fontSize, 160, UIHandler.largeElementHeight, 10, UIHandler.smallElementOffset);
        test.addStyledField(240, UIHandler.smallElementHeight, UIHandler.fontSize, 160 + 80, UIHandler.largeElementHeight, new Consumer<String>() {
            public void accept(String str) {
                // TODO: do something
            }
        });
        // port input
        test.addStyledMessage("Port:", 80, UIHandler.smallElementHeight, UIHandler.fontSize, 160 + 80 + 240, UIHandler.largeElementHeight, 10, UIHandler.smallElementOffset);
        test.addStyledField(80, UIHandler.smallElementHeight, UIHandler.fontSize, 160 + 80 + 240 + 80, UIHandler.largeElementHeight, new Consumer<String>() {
            public void accept(String str) {
                // TODO: do something
            }
        });
        // connect
        MessageBox connectButtonBox = test.addStyledButton("Connect", 160, UIHandler.smallElementHeight, UIHandler.fontSize, 160 + 80 + 240 + 80 + 80, UIHandler.largeElementHeight, 10, UIHandler.smallElementOffset, () -> {
            // TODO: mode switch button
        });

        // Responses

        // response box
        MessageBox responseBox = test.addMessageBox(Color.WHITE, 800, 0, UIHandler.largeElementHeight + UIHandler.smallElementHeight);

        // Message Input Bar

        // input box
        test.addStyledMessage("Message:", 120, UIHandler.smallElementHeight, UIHandler.fontSize, 0, 500 - UIHandler.smallElementHeight, 10, UIHandler.smallElementOffset);
        test.addStyledField(680, UIHandler.smallElementHeight, UIHandler.fontSize, 120, 500 - UIHandler.smallElementHeight, new Consumer<String>() {
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
