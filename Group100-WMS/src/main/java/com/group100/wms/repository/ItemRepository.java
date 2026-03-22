package com.group100.wms.repository;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.Item;
import java.sql.*;
import java.util.*;

/**
 * Repository class for handling Item-related database operations.
 *
 * OOP Concepts Used:
 * - Encapsulation: Database access logic is contained within this class.
 * - Abstraction: Provides simple methods to interact with the database without exposing SQL complexity.
 * - Polymorphism: Uses method overloading and exception handling across different methods.
 * - No direct inheritance used in this class.
 */
public class ItemRepository {

    // Retrieves all items from the database ordered by category and name
    public List<Item> findAll() throws DatabaseException {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT item_id, sku, name, description, category, colour, unit, warehouse_id " +
                "FROM items ORDER BY category, name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch items", e);
        }
        return list;
    }

    // Finds a specific item by its ID
    public Optional<Item> findById(int id) throws DatabaseException {
        String sql = "SELECT item_id, sku, name, description, category, colour, unit, warehouse_id " +
                "FROM items WHERE item_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find item", e);
        }
        return Optional.empty();
    }

    // Retrieves all items that belong to a specific warehouse
    public List<Item> findByWarehouseId(int warehouseId) throws DatabaseException {
        List<Item> list = new ArrayList<>();
        String sql = "SELECT item_id, sku, name, description, category, colour, unit, warehouse_id " +
                "FROM items WHERE warehouse_id = ? ORDER BY category, name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, warehouseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch items by warehouse", e);
        }
        return list;
    }

    // Retrieves the total available stock level for a given item
    public int getStockLevel(int itemId) throws DatabaseException {
        String sql = "SELECT COALESCE(SUM(available_qty), 0) FROM batches WHERE item_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to get stock level", e);
        }
        return 0;
    }
    // Saves a new item into the database and retrieves the generated ID
    public void save(Item item) throws DatabaseException {
        String sql = "INSERT INTO items (sku, name, description, category, colour, unit, warehouse_id) " +
                "VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, item.getSku());
            ps.setString(2, item.getName());
            ps.setString(3, item.getDescription());
            ps.setString(4, item.getCategory());
            ps.setString(5, item.getColour());
            ps.setString(6, item.getUnit());
            ps.setInt(7, item.getWarehouseId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) item.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save item", e);
        }
    }

    // Updates an existing item in the database
    public void update(Item item) throws DatabaseException {
        String sql = "UPDATE items SET sku=?, name=?, description=?, category=?, " +
                "colour=?, unit=?, warehouse_id=? WHERE item_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getSku());
            ps.setString(2, item.getName());
            ps.setString(3, item.getDescription());
            ps.setString(4, item.getCategory());
            ps.setString(5, item.getColour());
            ps.setString(6, item.getUnit());
            ps.setInt(7, item.getWarehouseId());
            ps.setInt(8, item.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update item", e);
        }
    }

    // Maps a database result set row to an Item object
    private Item mapRow(ResultSet rs) throws SQLException {
        return new Item(
                rs.getInt("item_id"), rs.getString("sku"),
                rs.getString("name"), rs.getString("description"),
                rs.getString("category"), rs.getString("colour"),
                rs.getString("unit"), rs.getInt("warehouse_id"));
    }
}
