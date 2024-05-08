package First;

import java.io.*;
import java.net.*;

public class Client {
    private Socket socket = null;
    private OutputStream out = null;

    public Client(String address, int port) {
        try {
            socket = new Socket(address, port);
            out = socket.getOutputStream();
        } catch (UnknownHostException u) {
            System.out.println(u);
        } catch (IOException i) {
            System.out.println(i);
        }
    }

    public void sendMessage(String publicKey, String message, byte[] signature) {
        try {
            String signatureStr = RSA.bytesToString(signature);
            String dataToSend = publicKey + "\n" + message + "\n" + signatureStr;
            out.write(dataToSend.getBytes());
            out.flush();
        } catch (IOException i) {
            System.out.println("Failed to send message: " + i.getMessage());
        }
    }

    public void closeConnection() {
        try {
            out.close();
            socket.close();
        } catch (IOException i) {
            System.out.println(i);
        }
    }
}
