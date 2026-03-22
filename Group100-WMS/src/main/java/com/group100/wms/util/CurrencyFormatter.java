package com.group100.wms.util;

import java.text.NumberFormat;
import java.util.Locale;

// OOP Concepts Used:
// Encapsulation - Currency formatting logic is contained within this utility class
// Abstraction - Provides simple methods to format currency without exposing internal formatting logic
// Polymorphism - Different methods format different currencies (LKR, USD)
// Inheritance - Uses Java built-in classes like NumberFormat and Locale which are part of class hierarchies

public final class CurrencyFormatter {

    // Private constructor to prevent instantiation (utility class)
    private CurrencyFormatter() {}

    // Locale representing Sri Lanka for currency formatting
    private static final Locale LKR_LOCALE = new Locale("si", "LK");

    // Formats a double value into Sri Lankan Rupees (LKR)
    public static String formatLKR(double amount) {
        return String.format("LKR %,.2f", amount);
    }

    // Formats a double value into US Dollars (USD) using built-in NumberFormat
    public static String formatUSD(double amount) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(amount);
    }

    // Default currency formatter (currently uses LKR)
    public static String format(double amount) {
        return formatLKR(amount);
    }
}
