package com.group100.wms.util;

import java.text.NumberFormat;
import java.util.Locale;

public final class CurrencyFormatter {
    private CurrencyFormatter() {}

    private static final Locale LKR_LOCALE = new Locale("si", "LK");

    public static String formatLKR(double amount) {
        return String.format("LKR %,.2f", amount);
    }

    public static String formatUSD(double amount) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(amount);
    }

    public static String format(double amount) {
        return formatLKR(amount);
    }
}