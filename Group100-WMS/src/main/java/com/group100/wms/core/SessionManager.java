package com.group100.wms.core;

import com.group100.wms.model.User;

import java.time.LocalDateTime;

/**
 * Holds the currently logged-in user and session state.
 * Singleton — one session per application instance.
 *
 * OOP Concepts Used:
 * - Encapsulation: Session data (currentUser, lastActivityTime) is managed within this class.
 * - Abstraction: Provides simple methods to manage session without exposing internal details.
 * - Singleton Pattern (Design Concept): Ensures only one session exists in the application.
 * - No Inheritance or Polymorphism is used in this class.
 */
public final class SessionManager {

    // Stores the currently logged-in user
    private static User currentUser;

    // Stores the last activity timestamp of the current session
    private static LocalDateTime lastActivityTime;

    // Private constructor to prevent instantiation
    private SessionManager() {}

    // Logs in a user and initializes session activity time
    public static void login(User user) {
        currentUser      = user;
        lastActivityTime = LocalDateTime.now();
        System.out.println("[SESSION] User logged in: " + user.getUsername());
    }

    // Logs out the current user and clears session data
    public static void logout() {
        System.out.println("[SESSION] User logged out: "
                + (currentUser != null ? currentUser.getUsername() : "none"));
        currentUser      = null;
        lastActivityTime = null;
    }

    // Returns the currently logged-in user
    public static User getCurrentUser() {
        return currentUser;
    }

    // Checks whether a user is currently logged in
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    // Updates the last activity time to the current time
    public static void updateActivity() {
        lastActivityTime = LocalDateTime.now();
    }

    // Checks whether the session has expired based on inactivity timeout
    public static boolean isSessionExpired() {
        if (lastActivityTime == null) return true;
        return lastActivityTime
                .plusMinutes(AppConfig.SESSION_TIMEOUT_MINUTES)
                .isBefore(LocalDateTime.now());
    }
}
