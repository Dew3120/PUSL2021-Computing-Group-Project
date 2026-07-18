package com.group100.wms.repository;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SupplierRepository {

    public Optional<Supplier> findById(int id) throws DatabaseException {
        String sql = "SELECT supplier_id, name, contact, address, email FROM suppliers WHERE supplier_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find supplier by id", e);
        }
        return Optional.empty();
    }

    public List<Supplier> findAll() throws DatabaseException {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT supplier_id, name, contact, address, email FROM suppliers ORDER BY name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all suppliers", e);
        }
        return list;
    }

    public List<Supplier> findAllActive() throws DatabaseException {
        return findAll();
    }

    public void save(Supplier supplier) throws DatabaseException {
        String sql = "INSERT INTO suppliers (name, contact, address, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, supplier.getName());
            ps.setString(2, preferredContact(supplier));
            ps.setString(3, supplier.getAddress());
            ps.setString(4, supplier.getEmail());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) supplier.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save supplier", e);
        }
    }

    public void update(Supplier supplier) throws DatabaseException {
        String sql = "UPDATE suppliers SET name=?, contact=?, address=?, email=? WHERE supplier_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, supplier.getName());
            ps.setString(2, preferredContact(supplier));
            ps.setString(3, supplier.getAddress());
            ps.setString(4, supplier.getEmail());
            ps.setInt(5, supplier.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update supplier", e);
        }
    }

    private String preferredContact(Supplier supplier) {
        if (supplier.getPhone() != null && !supplier.getPhone().isBlank()) {
            return supplier.getPhone();
        }
        return supplier.getContactPerson();
    }

    private Supplier mapRow(ResultSet rs) throws SQLException {
        String contact = rs.getString("contact");
        return new Supplier(
                rs.getInt("supplier_id"),
                rs.getString("name"),
                contact,
                contact,
                rs.getString("email"),
                rs.getString("address"),
                true
        );
    }
}
