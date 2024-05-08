package First;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FirstApplication extends JFrame {
    private JTextField inputField;
    private JButton signButton;
    private JTextArea resultArea;
    private RSA rsa;

    public FirstApplication() {
        super("Digital Signature Application");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        inputField = new JTextField(20);
        signButton = new JButton("Sign and Send");
        resultArea = new JTextArea();
        resultArea.setEditable(false);

        rsa = new RSA();

        signButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String message = inputField.getText();
                byte[] messageBytes = message.getBytes();
                byte[] signature = rsa.sign(messageBytes);
                String publicKey = rsa.getPublicKey();

                Client client = new Client("localhost", 8080);
                client.sendMessage(publicKey, message, signature);
                client.closeConnection();

                resultArea.setText("Message and signature sent!");
            }
        });


        JPanel panel = new JPanel();
        panel.add(inputField);
        panel.add(signButton);
        panel.add(new JScrollPane(resultArea));

        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new FirstApplication();
    }
}
