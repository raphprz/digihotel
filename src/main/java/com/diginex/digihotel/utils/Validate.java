package com.diginex.digihotel.utils;

public class Validate {

    public static <T> void notNull(T object, String message, Object... values) {
        if (object == null) {
            throw new NullPointerException(String.format(message, values));
        }
    }

    public static void isTrue(boolean expression, String message, long value) {
        if (!expression) {
            throw new IllegalArgumentException(String.format(message, value));
        }
    }

    public static void isValidString(String str, String message, Object... values) {
        if (str == null || str.isEmpty()) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

}
