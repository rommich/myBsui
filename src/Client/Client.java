package Client;

import Functions.Dif_Hel;
import Functions.JSONFunctions;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;

public class Client {
    private final BigInteger aSecretValue = new BigInteger("6");
    private JTextField textFieldHost;
    private JPanel panel1;
    private JButton connectButton;
    private JTextArea textArea1;
    private JTextField textFieldMessage;
    private JButton sendButton;
    private JFormattedTextField formattedTextFieldPort;
    private JLabel valueLabel;
    private JTextField textFieldName;
    private JButton nameButton;
    private JRadioButton noneRadioButton;
    private JRadioButton cezarRadioButton;
    private JRadioButton xorRadioButton;
    private BigInteger p;
    private BigInteger g;
    private BigInteger a;
    private BigInteger b;
    private BigInteger s;
    private Socket clientSocket = null;
    private String name;
    private short encryption = 0;

    public Client() {
        connectButton.addActionListener(e -> {
            int port = Integer.parseInt(formattedTextFieldPort.getText());
            String host = textFieldHost.getText();
            try {
                clientSocket = new Socket(host, port);
                sendButton.setEnabled(true);
                new Listener(clientSocket).start();
                JsonObject obj = new JsonObject();
                obj.addProperty("request", "keys");
                JSONFunctions.sendJSONtoSocket(obj, clientSocket);
                System.out.print("Connected to server");
            } catch (IOException ex) {
                ex.printStackTrace();
                System.out.println("Error");
            }
        });

        sendButton.addActionListener(e -> {
            JsonObject obj = new JsonObject();
            obj.addProperty("msg", JSONFunctions.base64Encode(textFieldMessage.getText()));
            obj.addProperty("from", name);
            try {
                JSONFunctions.sendJSONtoSocket(obj, clientSocket, encryption, s.intValue());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            System.out.print("Sent");
        });

        nameButton.addActionListener(e -> {
            name = textFieldName.getText();
        });
        noneRadioButton.addActionListener(e -> {
            if (noneRadioButton.isEnabled()) {
                JsonObject obj = new JsonObject();
                obj.addProperty("encryption", "none");
                try {
                    JSONFunctions.sendJSONtoSocket(obj, clientSocket, encryption, s.intValue());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                encryption = 0;
            }
        });
        cezarRadioButton.addActionListener(e -> {
            JsonObject obj = new JsonObject();
            obj.addProperty("encryption", "cezar");
            try {
                JSONFunctions.sendJSONtoSocket(obj, clientSocket, encryption, s.intValue());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            encryption = 1;
        });
        xorRadioButton.addActionListener(e -> {
            JsonObject obj = new JsonObject();
            obj.addProperty("encryption", "xor");
            try {
                JSONFunctions.sendJSONtoSocket(obj, clientSocket, encryption, s.intValue());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            encryption = 2;
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Client");
        frame.setContentPane(new Client().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public void CreateWindow() {
        JFrame frame = new JFrame("Client");
        frame.setContentPane(new Client().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    @SuppressWarnings("UnusedAssignment")
    class Listener extends Thread {
        private final Socket socket;
        private JsonObject json;

        public Listener(Socket clientSocket) {
            super();
            socket = clientSocket;
            System.out.print(socket.getInetAddress());
        }

        public void run() {
            while (true) {
                try {
                    if (s == null)
                        json = JSONFunctions.getJSONfromSocket(socket);
                    else
                        json = JSONFunctions.getJSONfromSocket(socket, encryption, s.intValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (json.has("msg") && json.has("from")) {
                    textArea1.append(json.get("from") + " " + JSONFunctions.base64Decode(json.get("msg").getAsString()) + "\n");
                }
                if (json.has("p") && json.has("g")) {
                    p = json.get("p").getAsBigInteger();
                    g = json.get("g").getAsBigInteger();
                    a = Dif_Hel.calculateMod(g, aSecretValue, p);
                    JsonObject obj = new JsonObject();
                    obj.addProperty("a", a);
                    try {
                        JSONFunctions.sendJSONtoSocket(obj, socket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (json.has("b")) {
                    b = json.get("b").getAsBigInteger();
                    s = Dif_Hel.calculateMod(b, aSecretValue, p);
                }
            }
        }
    }
}
