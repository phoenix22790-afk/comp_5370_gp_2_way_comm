import java.util.Scanner;

import Net.*;
import Code.*;

public class TempCommandLineApp {
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);
        Network net = new Network();

        System.out.print("(L)istener or (H)ost?: ");
        if (sc.nextLine().toUpperCase().startsWith("H")) {
            net.setMode(Network.Mode.HOST);
            System.out.print("ip: ");
            net.setHost(sc.nextLine());
        } else {
            net.setMode(Network.Mode.LISTENER);
        }
        System.out.print("port: ");
        net.setPort(Integer.parseInt(sc.nextLine()));
        net.start();

        // key exchanges
        CodeHandler crypto = new CodeHandler();
        String[] mine = crypto.getPublicCoords();
        net.send(mine[0] + "," + mine[1]);
        String[] theirs = net.receive().split(",");
        crypto.setOther_calcShared(theirs[0], theirs[1]);
        System.out.println("connected");

        // reader
        new Thread(() -> {
            try {
                String line;
                while ((line = net.receive()) != null) {
                    System.out.println("[peer] " + crypto.decoder(line));
                }
            } catch (Exception e) {}
            System.out.println("peer left");
            System.exit(0);
        }).start();

        // writer
        while (sc.hasNextLine()) {
            String text = sc.nextLine();
            if (text.equals("/quit")) break;
            net.send(crypto.encoder(text));
        }
        net.close();
        System.exit(0);
    }
}
