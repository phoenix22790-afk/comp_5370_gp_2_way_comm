import javax.swing.*;

import Net.Network;
import Code.CodeHandler;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.function.Consumer;

import UI.MessageBox;
import UI.UIHandler;

public class UI_App {
    static boolean connectFlag = false;
    static boolean connected = false;
    static String messages = "";
    static String newMessage = null;

    public static void main(String[] args) throws Exception {
        Network conn = new Network();
        conn.setMode(Network.Mode.LISTENER); //default
        conn.setPort(-1); //force port to be set
        UIHandler test = new UIHandler();
        JFrame frame = new JFrame();
        test.setPreferredSize(new Dimension(800, 500));
        frame.add(test);
        frame.pack();
        frame.setVisible(true);

        // Responses

        // response box
        MessageBox responseBox = test.addMessageBox(Color.WHITE, UIHandler.fontSize, 0, 500 - UIHandler.smallElementHeight);

        // Status Bar

        // mode indicator
        MessageBox modeBox = test.addStyledMessage("Mode: LISTENER", 300, UIHandler.largeElementHeight, UIHandler.fontSize, 0, 0, 10, UIHandler.largeElementOffset);
        // connection status
        MessageBox statusBox = test.addStyledMessage("Hello!", 500, UIHandler.largeElementHeight, UIHandler.fontSize, 300, 0, 10, UIHandler.largeElementOffset);

        // Connection Bar

        // mode switch
        test.addStyledButton("Change Mode", 160, UIHandler.smallElementHeight, UIHandler.fontSize, 0, UIHandler.largeElementHeight, 10, UIHandler.smallElementOffset, () -> {
            connected = false;
            conn.switchMode();
            if (conn.getMode() == Network.Mode.LISTENER) {
                modeBox.setMessage("Mode: LISTENER");
                statusBox.setMessage("Mode updated to LISTENER");
            }
            else {
                modeBox.setMessage("Mode: HOST");
                statusBox.setMessage("Mode updated to HOST");
            }
        });
        // address input
        test.addStyledMessage("Addr:", 80, UIHandler.smallElementHeight, UIHandler.fontSize, 160, UIHandler.largeElementHeight, 10, UIHandler.smallElementOffset);
        test.addStyledField(240, UIHandler.smallElementHeight, UIHandler.fontSize, 160 + 80, UIHandler.largeElementHeight, new Consumer<String>() {
            public void accept(String str) {
                conn.setHost(str);
                statusBox.setMessage("Address updated to " + str);
            }
        });
        // port input
        test.addStyledMessage("Port:", 80, UIHandler.smallElementHeight, UIHandler.fontSize, 160 + 80 + 240, UIHandler.largeElementHeight, 10, UIHandler.smallElementOffset);
        test.addStyledField(80, UIHandler.smallElementHeight, UIHandler.fontSize, 160 + 80 + 240 + 80, UIHandler.largeElementHeight, new Consumer<String>() {
            public void accept(String str) {
                try {
                   int port = Integer.parseInt(str);
                   if (port >= 0 && port <= 65535) {
                        conn.setPort(port);
                        statusBox.setMessage("Port updated to " + str);
                   }
                }
                catch (NumberFormatException e) {
                    statusBox.setMessage("Please Input a Valid Port..");
                    return;
                }
            }
        });
        // connect
        MessageBox connectButtonBox = test.addStyledButton("Connect", 160, UIHandler.smallElementHeight, UIHandler.fontSize, 160 + 80 + 240 + 80 + 80, UIHandler.largeElementHeight, 10, UIHandler.smallElementOffset, () -> {
            if (connectFlag) {
                return;
            }
            if (connected) {
                conn.close();
                connected = false;
                return;
            }
            if (conn.getMode() == Network.Mode.HOST && conn.getAddress() == null) {
                statusBox.setMessage("Please Input an Address..");
                return;
            }
            if (conn.getPort() == -1) {
                statusBox.setMessage("Please Input a Port..");
                return;
            }
            connectFlag = true;
        });

        // Message Input Bar

        // input box
        test.addStyledMessage("Message:", 120, UIHandler.smallElementHeight, UIHandler.fontSize, 0, 500 - UIHandler.smallElementHeight, 10, UIHandler.smallElementOffset);
        test.addStyledField(680, UIHandler.smallElementHeight, UIHandler.fontSize, 120, 500 - UIHandler.smallElementHeight, new Consumer<String>() {
            public void accept(String str) {
                if (!connected) {
                    statusBox.setMessage("Please Start a Connection..");
                    return;
                }
                newMessage = str;
            }
        });

        // Window Handler

        // close handler
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                conn.close();
                System.exit(0);
            }
        });

        // Done with UI setup, run loop here:
        while (true) {
            if (connectFlag) {
                System.out.println("try connect");
                connected = false;
                try {
                    conn.close();
                    if (conn.getMode() == Network.Mode.LISTENER)
                        statusBox.setMessage("Waiting for connection on port: " + conn.getPort() + "...");
                    else
                        statusBox.setMessage("Attempting connection to " + conn.getAddress() + ", port: " + conn.getPort() + "...");
                    conn.start();
                    connectFlag = false;
                } catch (IOException e) {
                    statusBox.setMessage(e.toString());
                    e.printStackTrace();
                    connectFlag = false;
                    continue;
                }
                connectButtonBox.setMessage("Disconnect");
                if (conn.getMode() == Network.Mode.LISTENER)
                    statusBox.setMessage("Connected on port: " + conn.getPort());
                else
                    statusBox.setMessage("Connected to " + conn.getAddress() + ", port: " + conn.getPort());
                connected = true;

                // key exchanges
                CodeHandler crypto = new CodeHandler();
                String[] mine = crypto.getPublicCoords();
                conn.send(mine[0] + "," + mine[1]);
                String code = null;
                System.out.println("wait for code");
                boolean failed = false;
                while (code == null) {
                    Thread.sleep(10);
                    code = conn.receiveIfReady();
                    if (!connected || conn.isClosed()) {
                        System.out.println("fail");
                        failed = true;
                        break;
                    }
                }
                if (failed) {
                    continue;
                }
                String[] theirs = code.split(",");
                crypto.setOther_calcShared(theirs[0], theirs[1]);
                System.out.println("connected");

                // reader
                new Thread(() -> {
                    try {
                        String line;
                        while (connected) {
                            if ((line = conn.receiveIfReady()) != null)
                            {
                                String decoded = crypto.decoder(line);
                                System.out.println("[peer] " + decoded);
                                // add response
                                messages += "Peer: " + decoded + System.lineSeparator() + System.lineSeparator();
                                responseBox.setMessage(messages);
                            }
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                break;
                            }
                        }
                    } catch (IOException e) {
                        statusBox.setMessage(e.toString());
                        e.printStackTrace();
                    }
                    connected = false;
                    System.out.println("You left");
                    statusBox.setMessage("You left");
                }).start();

                new Thread(() -> {
                    while (connected) {
                        if (newMessage != null) {
                            conn.send(crypto.encoder(newMessage));
                            // add response
                            messages += "You: " + newMessage + System.lineSeparator() + System.lineSeparator();
                            responseBox.setMessage(messages);
                            newMessage = null;
                        }
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }).start();
            }
            if (connected && conn.isClosed()) {
                conn.close();
                connected = false;
                System.out.println("Peer left");
                statusBox.setMessage("Peer left");
            }
            if (!connected) {
                connectButtonBox.setMessage("Connect");
            }
            Thread.sleep(10);
        }
    }
}
