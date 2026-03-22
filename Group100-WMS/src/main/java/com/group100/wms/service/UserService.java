package com.group100.wms.service;

import com.group100.wms.core.AuditLogger;
import com.group100.wms.core.SessionManager;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.exception.UnauthorizedAccessException;
import com.group100.wms.model.User;
import com.group100.wms.repository.UserRepository;
import com.group100.wms.util.PasswordHasher;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// OOP Concepts used in this class:
// 1. Encapsulation: Access to user data is controlled through methods, and the UserRepository dependency is kept private.
// 2. Abstraction: The service provides a clean interface for user management (create, update, deactivate) while hiding the internal authorization and auditing logic.
public class UserService {

    // Stores the repository instance used for database operations on users
    private final UserRepository userRepository;

    // Initializes the service with the required user repository dependency
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Fetches all users from the database, strictly requiring administrative privileges
    public List<User> getAllUsers() throws DatabaseException, UnauthorizedAccessException {
        requireAdminRole();
        return userRepository.findAll();
    }

    // Finds a specific user by their unique ID, strictly requiring administrative privileges
    public Optional<User> getUserById(int id) throws DatabaseException, UnauthorizedAccessException {
        requireAdminRole();
        return userRepository.findById(id);
    }

    // Creates a new user account with a hashed password and logs the action
    public void createUser(String username, String plainPassword, int roleId, int employeeId)
            throws DatabaseException, UnauthorizedAccessException {
        requireAdminRole();
        // Stores the new User object being constructed
        User user = new User();
        user.setUsername(username.trim());
        user.setPasswordHash(PasswordHasher.hash(plainPassword));
        user.setRoleId(roleId);
        user.setEmployeeId(employeeId);
        user.setActive(true);
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        AuditLogger.log(SessionManager.getCurrentUser().getId(),
                "CREATE", "USERS", String.valueOf(user.getId()),
                "Created user: " + username);
    }

    // Updates existing user details in the database and logs the changes
    public void updateUser(User user) throws DatabaseException, UnauthorizedAccessException {
        requireAdminRole();
        userRepository.update(user);
        AuditLogger.log(SessionManager.getCurrentUser().getId(),
                "UPDATE", "USERS", String.valueOf(user.getId()),
                "Updated user: " + user.getUsername());
    }

    // Hashes a new password for a specific user and updates their record
    public void changePassword(int userId, String newPlainPassword)
            throws DatabaseException, UnauthorizedAccessException {
        requireAdminRole();
        // Stores the optional result of the user search
        Optional<User> opt = userRepository.findById(userId);
        if (opt.isEmpty()) return;
        // Stores the retrieved User object to be modified
        User user = opt.get();
        user.setPasswordHash(PasswordHasher.hash(newPlainPassword));
        userRepository.update(user);
        AuditLogger.log(SessionManager.getCurrentUser().getId(),
                "UPDATE", "USERS", String.valueOf(userId), "Password changed.");
    }

    // Disables a user account without deleting the record from the database
    public void deactivateUser(int userId) throws DatabaseException, UnauthorizedAccessException {
        requireAdminRole();
        // Stores the optional result of the user search
        Optional<User> opt = userRepository.findById(userId);
        if (opt.isEmpty()) return;
        // Stores the retrieved User object to be deactivated
        User user = opt.get();
        user.setActive(false);
        userRepository.update(user);
        AuditLogger.log(SessionManager.getCurrentUser().getId(),
                "DEACTIVATE", "USERS", String.valueOf(userId),
                "Deactivated user: " + user.getUsername());
    }

    // Security helper method that validates if the current session belongs to an Administrator (Role ID 1)
    private void requireAdminRole() throws UnauthorizedAccessException {
        // Stores the User object of the person currently logged into the system
        User current = SessionManager.getCurrentUser();
        if (current == null || current.getRoleId() != 1) {
            if (current != null) {
                AuditLogger.log(current.getId(), "UNAUTHORIZED", "USERS", "N/A",
                        "Unauthorized access attempt on UserService.");
            }
            throw new UnauthorizedAccessException("Admin role required.");
        }
    }
}
