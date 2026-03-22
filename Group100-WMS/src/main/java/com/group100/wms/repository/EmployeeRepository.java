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

/**
 * Repository class responsible for all database operations related to Employee entities.
 * Provides CRUD (Create, Read, Update, Delete) functionality using JDBC.
 * 
 * OOP Concepts used in this class:
 * - Encapsulation: All database access logic is encapsulated within this class; 
 *   external code interacts only through public methods
 * - Abstraction: Hides JDBC implementation details and SQL queries behind clean, 
 *   domain-oriented method names
 * - Single Responsibility Principle (part of SOLID): This class is solely responsible 
 *   for persistence operations of Employee objects
 */
public class EmployeeRepository {

    /**
     * Retrieves all employees from the database, ordered by full name.
     * @return List of all Employee objects
     * @throws DatabaseException if a database access error occurs
     */
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

    /**
     * Finds a single employee by their unique employee_id.
     * @param id the employee ID to search for
     * @return Optional containing the Employee if found, or empty Optional otherwise
     * @throws DatabaseException if a database access error occurs
     */
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

    /**
     * Retrieves all currently active employees, ordered by full name.
     * @return List of active Employee objects (where is_active = true)
     * @throws DatabaseException if a database access error occurs
     */
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

    /**
     * Saves a new Employee to the database and sets the generated employee_id back into the object.
     * @param emp the Employee object to persist (must not have an ID set yet)
     * @throws DatabaseException if insertion fails or generated key cannot be retrieved
     */
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

    /**
     * Updates an existing employee's details in the database based on employee_id.
     * @param emp the Employee object with updated values (must have a valid ID)
     * @throws DatabaseException if the update operation fails
     */
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

    /**
     * Maps a single ResultSet row to an Employee domain object.
     * @param rs the ResultSet positioned at the current row
     * @return a new Employee instance populated from the current row
     * @throws java.sql.SQLException if column access fails
     */
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
