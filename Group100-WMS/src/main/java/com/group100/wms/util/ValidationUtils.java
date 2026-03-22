package com.group100.wms.util;

// OOP Concepts Used:
// Encapsulation - Validation logic is contained within this utility class
// Abstraction - Complex validation rules are simplified into easy-to-use methods
// Polymorphism - Different methods validate different data types (String, numeric values)
// Inheritance - Uses built-in Java classes like String and Exception which are part of class hierarchies

public final class ValidationUtils {

    // Private constructor to prevent instantiation (utility class)
    private ValidationUtils() {}

    // Checks if a string is null or contains only whitespace
    public static boolean isNullOrBlank(String value) {
        return value == null || value.isBlank();
    }

    // Validates if the given string is a properly formatted email address
    public static boolean isValidEmail(String email) {
        if (isNullOrBlank(email)) return false;
        return email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");
    }

    // Validates if the given string is a valid phone number (digits, +, -, spaces allowed)
    public static boolean isValidPhone(String phone) {
        if (isNullOrBlank(phone)) return false;
        return phone.matches("^[0-9+\\-\\s]{7,15}$");
    }

    // Validates Sri Lankan NIC number (old and new formats)
    public static boolean isValidNic(String nic) {
        if (isNullOrBlank(nic)) return false;
        return nic.matches("^[0-9]{9}[VvXx]$") || nic.matches("^[0-9]{12}$");
    }

    // Checks if a string represents a positive double value
    public static boolean isPositiveDouble(String value) {
        try {
            return Double.parseDouble(value) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Checks if a string represents a positive integer value
    public static boolean isPositiveInt(String value) {
        try {
            return Integer.parseInt(value) > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
