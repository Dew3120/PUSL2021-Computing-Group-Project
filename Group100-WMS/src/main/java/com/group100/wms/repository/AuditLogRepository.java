package com.group100.wms.repository;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.AuditLog;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// OOP Concepts used in this class:
// 1. Encapsulation: Database access logic and mapping details are hidden within the class, using a private method (mapRow) for internal processing.
// 2. Abstraction: The class provides a simplified interface for retrieving audit logs without the caller needing to know the underlying SQL structure.
public class AuditLogRepository {

    // Retrieves all audit log entries from the database, sorted by the most recent first
    public List<AuditLog> findAll() throws DatabaseException {
        // Stores the list of all AuditLog objects retrieved from the database
        List<AuditLog> list = new ArrayList<>();
        // Stores the SQL query to select all records from the audit_logs table
        String sql = "SELECT * FROM audit_logs ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch audit logs", e);
        }
        return list;
    }

    // Retrieves all audit log entries associated with a specific user ID
    public List<AuditLog> findByUserId(int userId) throws DatabaseException {
        // Stores the list of AuditLog objects filtered by a specific user
        List<AuditLog> list = new ArrayList<>();
        // Stores the parameterized SQL query to filter logs by user_id
        String sql = "SELECT * FROM audit_logs WHERE user_id = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch audit logs by user", e);
        }
        return list;
    }

    // Retrieves all audit log entries related to actions performed on a specific database table
    public List<AuditLog> findByTableName(String tableName) throws DatabaseException {
        // Stores the list of AuditLog objects filtered by the table name
        List<AuditLog> list = new ArrayList<>();
        // Stores the parameterized SQL query to filter logs by the target table_name
        String sql = "SELECT * FROM audit_logs WHERE table_name = ? ORDER BY created_at DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, tableName);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch audit logs by table", e);
        }
        return list;
    }

    // Maps a single row from the ResultSet into an AuditLog model object
    private AuditLog mapRow(ResultSet rs) throws SQLException {
        return new AuditLog(
                rs.getInt("log_id"),
                rs.getInt("user_id"),
                rs.getString("action"),
                rs.getString("table_name"),
                String.valueOf(rs.getInt("record_id")),
                rs.getString("details"),
                rs.getObject("created_at", LocalDateTime.class)
        );
    }
}
