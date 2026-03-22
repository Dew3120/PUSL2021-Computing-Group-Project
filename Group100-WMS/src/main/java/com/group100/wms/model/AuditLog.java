package com.group100.wms.model;

import java.time.LocalDateTime;

/**
 * Represents an audit trail entry that logs significant actions performed
 * in the system (e.g., create, update, delete operations on records).
 * Used for tracking changes, security monitoring, and compliance.
 * 
 * OOP Concepts used in this class:
 * - Encapsulation: All fields are private; access is controlled through public getter and setter methods
 * - Abstraction: Provides a simple, standardized interface for audit logging without exposing 
 *   internal storage or formatting details
 */
public class AuditLog {
    
    // Unique identifier for this audit log entry in the database
    private int id;
    
    // Foreign key referencing the User who performed the action
    private int userId;
    
    // Type of action performed (e.g., "CREATE", "UPDATE", "DELETE", "APPROVE", "LOGIN")
    private String action;
    
    // Name of the database table or entity that was affected
    private String tableName;
    
    // Identifier of the specific record that was modified (usually the primary key, stored as String for flexibility)
    private String recordId;
    
    // Additional context or changed field information (e.g., old vs new values, JSON diff, description)
    private String details;
    
    // Timestamp when this action occurred / when the log entry was created
    private LocalDateTime createdAt;

    // Default constructor - useful for creating empty audit log objects or for frameworks
    public AuditLog() {}

    /**
     * Parameterized constructor to create a fully initialized AuditLog entry
     * @param id unique log entry identifier
     * @param userId ID of the user who performed the action
     * @param action type of action performed
     * @param tableName name of the affected table/entity
     * @param recordId identifier of the modified record
     * @param details additional information about the change
     * @param createdAt timestamp of when the action occurred
     */
    public AuditLog(int id, int userId, String action, String tableName,
                    String recordId, String details, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.action = action;
        this.tableName = tableName;
        this.recordId = recordId;
        this.details = details;
        this.createdAt = createdAt;
    }

    /**
     * Gets the unique identifier of this audit log entry
     * @return log entry ID
     */
    public int getId() { return id; }

    /**
     * Gets the ID of the user who performed the logged action
     * @return user ID
     */
    public int getUserId() { return userId; }

    /**
     * Gets the type of action that was performed
     * @return action string (e.g., "UPDATE", "DELETE")
     */
    public String getAction() { return action; }

    /**
     * Gets the name of the table or entity that was affected
     * @return table/entity name
     */
    public String getTableName() { return tableName; }

    /**
     * Gets the identifier of the specific record that was modified
     * @return record ID (as String)
     */
    public String getRecordId() { return recordId; }

    /**
     * Gets additional details about the action (e.g., changed fields, old/new values)
     * @return details string
     */
    public String getDetails() { return details; }

    /**
     * Gets the timestamp when this action was performed
     * @return creation timestamp
     */
    public LocalDateTime getCreatedAt() { return createdAt; }

    /**
     * Sets the unique identifier (typically used only by the persistence layer)
     * @param id the ID to set
     */
    public void setId(int id) { this.id = id; }

    /**
     * Sets or updates the user who performed the action
     * @param userId user ID to set
     */
    public void setUserId(int userId) { this.userId = userId; }

    /**
     * Sets or updates the type of action
     * @param action action type to set
     */
    public void setAction(String action) { this.action = action; }

    /**
     * Sets or updates the affected table/entity name
     * @param tableName table name to set
     */
    public void setTableName(String tableName) { this.tableName = tableName; }

    /**
     * Sets or updates the record identifier
     * @param recordId record ID to set
     */
    public void setRecordId(String recordId) { this.recordId = recordId; }

    /**
     * Sets or updates the additional details of the action
     * @param details details to set
     */
    public void setDetails(String details) { this.details = details; }

    /**
     * Sets or updates the timestamp of when the action occurred
     * @param createdAt timestamp to set
     */
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    /**
     * Returns a string representation of the AuditLog object (useful for logging/debugging)
     * @return string containing id, userId, action, tableName, and createdAt
     */
    @Override
    public String toString() {
        return "AuditLog{id=" + id + ", userId=" + userId
                + ", action='" + action + "', tableName='" + tableName
                + "', createdAt=" + createdAt + "}";
    }
}
