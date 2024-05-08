package Third;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class ThirdApplication extends JFrame {
    private JTextField publicKeyField, messageField, signatureField;
    private JLabel validationResultLabel;

    public ThirdApplication() {
        super("Third Digital Signature Application");
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

        validationResultLabel = new JLabel(" ");
        mainPanel.add(validationResultLabel, c);

        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);

        startServer();
    }

    private void startServer() {
        Thread serverThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(8081)) {
                System.out.println("Server is listening on port 8081");
                while (true) {
                    try (Socket socket = serverSocket.accept();
                         BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                        String publicKey = reader.readLine();
                        String message = reader.readLine();
                        String signature = reader.readLine();

                        SwingUtilities.invokeLater(() -> {
                            publicKeyField.setText(publicKey);
                            messageField.setText(message);
                            signatureField.setText(signature);

                            boolean isValid = validateSignature(publicKey, message, signature);
                            validationResultLabel.setText("Signature Valid: " + isValid);
                        });
                    } catch (IOException e) {
                        System.out.println("Error handling client: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            } catch (IOException e) {
                System.out.println("Server exception: " + e.getMessage());
                e.printStackTrace();
            }
        });
        serverThread.start();
    }

    private static boolean validateSignature(String publicKey, String message, String signatureBase64) {
        try {
            String[] parts = publicKey.split(",");
            BigInteger e = new BigInteger(parts[0]);
            BigInteger n = new BigInteger(parts[1]);

            byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);
            BigInteger signatureBigInteger = new BigInteger(signatureBytes);

            BigInteger decryptedSignature = signatureBigInteger.modPow(e, n);

            byte[] decryptedSignatureBytes = decryptedSignature.toByteArray();

            byte[] messageBytes = message.getBytes();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] messageHash = digest.digest(messageBytes);

            return MessageDigest.isEqual(decryptedSignatureBytes, messageHash);
        } catch (NoSuchAlgorithmException | IllegalArgumentException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        new ThirdApplication();
    }
}
