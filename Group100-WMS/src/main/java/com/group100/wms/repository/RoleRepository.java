package com.group100.wms.repository;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// OOP Concepts Used:
// Encapsulation - All role database access logic is encapsulated within this repository class.
// Abstraction - Uses DatabaseConnection to abstract database connection details.
// Inheritance - Uses standard Java classes (Connection, PreparedStatement, ResultSet) which provide polymorphic behavior.
// Polymorphism - Optional class used to handle presence or absence of Role objects gracefully.

public class RoleRepository {

    // Finds a role by its unique ID. Returns an Optional<Role> which may be empty if no role is found
    public Optional<Role> findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM ROLES WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find role by id", e);
        }
        return Optional.empty();
    }

    // Finds a role by its role name. Returns an Optional<Role> which may be empty if no role is found
    public Optional<Role> findByName(String roleName) throws DatabaseException {
        String sql = "SELECT * FROM ROLES WHERE role_name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roleName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find role by name", e);
        }
        return Optional.empty();
    }

    // Retrieves all roles from the database and returns them as a List<Role>
    public List<Role> findAll() throws DatabaseException {
        List<Role> list = new ArrayList<>();
        String sql = "SELECT * FROM ROLES ORDER BY id";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all roles", e);
        }
        return list;
    }

    // Maps a ResultSet row to a Role object
    private Role mapRow(ResultSet rs) throws SQLException {
        return new Role(
                rs.getInt("id"),
                rs.getString("role_name"),
                rs.getString("description")
        );
    }
}