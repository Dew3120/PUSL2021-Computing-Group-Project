package com.group100.wms.repository;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.GoodsIssueNote;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// OOP Concepts used in this class:
// 1. Encapsulation: Use of private helper methods (mapRow) and wrapping data access logic within a specific repository class.
// 2. Abstraction: Hiding the complexity of SQL queries and database connection management behind simple method calls.
public class GinRepository {

    // Retrieves all Goods Issue Note records from the database ordered by date
    public List<GoodsIssueNote> findAll() throws DatabaseException {
        // Stores the list of GoodsIssueNote objects retrieved from the database
        List<GoodsIssueNote> list = new ArrayList<>();
        // Stores the SQL query string for selecting all records
        String sql = "SELECT gin_id, warehouse_id, destination, destination_type, " +
                "issued_by, issued_date, status " +
                "FROM goods_issue_notes ORDER BY issued_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to fetch GINs", e);
        }
        return list;
    }

    // Searches for a specific Goods Issue Note by its unique ID
    public Optional<GoodsIssueNote> findById(int ginId) throws DatabaseException {
        // Stores the SQL query string for selecting a record by ID
        String sql = "SELECT gin_id, warehouse_id, destination, destination_type, " +
                "issued_by, issued_date, status " +
                "FROM goods_issue_notes WHERE gin_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ginId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to find GIN", e);
        }
        return Optional.empty();
    }

    // Inserts a new Goods Issue Note record into the database and updates the object with the generated ID
    public void save(GoodsIssueNote gin) throws DatabaseException {
        // Stores the SQL query string for inserting a new GIN record
        String sql = "INSERT INTO goods_issue_notes " +
                "(warehouse_id, destination, destination_type, issued_by, issued_date, status) " +
                "VALUES (?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, gin.getWarehouseId());
            ps.setString(2, gin.getDestination());
            ps.setString(3, gin.getDestType());
            ps.setInt(4, gin.getIssuedBy());
            ps.setDate(5, Date.valueOf(gin.getIssuedDate()));
            ps.setString(6, gin.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) gin.setId(keys.getInt(1));
            }
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to save GIN", e);
        }
    }

    // Updates the status of an existing Goods Issue Note in the database
    public void updateStatus(int ginId, String status) throws DatabaseException {
        // Stores the SQL query string for updating the status column
        String sql = "UPDATE goods_issue_notes SET status = ? WHERE gin_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, ginId);
            ps.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to update GIN status", e);
        }
    }

    // Converts a single row from the SQL ResultSet into a GoodsIssueNote object
    private GoodsIssueNote mapRow(ResultSet rs) throws java.sql.SQLException {
        return new GoodsIssueNote(
                rs.getInt("gin_id"),
                rs.getInt("warehouse_id"),
                rs.getString("destination"),
                rs.getString("destination_type"),
                rs.getInt("issued_by"),
                rs.getDate("issued_date").toLocalDate(),
                rs.getString("status"));
    }
}
