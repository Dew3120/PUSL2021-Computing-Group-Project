package com.group100.wms.repository;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.Warehouse;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WarehouseRepository {

    public Optional<Warehouse> findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM WAREHOUSES WHERE id = ?";
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

    public List<Warehouse> findAll() throws DatabaseException {
        List<Warehouse> list = new ArrayList<>();
        String sql = "SELECT * FROM WAREHOUSES ORDER BY name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all warehouses", e);
        }
        return list;
    }

    public List<Warehouse> findAllActive() throws DatabaseException {
        List<Warehouse> list = new ArrayList<>();
        String sql = "SELECT * FROM WAREHOUSES WHERE is_active = true ORDER BY name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch active warehouses", e);
        }
        return list;
    }

    public void save(Warehouse warehouse) throws DatabaseException {
        String sql = "INSERT INTO WAREHOUSES (name, location, manager_name, is_active) "
                + "VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, warehouse.getName());
            ps.setString(2, warehouse.getLocation());
            ps.setString(3, warehouse.getManagerName());
            ps.setBoolean(4, warehouse.isActive());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) warehouse.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save warehouse", e);
        }
    }

    public void update(Warehouse warehouse) throws DatabaseException {
        String sql = "UPDATE WAREHOUSES SET name=?, location=?, manager_name=?, is_active=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, warehouse.getName());
            ps.setString(2, warehouse.getLocation());
            ps.setString(3, warehouse.getManagerName());
            ps.setBoolean(4, warehouse.isActive());
            ps.setInt(5, warehouse.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update warehouse", e);
        }
    }

    private Warehouse mapRow(ResultSet rs) throws SQLException {
        return new Warehouse(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("location"),
                rs.getString("manager_name"),
                rs.getBoolean("is_active")
        );
    }
}