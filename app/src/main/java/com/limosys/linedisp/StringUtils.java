package com.limosys.linedisp;

import java.util.Random;

public class StringUtils {

    private StringUtils() {
    }

    public static boolean isNumeric(String str) {
        String regex = "[0-9]+";

        return str == null ? false : str.matches(regex);
    }

    public static boolean startsWith(String source, String pattern) {
        if (pattern == null || pattern.isEmpty() || source == null)
            return false;

        return source.startsWith(pattern);
    }

    public static boolean startsWithAny(String source, String[] patterns) {
        if (source == null || patterns == null || patterns.length == 0)
            return false;

        for (int i = 0; i < patterns.length; i++) {
            if (source.startsWith(patterns[i]))
                return true;
        }
        return false;
    }

    private static String randomStringData = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String randomString(int length, boolean letters, boolean digits) {
        if (length <= 0 || (!letters && !digits))
            return "";

        Random r = new Random();
        int offset = !letters ? 52 : 0;
        int randomMax = letters && digits ? 62 : letters && !digits ? 52 : !letters && digits ? 10 : 0;

        char[] resChar = new char[length];
        int index = 0;
        for (int i = 0; i < length; i++) {
            index = r.nextInt(randomMax) + offset;
            char ch = randomStringData.charAt(index);
            resChar[i] = ch;
        }

        String res = String.valueOf(resChar);

        return res;
    }

    public static String removeEnd(String input, String pattern) {
        if (input == null)
            return null;

        if (pattern == null || pattern.isEmpty())
            return input;

        if (input.endsWith(pattern)) {
            return input.substring(0, input.length() - pattern.length());
        } else {
            return input;
        }
    }

}
