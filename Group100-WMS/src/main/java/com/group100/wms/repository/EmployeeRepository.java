package com.group100.wms.repository;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.Employee;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EmployeeRepository {

    public List<Employee> findAll() throws DatabaseException {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT employee_id, user_id, full_name, designation, daily_rate, is_active " +
                "FROM employees ORDER BY full_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to fetch employees", e);
        }
        return list;
    }

    public Optional<Employee> findById(int id) throws DatabaseException {
        String sql = "SELECT employee_id, user_id, full_name, designation, daily_rate, is_active " +
                "FROM employees WHERE employee_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to find employee", e);
        }
        return Optional.empty();
    }

    public List<Employee> findActive() throws DatabaseException {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT employee_id, user_id, full_name, designation, daily_rate, is_active " +
                "FROM employees WHERE is_active = 1 ORDER BY full_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to fetch active employees", e);
        }
        return list;
    }

    public void save(Employee emp) throws DatabaseException {
        String sql = "INSERT INTO employees (user_id, full_name, designation, daily_rate, is_active) " +
                "VALUES (?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, emp.getUserId());
            ps.setString(2, emp.getFullName());
            ps.setString(3, emp.getDesignation());
            ps.setDouble(4, emp.getDailyRate());
            ps.setBoolean(5, emp.isActive());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) emp.setId(keys.getInt(1));
            }
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to save employee", e);
        }
    }

    public void update(Employee emp) throws DatabaseException {
        String sql = "UPDATE employees SET full_name=?, designation=?, daily_rate=?, is_active=? " +
                "WHERE employee_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, emp.getFullName());
            ps.setString(2, emp.getDesignation());
            ps.setDouble(3, emp.getDailyRate());
            ps.setBoolean(4, emp.isActive());
            ps.setInt(5, emp.getId());
            ps.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to update employee", e);
        }
    }

    private Employee mapRow(ResultSet rs) throws java.sql.SQLException {
        return new Employee(
                rs.getInt("employee_id"),
                rs.getInt("user_id"),
                rs.getString("full_name"),
                rs.getString("designation"),
                rs.getDouble("daily_rate"),
                rs.getBoolean("is_active"));
    }
}