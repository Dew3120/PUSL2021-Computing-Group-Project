package com.group100.wms.model;

import java.time.LocalDateTime;

/**
 * Represents a system user with login credentials and role assignment.
 * 
 * OOP Concepts used in this class:
 * - Encapsulation: All fields are private with public getters and setters for controlled access
 * - Abstraction: Provides a clean interface (getters/setters) to user data without exposing implementation details
 */
public class User {
    
    // Unique identifier for the user in the database
    private int id;
    
    // Unique login name of the user
    private String username;
    
    // Hashed version of the user's password (never stores plain text)
    private String passwordHash;
    
    // Foreign key referencing the role this user has (links to roles table)
    private int roleId;
    
    // Foreign key linking this user account to an employee record
    private int employeeId;
    
    // Indicates whether the user account is currently active/enabled
    private boolean isActive;
    
    // Timestamp when this user account was created
    private LocalDateTime createdAt;

    // Default constructor - required for frameworks like Hibernate/JPA or when creating empty objects
    public User() {}

    /**
     * Parameterized constructor to create a fully initialized User object
     * @param id unique identifier
     * @param username login name
     * @param passwordHash securely hashed password
     * @param roleId role identifier
     * @param employeeId linked employee identifier
     * @param isActive account active status
     * @param createdAt creation timestamp
     */
    public User(int id, String username, String passwordHash,
                int roleId, int employeeId, boolean isActive,
                LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.roleId = roleId;
        this.employeeId = employeeId;
        this.isActive = isActive;
        this.createdAt = createdAt;
    }

    /**
     * Gets the unique identifier of the user
     * @return the user's ID
     */
    public int getId() { return id; }

    /**
     * Gets the username used for login
     * @return the username
     */
    public String getUsername() { return username; }

    /**
     * Gets the hashed password (never returns plain text)
     * @return the password hash
     */
    public String getPasswordHash() { return passwordHash; }

    /**
     * Gets the role identifier this user belongs to
     * @return role ID
     */
    public int getRoleId() { return roleId; }

    /**
     * Gets the linked employee record ID
     * @return employee ID
     */
    public int getEmployeeId() { return employeeId; }

    /**
     * Checks if the user account is currently active
     * @return true if account is active, false otherwise
     */
    public boolean isActive() { return isActive; }

    /**
     * Gets the date and time when this user was created
     * @return creation timestamp
     */
    public LocalDateTime getCreatedAt() { return createdAt; }

    /**
     * Sets the unique identifier (typically used only by persistence layer)
     * @param id the ID to set
     */
    public void setId(int id) { this.id = id; }

    /**
     * Sets the username for this user
     * @param username the username to set
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * Sets the hashed password for this user
     * @param passwordHash the hashed password to set
     */
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    /**
     * Assigns or changes the role of this user
     * @param roleId the role ID to set
     */
    public void setRoleId(int roleId) { this.roleId = roleId; }

    /**
     * Links or updates the employee record associated with this user
     * @param employeeId the employee ID to set
     */
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }

    /**
     * Activates or deactivates the user account
     * @param active true to activate, false to deactivate
     */
    public void setActive(boolean active) { this.isActive = active; }

    /**
     * Sets the creation timestamp (typically set once during creation)
     * @param createdAt the timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /**
     * Returns a string representation of the User object (useful for logging/debugging)
     * @return string containing id, username, and roleId
     */
    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', roleId=" + roleId + "}";
    }
}
