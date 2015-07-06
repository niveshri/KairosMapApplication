package io.kairos.maps.utils;

public class Utils {
    public static String trimString(String str, int size) {
        if (str == null) return "";
        return str.length() <= size ? str : str.substring(0, size - 2) + "..";
    }

    public static String capitalize(String line) {
        if (line == null || line.length() <= 0) return "";
        if (line.length() == 1) return line.toUpperCase();

        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }

    public static String formatPhoneNumber(String phoneNumber) {
        if (phoneNumber.length() < 10) return phoneNumber;

        String trimmedString = phoneNumber.substring(phoneNumber.length() - 10);
        return "(" + trimmedString.substring(0, 3) + ")" +
                trimmedString.substring(3, 6) + "-" + trimmedString.substring(6);
    }
}
