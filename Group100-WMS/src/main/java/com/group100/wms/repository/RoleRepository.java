package com.group100.wms.repository;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoleRepository {

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

    private Role mapRow(ResultSet rs) throws SQLException {
        return new Role(
                rs.getInt("id"),
                rs.getString("role_name"),
                rs.getString("description")
        );
    }
}