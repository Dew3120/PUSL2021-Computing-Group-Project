package com.group100.wms.repository;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// OOP Concepts Used:
// Encapsulation - Database access logic is encapsulated within this repository class.
// Abstraction - Uses DatabaseConnection class to abstract database connection details.
// Inheritance - Uses standard Java classes (Connection, PreparedStatement, ResultSet) with polymorphic behavior.
// Polymorphism - Optional class used to handle presence or absence of user objects gracefully.

public class UserRepository {

    // Finds a user by their username. Returns an Optional<User> which may be empty if user not found
    public Optional<User> findByUsername(String username) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find user by username", e);
        }
        return Optional.empty();
    }

    // Finds a user by their unique ID. Returns an Optional<User> which may be empty if user not found
    public Optional<User> findById(int id) throws DatabaseException {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to find user by id", e);
        }
        return Optional.empty();
    }

    // Retrieves all users from the database and returns them as a List<User>
    public List<User> findAll() throws DatabaseException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY username";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all users", e);
        }
        return list;
    }

    // Saves a new user to the database and sets its generated ID on the user object
    public void save(User user) throws DatabaseException {
        String sql = "INSERT INTO users (username, password_hash, role_id, is_active, created_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setInt(3, user.getRoleId());
            ps.setBoolean(4, user.isActive());
            ps.setObject(5, user.getCreatedAt() != null ? user.getCreatedAt() : LocalDateTime.now());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) user.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save user", e);
        }
    }

    // Updates an existing user's information in the database
    public void update(User user) throws DatabaseException {
        String sql = "UPDATE users SET username=?, password_hash=?, role_id=?, is_active=? WHERE user_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setInt(3, user.getRoleId());
            ps.setBoolean(4, user.isActive());
            ps.setInt(5, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to update user", e);
        }
    }

    // Deletes a user from the database by their ID
    public void deleteById(int id) throws DatabaseException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete user", e);
        }
    }

    // Maps a ResultSet row to a User object
    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getInt("role_id"),
                0,
                rs.getBoolean("is_active"),
                rs.getObject("created_at", LocalDateTime.class)
        );
    }
}