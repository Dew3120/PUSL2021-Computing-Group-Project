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

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAllUsers() throws DatabaseException, UnauthorizedAccessException {
        requireAdminRole();
        return userRepository.findAll();
    }

    public Optional<User> getUserById(int id) throws DatabaseException, UnauthorizedAccessException {
        requireAdminRole();
        return userRepository.findById(id);
    }

    public void createUser(String username, String plainPassword, int roleId, int employeeId)
            throws DatabaseException, UnauthorizedAccessException {
        requireAdminRole();
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

    public void updateUser(User user) throws DatabaseException, UnauthorizedAccessException {
        requireAdminRole();
        userRepository.update(user);
        AuditLogger.log(SessionManager.getCurrentUser().getId(),
                "UPDATE", "USERS", String.valueOf(user.getId()),
                "Updated user: " + user.getUsername());
    }

    public void changePassword(int userId, String newPlainPassword)
            throws DatabaseException, UnauthorizedAccessException {
        requireAdminRole();
        Optional<User> opt = userRepository.findById(userId);
        if (opt.isEmpty()) return;
        User user = opt.get();
        user.setPasswordHash(PasswordHasher.hash(newPlainPassword));
        userRepository.update(user);
        AuditLogger.log(SessionManager.getCurrentUser().getId(),
                "UPDATE", "USERS", String.valueOf(userId), "Password changed.");
    }

    public void deactivateUser(int userId) throws DatabaseException, UnauthorizedAccessException {
        requireAdminRole();
        Optional<User> opt = userRepository.findById(userId);
        if (opt.isEmpty()) return;
        User user = opt.get();
        user.setActive(false);
        userRepository.update(user);
        AuditLogger.log(SessionManager.getCurrentUser().getId(),
                "DEACTIVATE", "USERS", String.valueOf(userId),
                "Deactivated user: " + user.getUsername());
    }

    private void requireAdminRole() throws UnauthorizedAccessException {
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