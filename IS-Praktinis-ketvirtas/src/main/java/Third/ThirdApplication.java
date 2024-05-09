package Third;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Arrays;
import java.math.BigInteger; // Import added


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

                        // Read signature as Base64 encoded string
                        String signatureBase64 = reader.readLine();
                        byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);

                        SwingUtilities.invokeLater(() -> {
                            publicKeyField.setText(publicKey);
                            messageField.setText(message);
                            signatureField.setText(signatureBase64);

                            boolean isValid = validateSignature(publicKey, message, signatureBytes);
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

    private static boolean validateSignature(String publicKey, String message, byte[] signatureBytes) {
        try {
            System.out.println("Received public key: " + publicKey);
            System.out.println("Received message: " + message);
            System.out.println("Received signature bytes: " + Arrays.toString(signatureBytes));

            // Split the publicKey into e and n
            String[] parts = publicKey.split(",");
            if (parts.length != 2) {
                return false; // Invalid publicKey format, return false
            }
            BigInteger e = new BigInteger(parts[0]);
            BigInteger n = new BigInteger(parts[1]);

            // Create RSAPublicKeySpec
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(n, e);

            // Generate PublicKey from keySpec
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey rsaPublicKey = keyFactory.generatePublic(keySpec);

            // Initialize Signature object with PublicKey
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(rsaPublicKey);

            // Update Signature object with the message
            signature.update(message.getBytes());

            // Verify the signature
            boolean isValid = signature.verify(signatureBytes);
            System.out.println("Signature Valid: " + isValid);
            return isValid;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | SignatureException | InvalidKeyException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        new ThirdApplication();
    }
}
