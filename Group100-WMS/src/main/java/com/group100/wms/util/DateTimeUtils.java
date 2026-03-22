package com.group100.wms.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// OOP Concepts Used:
// Encapsulation - Date formatting and parsing logic is contained within this utility class
// Abstraction - Simplifies complex date/time operations into easy-to-use methods
// Polymorphism - Different methods handle different input types (LocalDate vs LocalDateTime)
// Inheritance - Uses Java Time API classes which extend core Java classes

public final class DateTimeUtils {

    // Private constructor to prevent instantiation (utility class)
    private DateTimeUtils() {}

    // Formatter used for date (e.g., 23/03/2026)
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Formatter used for date and time (e.g., 23/03/2026 14:30)
    private static final DateTimeFormatter DATETIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // Formats a LocalDate into a string, returns empty string if null
    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMAT) : "";
    }

    // Formats a LocalDateTime into a string, returns empty string if null
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATETIME_FORMAT) : "";
    }

    // Parses a string into a LocalDate using defined format
    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, DATE_FORMAT);
    }

    // Returns the ISO week number for a given date
    public static int getWeekNumber(LocalDate date) {
        return date.get(java.time.temporal.WeekFields.ISO.weekOfYear());
    }

    // Checks whether a given date falls on a weekend (Saturday or Sunday)
    public static boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek() == java.time.DayOfWeek.SATURDAY
                || date.getDayOfWeek() == java.time.DayOfWeek.SUNDAY;
    }
}
