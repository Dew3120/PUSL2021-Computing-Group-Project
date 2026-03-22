package com.group100.wms.service;

import com.group100.wms.core.AuditLogger;
import com.group100.wms.core.SessionManager;
import com.group100.wms.exception.AuthenticationException;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.User;
import com.group100.wms.repository.UserRepository;
import com.group100.wms.util.PasswordHasher;

import java.util.Optional;

// OOP Concepts used in this class:
// 1. Encapsulation: The class manages authentication logic and user sessions while keeping the UserRepository dependency private.
// 2. Abstraction: It provides high-level login/logout functionality, hiding the complexities of password hashing, session management, and audit logging from the UI layer.
public class AuthService {

    // Stores the data access object for performing user-related database operations
    private final UserRepository userRepository;

    // Constructor to initialize the service with a specific user repository
    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Validates user credentials, checks account status, manages sessions, and logs login attempts
    public User login(String username, String password)
            throws AuthenticationException, DatabaseException {
        if (username == null || username.isBlank())
            throw new AuthenticationException("Username cannot be empty.");
        if (password == null || password.isBlank())
            throw new AuthenticationException("Password cannot be empty.");

        // Stores the optional user result retrieved from the database by username
        Optional<User> opt = userRepository.findByUsername(username.trim());
        if (opt.isEmpty()) {
            AuditLogger.log(0, "FAILED_LOGIN", "USERS", "0",
                    "Failed login attempt — unknown username: " + username);
            throw new AuthenticationException("Invalid username or password.");
        }

        // Stores the actual User object if found in the database
        User user = opt.get();
        // Stores the boolean result of comparing the provided password with the stored hash
        boolean match = PasswordHasher.verify(password, user.getPasswordHash());

        if (!user.isActive()) {
            AuditLogger.log(user.getId(), "FAILED_LOGIN", "USERS",
                    String.valueOf(user.getId()),
                    "Failed login — account disabled: " + username);
            throw new AuthenticationException("Account is disabled. Contact administrator.");
        }

        if (!match) {
            AuditLogger.log(user.getId(), "FAILED_LOGIN", "USERS",
                    String.valueOf(user.getId()),
                    "Failed login — wrong password: " + username);
            throw new AuthenticationException("Invalid username or password.");
        }

        SessionManager.login(user);
        AuditLogger.log(user.getId(), "LOGIN", "USERS",
                String.valueOf(user.getId()), "User logged in successfully.");
        return user;
    }

    // Clears the current user session and records the logout event in the audit log
    public void logout() {
        // Stores the current authenticated user retrieved from the session manager
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            AuditLogger.log(user.getId(), "LOGOUT", "USERS",
                    String.valueOf(user.getId()), "User logged out.");
        }
        SessionManager.logout();
    }
}
