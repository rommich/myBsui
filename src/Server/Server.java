package Server;

import Functions.Dif_Hel;
import com.google.gson.JsonObject;

import javax.crypto.spec.DHParameterSpec;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.AlgorithmParameterGenerator;
import java.security.AlgorithmParameters;
import java.util.ArrayList;


class Server {

    private ArrayList<SocketsHelper> socketArrayList = new ArrayList<SocketsHelper>();

    @SuppressWarnings("ConstantConditions")
    Server() {
        try {
            InetAddress iAddress = InetAddress.getLocalHost();
            String server_IP = iAddress.getHostAddress();
            System.out.println("Server IP address : " + server_IP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        int intSocket = 2343;
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(intSocket);
        } catch (IOException e) {
            System.err.println("Could not listen on port: " + intSocket);
        }
        try {//server listening to connection
            System.out.print("Listening for connection on socket: " + serverSocket.getLocalSocketAddress());
            while (true) {
                Socket server = serverSocket.accept();
                new Helper(server).start();
                socketArrayList.add((new SocketsHelper(server)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error");
        } finally {
            try {
                serverSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Server();
    }

    class SocketsHelper {
        private Socket s;
        private ArrayList<JsonObject> m = new ArrayList<JsonObject>();

        SocketsHelper(Socket _s) {
            s = _s;
        }
    }

    class Helper extends Thread {
        private final Socket socket;
        private JsonObject json;
        private BigInteger p;
        private BigInteger g;
        private BigInteger a;
        private BigInteger b;
        private BigInteger bSecretValue = new BigInteger("4");
        private BigInteger s;
        private short encryption = 0;

        public Helper(Socket clientSocket) {
            super();
            socket = clientSocket;
            try {
                AlgorithmParameterGenerator paramGen = AlgorithmParameterGenerator.getInstance("DH");
                paramGen.init(1024);
                AlgorithmParameters params = paramGen.generateParameters();
                DHParameterSpec dhSpec = params.getParameterSpec(DHParameterSpec.class);
                p = dhSpec.getP();
                g = dhSpec.getP();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            System.out.print("\n Accepted connetion from: " + socket.getInetAddress());
        }

        public void run() {
            while (true) {
                for (SocketsHelper sockets : socketArrayList) {
                    if (sockets.s.equals(socket)) {
                        for (JsonObject jsons : sockets.m) {
                            try {
                                Functions.JSONFunctions.sendJSONtoSocket(jsons, socket, encryption, s.intValue());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        sockets.m.clear();
                    }
                }
                try {
                    if (s == null)
                        json = Functions.JSONFunctions.getJSONfromSocket(socket);
                    else
                        json = Functions.JSONFunctions.getJSONfromSocket(socket, encryption, s.intValue());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //noinspection ConstantConditions
                if (json.has("msg")) {

                    for (SocketsHelper sockets : socketArrayList) {
                        sockets.m.add(json);
                    }

                }
                if (json.has("request")) {
                    JsonObject obj = new JsonObject();
                    obj.addProperty("p", p);
                    obj.addProperty("g", g);
                    System.out.print(obj.toString());
                    try {
                        Functions.JSONFunctions.sendJSONtoSocket(obj, socket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    obj = new JsonObject();
                    b = Dif_Hel.calculateMod(g, bSecretValue, p);
                    obj.addProperty("b", b);
                    try {
                        Functions.JSONFunctions.sendJSONtoSocket(obj, socket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (json.has("encryption")) {
                    String encr = json.get("encryption").getAsString();
                    if ("none".equals(encr)) {
                        encryption = 0;
                        System.out.print("enc :" + encryption);
                    } else if ("cezar".equals(encr)) {
                        encryption = 1;
                        System.out.print("enc :" + encryption);
                    } else if ("xor".equals(encr)) {
                        encryption = 2;
                        System.out.print("enc :" + encryption);
                    }
                }
                if (json.has("a")) {
                    a = json.get("a").getAsBigInteger();
                    s = Dif_Hel.calculateMod(a, bSecretValue, p);
                    System.out.print("p: " + p + " g: " + g + " a: " + a + " b: " + b + " s: " + s + " as: " + bSecretValue);
                }
            }
        }
    }
}
