package com.group100.wms.model;

/**
 * Represents a system role — Admin, Manager, Supervisor, Accountant, etc.
 * 
 * OOP Concepts used in this class:
 * - Encapsulation: All fields are declared private; access is controlled through public getter and setter methods
 * - Abstraction: The class exposes a simple interface for working with role data without revealing internal representation
 */
public class Role {
    
    // Unique identifier for the role in the database
    private int id;
    
    // Unique name of the role (e.g., "ADMIN", "MANAGER", "SUPERVISOR")
    private String roleName;
    
    // Human-readable explanation of what this role can do or its purpose
    private String description;

    // Default (no-argument) constructor - useful for frameworks or creating empty role objects
    public Role() {}

    /**
     * Parameterized constructor to create a fully initialized Role object
     * @param id unique identifier of the role
     * @param roleName name of the role
     * @param description detailed explanation of the role's responsibilities/permissions
     */
    public Role(int id, String roleName, String description) {
        this.id = id;
        this.roleName = roleName;
        this.description = description;
    }

    /**
     * Gets the unique identifier of this role
     * @return the role's ID
     */
    public int getId() { return id; }

    /**
     * Gets the name of this role
     * @return the role name
     */
    public String getRoleName() { return roleName; }

    /**
     * Gets the description explaining the role's purpose and permissions
     * @return the role description
     */
    public String getDescription() { return description; }

    /**
     * Sets the unique identifier for this role (typically used by persistence layer)
     * @param id the ID to assign
     */
    public void setId(int id) { this.id = id; }

    /**
     * Sets or updates the name of this role
     * @param roleName the role name to set
     */
    public void setRoleName(String roleName) { this.roleName = roleName; }

    /**
     * Sets or updates the description of this role
     * @param description the description to set
     */
    public void setDescription(String description) { this.description = description; }

    /**
     * Returns a string representation of the Role object (useful for logging and debugging)
     * @return string containing id and roleName
     */
    @Override
    public String toString() {
        return "Role{id=" + id + ", roleName='" + roleName + "'}";
    }
}
