package Second;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

public class Server extends Thread {
    private int port;
    private SecondApplication app;

    public Server(int port, SecondApplication app) {
        this.port = port;
        this.app = app;
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New client connected");

                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String publicKey = input.readLine();
                String message = input.readLine();
                String signature = input.readLine();

                app.updateFields(publicKey, message, signature);
            }
        } catch (IOException ex) {
            System.out.println("Server exception: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}
