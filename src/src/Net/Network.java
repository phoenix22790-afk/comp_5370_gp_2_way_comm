package Net;

import java.io.*;
import java.net.*;
import java.util.function.Consumer;


public class Network {

    public enum Mode { LISTENER, HOST }

    private Mode mode;
    private String host;
    private int port;
    private Consumer<String> onMessage;
    private Runnable onDisconnect;
    private volatile boolean reading = false;

    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public void setMode(Mode mode) {
        this.mode = mode;
    }
    public void setHost(String host) {
        this.host = host;
    }
    public void setPort(int port) {
        this.port = port;
    }


    public void start() throws IOException {
        if (mode == Mode.LISTENER) {
            serverSocket = new ServerSocket(port);
            System.out.println("Listening on port " + port + "...");
            serverSocket.setSoTimeout(10000);
            socket = serverSocket.accept();
            System.out.println("Accepted connection from " + socket.getRemoteSocketAddress());
        } else {
            socket = new Socket(host, port);
            System.out.println("Connected to " + host + ":" + port);
        }
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }
    public void startReading() {
        if (onMessage == null) throw new IllegalStateException("Call setOnMessage() first");
        if (reading) return;
        reading = true;
        Thread t = new Thread(this::readLoop, "net-reader");
        t.setDaemon(true);
        t.start();
    }

    private void readLoop() {
        try {
            String line;
            while (reading && (line = in.readLine()) != null) {
                onMessage.accept(line);
            }
        } catch (IOException ignored) {
            // socket closed by close() or a network error
        }
        boolean theyLeft = reading;   // still true only if close() wasn't called
        reading = false;
        if (theyLeft && onDisconnect != null) onDisconnect.run();
    }

    public void send(String message) {
        out.println(message);
    }


    public String receive() throws IOException {
        return in.readLine();
    }

    public void switchMode() {
        close();
        mode = (mode == Mode.LISTENER) ? Mode.HOST : Mode.LISTENER;
        System.out.println("Switched to " + mode + " mode");
    }

    public Mode getMode() {
        return mode;
    }

    public String getAddress() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public void close() {
        try {
            if (in != null) in.close();
        } catch (IOException ignored) {}
        if (out != null) out.close();
        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {}
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException ignored) {}
        in = null;
        out = null;
        socket = null;
        serverSocket = null;
        reading = false;
    }
}