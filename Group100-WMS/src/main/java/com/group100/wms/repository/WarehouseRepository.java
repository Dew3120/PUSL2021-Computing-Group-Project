package com.group100.wms.repository;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.Warehouse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository class for handling Warehouse-related database operations.
 *
 * OOP Concepts Used:
 * - Encapsulation: Database access logic is contained within this class.
 * - Abstraction: Provides simplified methods to interact with warehouse data without exposing SQL queries.
 * - Polymorphism: Demonstrated via exception handling and method behavior.
 * - No direct inheritance used in this class.
 */
public class WarehouseRepository {

    // Finds a warehouse by its ID
    public Optional<Warehouse> findById(int id) throws DatabaseException {
        String sql = "SELECT warehouse_id, name, location FROM warehouses WHERE warehouse_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find warehouse by id", e);
        }
        return Optional.empty();
    }

    // Retrieves all warehouses from the database ordered by name
    public List<Warehouse> findAll() throws DatabaseException {
        List<Warehouse> list = new ArrayList<>();
        String sql = "SELECT warehouse_id, name, location FROM warehouses ORDER BY name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all warehouses", e);
        }
        return list;
    }

    // Retrieves all active warehouses from the database
    public List<Warehouse> findAllActive() throws DatabaseException {
        return findAll();
    }

    // Saves a new warehouse record into the database and retrieves the generated ID
    public void save(Warehouse warehouse) throws DatabaseException {
        String sql = "INSERT INTO warehouses (name, location) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, warehouse.getName());
            ps.setString(2, warehouse.getLocation());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) warehouse.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save warehouse", e);
        }
    }
    // Updates an existing warehouse record in the database
    public void update(Warehouse warehouse) throws DatabaseException {
        String sql = "UPDATE warehouses SET name=?, location=? WHERE warehouse_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, warehouse.getName());
            ps.setString(2, warehouse.getLocation());
            ps.setInt(3, warehouse.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update warehouse", e);
        }
    }

    // Maps a database result set row to a Warehouse object
    private Warehouse mapRow(ResultSet rs) throws SQLException {
        return new Warehouse(
                rs.getInt("warehouse_id"),
                rs.getString("name"),
                rs.getString("location"),
                null,
                true
        );
    }
}
