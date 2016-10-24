package Functions;

import Encryption.Cezar;
import Encryption.Xor;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class JSONFunctions {

    public static String base64Encode(String token) {
        byte[] encodedBytes = Base64.getEncoder().encode(token.getBytes());
        return new String(encodedBytes, Charset.forName("UTF-8"));
    }

    public static String base64Decode(String token) {
        byte[] decodedBytes = Base64.getDecoder().decode(token.getBytes());
        return new String(decodedBytes, Charset.forName("UTF-8"));
    }

    public static JsonObject getJSONfromSocket(Socket socket) throws Exception {
        String message = new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine();
        if (message.isEmpty())
            return null;
        else
            return new JsonParser().parse(message).getAsJsonObject();
    }

    public static void sendJSONtoSocket(JsonObject object, Socket socket) throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        out.write(object.toString());
        out.newLine();
        out.flush();
    }

    public static JsonObject getJSONfromSocket(Socket socket, short encryption, int secret) throws Exception {
        String message = new BufferedReader(new InputStreamReader(socket.getInputStream())).readLine();
        switch (encryption) {
            case 0:
                break;
            case 1:
                message = Cezar.decrypt(message, secret);
                break;
            case 2:
                message = Xor.decrypt(message, secret);
                break;
            default:
                System.out.print("message error");
        }
        if (message.isEmpty())
            return null;
        else
            return new JsonParser().parse(message).getAsJsonObject();
    }

    public static void sendJSONtoSocket(JsonObject object, Socket socket, short encryption, int secret) throws IOException {
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
        switch (encryption) {
            case 0:
                out.write(object.toString());
                break;
            case 1:
                out.write(Cezar.encrypt(object.toString(), secret));
                break;
            case 2:
                out.write(Xor.encrypt(object.toString(), secret));
                break;
            default:
                System.out.print("message error");
        }
        out.newLine();
        out.flush();
    }
}
