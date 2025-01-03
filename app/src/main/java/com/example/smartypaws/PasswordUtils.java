package com.example.smartypaws;

public class PasswordUtils {
    public static boolean isStrongPassword(String password) {
        if (password.length() < 8) {
            return false;
        }
        boolean hasUpperCase = false, hasLowerCase = false, hasDigit = false, hasSpecialChar = false;

        String specialChars = "!@#$%^&*()_-+{}[]\\|'\"<>?,./";

        for (char ch : password.toCharArray()) {
            if (Character.isUpperCase(ch)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(ch)) {
                hasLowerCase = true;
            } else if (Character.isDigit(ch)) {
                hasDigit = true;
            } else if (specialChars.contains(String.valueOf(ch))) {
                hasSpecialChar = true;
            }

        }
        return hasUpperCase && hasLowerCase && hasSpecialChar && hasDigit;
    }
}
