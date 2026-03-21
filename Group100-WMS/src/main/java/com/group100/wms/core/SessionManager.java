package com.group100.wms.core;

import com.group100.wms.model.User;

import java.time.LocalDateTime;

/**
 * Holds the currently logged-in user and session state.
 * Singleton — one session per application instance.
 */
public final class SessionManager {

    private static User    currentUser;
    private static LocalDateTime lastActivityTime;

    private SessionManager() {}

    public static void login(User user) {
        currentUser      = user;
        lastActivityTime = LocalDateTime.now();
        System.out.println("[SESSION] User logged in: " + user.getUsername());
    }

    public static void logout() {
        System.out.println("[SESSION] User logged out: "
                + (currentUser != null ? currentUser.getUsername() : "none"));
        currentUser      = null;
        lastActivityTime = null;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    public static void updateActivity() {
        lastActivityTime = LocalDateTime.now();
    }

    public static boolean isSessionExpired() {
        if (lastActivityTime == null) return true;
        return lastActivityTime
                .plusMinutes(AppConfig.SESSION_TIMEOUT_MINUTES)
                .isBefore(LocalDateTime.now());
    }
}