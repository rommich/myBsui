package Encryption;

/**
 * Created by Mich on 10/17/2016.
 */
public class Cezar {
    public static String encrypt(String str, int shift) {
        int length = str.length();
        StringBuilder strBuilder = new StringBuilder();
        char c;
        for (int i = 0; i < length; i++) {
            c = str.charAt(i);
            if (Character.isLetter(c)) {
                c = (char) (str.charAt(i) + shift);
                if ((Character.isLowerCase(str.charAt(i)) && c > 'z') || (Character.isUpperCase(str.charAt(i)) && c > 'Z'))
                    c = (char) (str.charAt(i) - (26 - shift));
            }
            strBuilder.append(c);
        }
        return strBuilder.toString();
    }

    public static String decrypt(String str, int shift) {
        int length = str.length();
        StringBuilder strBuilder = new StringBuilder();
        char c;
        for (int i = 0; i < length; i++) {
            c = str.charAt(i);
            if (Character.isLetter(c)) {
                c = (char) (str.charAt(i) - shift);
                if ((Character.isLowerCase(str.charAt(i)) && c < 'a') || (Character.isUpperCase(str.charAt(i)) && c < 'A'))
                    c = (char) (str.charAt(i) + (26 - shift));
            }
            strBuilder.append(c);
        }
        return strBuilder.toString();
    }
}
