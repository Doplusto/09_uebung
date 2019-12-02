package de.wif.it;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Server extends Thread {

    private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];

    // user map
    private HashMap<String, List<String>> messages;

    public Server(int port) throws SocketException {
        socket = new DatagramSocket(port);
        messages = new HashMap<>();
    }

    /**
     * Run and run and run the server
     */
    public void run() {
        running = true;

        while (running) {
            Arrays.fill(buf,(byte)0);
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                // Nachricht entgegennehmen
                socket.receive(packet);
                InetAddress address = packet.getAddress();
                int port = packet.getPort();
                String received  = new String(packet.getData(), packet.getOffset(), packet.getLength());

                // eventuell muss hier eine Nachricht zurückgeschickt werden
                byte[] msg = processMsg(received).getBytes();
                DatagramPacket replayPacket = new DatagramPacket(msg, msg.length, address, port);
                socket.send(replayPacket);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Server closed!");
        socket.close();
    }

    /**
     * The method processes the incoming messages
     * @param msg
     */
    private String processMsg(String msg) {
        msg = msg.trim();

        // send protocol
        if (msg.startsWith("@")) {
            String user = msg.substring(0, msg.indexOf(" "));
            List<String> msgs = messages.getOrDefault(user, new ArrayList<>());
            msgs.add(msg.substring(msg.indexOf(" "), msg.length()));
            messages.put(user, msgs);
            return "200: Success";
        }

        // get protocol
        if  (msg.toLowerCase().startsWith("get")) {
            String user = msg.substring(msg.indexOf("@"), msg.length());
            List<String> list = messages.get(user);
            String reply = "";
            if (list != null)
                 reply =  String.join("\n\r", list);
            messages.remove(user);
            return reply;
        }
        return "500 : internal Error";
    }

    /**
     * Just run the server.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Server s = new Server(5555);
        s.start();
    }
}