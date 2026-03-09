package com.evgenii.rbac.util;

import java.net.PortUnreachableException;
import java.security.PublicKey;
import java.util.Locale;

public class ValidationUtils {

    private static final String USERNAME_PATTERN = "^[a-zA-Z0-9_]{3,20}$";
    private static final String EMAIL_PATTERN = "^[\\w-.]+@[\\w-]+\\.[a-z]{2,4}$";
    private static final String DATE_PATTERN = "^\\d{4}-\\d{2}-\\d{2}$";

    public static boolean isValidUsername(String username) {
        if (username == null) return false;
        return username.matches(USERNAME_PATTERN);
    }

    public static boolean isValidEmail (String email) {
        if (email == null) return false;
        return email.matches(EMAIL_PATTERN);
    }

    public static boolean isValidData (String date) {
        if (date == null) return false;
        return date.matches(DATE_PATTERN);
    }

    public static String normalizeString (String input) {
        if (input == null) return null;
        return input.trim().toLowerCase();
    }

    public static void requireNonEmpty (String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
    }
}
