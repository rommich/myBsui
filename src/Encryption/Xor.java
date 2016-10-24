package Encryption;

import java.nio.charset.StandardCharsets;

/**
 * Created by Mich on 10/17/2016.
 */
public class Xor {
    public static String encrypt(String str, int secret) {
        byte[] byteArray = intToByteArray(secret);
        StringBuilder strBuilder = new StringBuilder();
        for (byte c : str.getBytes(StandardCharsets.UTF_8)) strBuilder.append((char) (c ^ byteArray[3]));
        return strBuilder.toString();
    }

    public static String decrypt(String str, int secret) {
        byte[] byteArray = intToByteArray(secret);
        StringBuilder strBuilder = new StringBuilder();
        for (byte c : str.getBytes(StandardCharsets.UTF_8)) strBuilder.append((char) (c ^ byteArray[3]));
        return strBuilder.toString();
    }

    private static final byte[] intToByteArray(int value) {
        return new byte[]{
                (byte) (value >>> 24),
                (byte) (value >>> 16),
                (byte) (value >>> 8),
                (byte) value};
    }
}
