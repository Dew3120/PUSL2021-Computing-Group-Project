package com.group100.wms.service;

import com.group100.wms.core.AuditLogger;
import com.group100.wms.core.SessionManager;
import com.group100.wms.exception.AuthenticationException;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.User;
import com.group100.wms.repository.UserRepository;
import com.group100.wms.util.PasswordHasher;

import java.util.Optional;

public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String username, String password)
            throws AuthenticationException, DatabaseException {
        if (username == null || username.isBlank())
            throw new AuthenticationException("Username cannot be empty.");
        if (password == null || password.isBlank())
            throw new AuthenticationException("Password cannot be empty.");

        Optional<User> opt = userRepository.findByUsername(username.trim());
        if (opt.isEmpty()) {
            AuditLogger.log(0, "FAILED_LOGIN", "USERS", "0",
                    "Failed login attempt — unknown username: " + username);
            throw new AuthenticationException("Invalid username or password.");
        }

        User user = opt.get();
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

    public void logout() {
        User user = SessionManager.getCurrentUser();
        if (user != null) {
            AuditLogger.log(user.getId(), "LOGOUT", "USERS",
                    String.valueOf(user.getId()), "User logged out.");
        }
        SessionManager.logout();
    }
}