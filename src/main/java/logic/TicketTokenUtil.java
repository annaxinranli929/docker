package logic;

import java.security.SecureRandom;

public class TicketTokenUtil {
    private static final SecureRandom RND = new SecureRandom();
    private static final char[] CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

    public static String newToken(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(CHARS[RND.nextInt(CHARS.length)]);
        }
        return sb.toString();
    }
}
