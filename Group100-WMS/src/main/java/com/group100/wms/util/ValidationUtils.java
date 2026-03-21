package com.group100.wms.util;

public final class ValidationUtils {
    private ValidationUtils() {}

    public static boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    public static boolean isValidEmail(String email) {
        if (isNullOrBlank(email)) return false;
        return email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");
    }

    public static boolean isValidPhone(String phone) {
        if (isNullOrBlank(phone)) return false;
        return phone.matches("^[0-9+\\-\\s]{7,15}$");
    }

    public static boolean isValidNic(String nic) {
        if (isNullOrBlank(nic)) return false;
        return nic.matches("^[0-9]{9}[VvXx]$") || nic.matches("^[0-9]{12}$");
    }

    public static boolean isPositiveDouble(String value) {
        try {
            return Double.parseDouble(value) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isPositiveInt(String value) {
        try {
            return Integer.parseInt(value) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}