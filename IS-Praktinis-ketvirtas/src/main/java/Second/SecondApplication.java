package Second;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;
import java.io.OutputStream;
import java.util.Base64;


public class SecondApplication extends JFrame {
    private JTextField publicKeyField, messageField, signatureField;
    private JButton editSignatureButton, sendButton;

    public SecondApplication() {
        super("Second Digital Signature Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 10, 10, 10);

        publicKeyField = new JTextField(20);
        publicKeyField.setEditable(false);
        mainPanel.add(new JLabel("Public Key:"), c);
        mainPanel.add(publicKeyField, c);

        messageField = new JTextField(20);
        messageField.setEditable(false);
        mainPanel.add(new JLabel("Message:"), c);
        mainPanel.add(messageField, c);

        signatureField = new JTextField(20);
        signatureField.setEditable(false);
        mainPanel.add(new JLabel("Digital Signature:"), c);
        mainPanel.add(signatureField, c);

        editSignatureButton = new JButton("Edit Signature");
        editSignatureButton.addActionListener(e -> signatureField.setEditable(true));
        mainPanel.add(editSignatureButton, c);

        sendButton = new JButton("Send Data");
        sendButton.addActionListener(e -> sendData());
        mainPanel.add(sendButton, c);

        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);

        startServer();
    }

    private void startServer() {
        Server server = new Server(8080, this);
        server.start();
    }

    public void updateFields(String publicKey, String message, String signature) {
        publicKeyField.setText(publicKey);
        messageField.setText(message);

        // Decode Base64 signature back to byte array
        byte[] signatureBytes = Base64.getDecoder().decode(signature);
        signatureField.setText(Base64.getEncoder().encodeToString(signatureBytes)); // Display as Base64 string
    }



    private void sendData() {
        try (Socket socket = new Socket("localhost", 8081);
             OutputStream out = socket.getOutputStream()) {
            out.write(publicKeyField.getText().getBytes());
            out.write("\n".getBytes());
            out.write(messageField.getText().getBytes());
            out.write("\n".getBytes());

            // Send signature data as Base64 encoded string
            out.write(signatureField.getText().getBytes());
            out.flush();
        } catch (IOException e) {
            System.out.println("Error sending data: " + e.getMessage());
            e.printStackTrace();
        }
    }



    public static void main(String[] args) {
        new SecondApplication();
    }
}
