package de.wif.it;

import java.io.IOException;
import java.net.*;


/**
 * UDP Client
 */
public class Client extends Thread {
    private DatagramSocket socket;
    private InetAddress address;
    private int port;
    private String user;

    /**
     * Creates an instance of my wonderful client.
     * @param IPAdress
     * @param port
     * @throws SocketException
     * @throws UnknownHostException
     */
    public Client(String user, String IPAdress, int port) throws SocketException, UnknownHostException {
        socket = new DatagramSocket();
        address = InetAddress.getByName(IPAdress);
        this.port = port;
        this.user = user;
    }

    /**
     * Sends a message. Format @<username> @<from> <message>
     * @param msg
     * @return
     */
    private String sendMsg(String msg) {
        byte[] buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        try {
            socket.send(packet);
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            String received = new String(packet.getData(), 0, packet.getLength());
            return received;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Fetches the last messages.
     * @param msg
     * @return
     */
    public String getMsgs() {
        byte[] buf = ("GET @"+user).getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        try {
            socket.send(packet);
            buf =  new byte[256];
            packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            String received = new String(
                    packet.getData(), 0, packet.getLength());
            return received;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Close everything.
     */
    public void close() {
        socket.close();
        System.out.println("Client closed!");
    }

    /**
     * My stupid test method.
     */
    public void run() {
        System.out.println(sendMsg("@Test @" + user +  " Hallo"));
        System.out.println(getMsgs());
        System.out.println(sendMsg("@Test1 @" + user +  " Hallo"));
        System.out.println(sendMsg("@Test @" + user +  " Hallo"));
        System.out.println(getMsgs());
        System.out.println(sendMsg("@Test2 @" + user +  " Hallo"));
        System.out.println(sendMsg("@Test @" + user +  " Hallo"));

        System.out.println(getMsgs());
    }

    /**
     * Run the test.
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        Client client1 = new Client("Test", "localhost", 5555);
        Client client2 = new Client("Test1", "localhost", 5555);
        client1.start();
        client2.start();
        client1.wait();
        client2.wait();
        client1.close();
        client2.close();
    }
}