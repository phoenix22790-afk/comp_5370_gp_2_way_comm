import java.io.*;
import java.net.*;


public class Network_App {
    public enum Mode { LISTENER, HOST }

    private Mode mode;
    private String host;
    private int port;

    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public Network_App(Mode mode, String host, int port) {
        this.mode = mode;
        this.host = host;
        this.port = port;
    }

    public void start() throws IOException {
        if (mode == Mode.LISTENER) {
            serverSocket = new ServerSocket(port);
            System.out.println("Listening on port " + port + "...");
            socket = serverSocket.accept();
            System.out.println("Accepted connection from " + socket.getRemoteSocketAddress());
        } else {
            socket = new Socket(host, port);
            System.out.println("Connected to " + host + ":" + port);
        }
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
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

    public void close() {
        try { if (in != null) in.close(); } catch (IOException ignored) {}
        if (out != null) out.close();
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
        try { if (serverSocket != null) serverSocket.close(); } catch (IOException ignored) {}
        in = null; out = null; socket = null; serverSocket = null;
    }
}
