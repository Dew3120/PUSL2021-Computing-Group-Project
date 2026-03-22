package com.group100.wms.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Writes every data change to the AUDIT_LOGS table.
 * Call AuditLogger.log() from any service after a write operation.
 *
 * OOP Concepts Used:
 * - Encapsulation: Logging functionality is contained within this class.
 * - Abstraction: Hides the complexity of database logging from other classes.
 * - No Inheritance or Polymorphism is used in this class.
 */
public final class AuditLogger {

    // Private constructor to prevent instantiation of this utility class
    private AuditLogger() {}

    /**
     * @param userId     ID of the user performing the action
     * @param action     e.g. "CREATE", "UPDATE", "DELETE", "LOGIN"
     * @param tableName  e.g. "ITEMS", "USERS", "BATCHES"
     * @param recordId   Primary key of the affected record (as String)
     * @param details    Human-readable description of the change
     */

    // Logs user actions into the AUDIT_LOGS table in the database
    public static void log(int userId,
                           String action,
                           String tableName,
                           String recordId,
                           String details) {

        // Stores the SQL query used to insert a new audit log record
        String sql = "INSERT INTO AUDIT_LOGS "
                + "(user_id, action, table_name, record_id, details, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, action);
            ps.setString(3, tableName);
            ps.setString(4, recordId);
            ps.setString(5, details);
            ps.setObject(6, LocalDateTime.now());

            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[AUDIT] Failed to write audit log: " + e.getMessage());
        }
    }
}
