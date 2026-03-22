package com.group100.wms.repository;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.Supplier;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// OOP Concepts Used:
// Encapsulation - All supplier database operations are encapsulated within this repository class.
// Abstraction - Uses DatabaseConnection to abstract database connectivity.
// Inheritance - Uses standard Java SQL classes (Connection, PreparedStatement, ResultSet) which provide polymorphic behavior.
// Polymorphism - Optional class used to handle presence or absence of Supplier objects gracefully.

public class SupplierRepository {

    // Finds a supplier by its unique ID. Returns an Optional<Supplier> which may be empty if no supplier is found
    public Optional<Supplier> findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM SUPPLIERS WHERE id = ?";
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

    // Retrieves all suppliers from the database and returns them as a List<Supplier>
    public List<Supplier> findAll() throws DatabaseException {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM SUPPLIERS ORDER BY name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all suppliers", e);
        }
        return list;
    }

    // Retrieves only active suppliers (is_active = true) and returns them as a List<Supplier>
    public List<Supplier> findAllActive() throws DatabaseException {
        List<Supplier> list = new ArrayList<>();
        String sql = "SELECT * FROM SUPPLIERS WHERE is_active = true ORDER BY name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch active suppliers", e);
        }
        return list;
    }

    // Saves a new supplier to the database and sets its generated ID on the supplier object
    public void save(Supplier supplier) throws DatabaseException {
        String sql = "INSERT INTO SUPPLIERS (name, contact_person, phone, email, address, is_active) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, supplier.getName());
            ps.setString(2, supplier.getContactPerson());
            ps.setString(3, supplier.getPhone());
            ps.setString(4, supplier.getEmail());
            ps.setString(5, supplier.getAddress());
            ps.setBoolean(6, supplier.isActive());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) supplier.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save supplier", e);
        }
    }

    // Updates an existing supplier's information in the database
    public void update(Supplier supplier) throws DatabaseException {
        String sql = "UPDATE SUPPLIERS SET name=?, contact_person=?, phone=?, "
                + "email=?, address=?, is_active=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, supplier.getName());
            ps.setString(2, supplier.getContactPerson());
            ps.setString(3, supplier.getPhone());
            ps.setString(4, supplier.getEmail());
            ps.setString(5, supplier.getAddress());
            ps.setBoolean(6, supplier.isActive());
            ps.setInt(7, supplier.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update supplier", e);
        }
    }

    // Maps a ResultSet row to a Supplier object
    private Supplier mapRow(ResultSet rs) throws SQLException {
        return new Supplier(
                rs.getInt("id"),
                rs.getString("name"),
                rs.getString("contact_person"),
                rs.getString("phone"),
                rs.getString("email"),
                rs.getString("address"),
                rs.getBoolean("is_active")
        );
    }
}