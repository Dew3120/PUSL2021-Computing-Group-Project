// =============================================================================
// PayrollRepository.java
// Part of: Centralized Apparel Warehouse Management System (WMS)
// Module: Repository Layer — Payroll Database Access
//
// OOP CONCEPTS USED IN THIS CLASS:
// - ENCAPSULATION: The SQL query strings are defined locally within each method
//   rather than exposed as class-level fields. The mapRow() helper is private,
//   hiding the ResultSet-to-Payroll mapping logic from all callers and ensuring
//   it can only be changed in one place.
// - ABSTRACTION: DatabaseConnection abstracts the underlying JDBC connection
//   setup and pooling. The Payroll model object abstracts the raw column values
//   into a clean Java object. Callers of this repository never deal with SQL,
//   ResultSets, or connection management — only with Payroll objects and lists.
// - POLYMORPHISM: mapRow() is called uniformly from both findByMonthYear() and
//   findByEmployee() regardless of which query produced the ResultSet, producing
//   a correctly populated Payroll object in both cases from a single method.
// - INHERITANCE: Payroll follows the JavaBean contract with getters and setters,
//   and DatabaseException wraps java.sql.SQLException — inheriting from the
//   application's custom exception hierarchy to provide consistent error handling
//   across all repository classes in the system.
// =============================================================================

package com.group100.wms.repository;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.Payroll;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PayrollRepository {

    // Retrieves all payroll records for a given month and year, joining with the
    // employees table to include each employee's full name in the result.
    // Results are ordered alphabetically by employee name.
    // Returns a list of mapped Payroll objects, or an empty list if none exist.
    // Throws DatabaseException if the SQL query fails.
    public List<Payroll> findByMonthYear(int month, int year) throws DatabaseException {
        List<Payroll> list = new ArrayList<>();
        String sql = "SELECT p.payroll_id, p.employee_id, p.month, p.year, p.base_salary, " +
                "p.overtime, p.deductions, p.epf_employer, p.etf, p.net_salary, " +
                "p.generated_by, p.generated_date, e.full_name " +
                "FROM payroll p JOIN employees e ON p.employee_id = e.employee_id " +
                "WHERE p.month = ? AND p.year = ? ORDER BY e.full_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to fetch payroll", e);
        }
        return list;
    }

    // Retrieves the complete payroll history for a single employee identified by their ID.
    // Results are ordered by most recent year and month first.
    // Returns a list of mapped Payroll objects, or an empty list if none exist.
    // Throws DatabaseException if the SQL query fails.
    public List<Payroll> findByEmployee(int employeeId) throws DatabaseException {
        List<Payroll> list = new ArrayList<>();
        String sql = "SELECT payroll_id, employee_id, month, year, base_salary, overtime, " +
                "deductions, epf_employer, etf, net_salary, generated_by, generated_date " +
                "FROM payroll WHERE employee_id = ? ORDER BY year DESC, month DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to fetch employee payroll", e);
        }
        return list;
    }

    // Inserts a new payroll record into the database for the given Payroll object.
    // Uses RETURN_GENERATED_KEYS to retrieve and set the auto-generated payroll_id
    // back onto the Payroll object after a successful insert.
    // If generatedAt is null, falls back to today's date for the generated_date column.
    // Throws DatabaseException if the insert fails.
    public void save(Payroll p) throws DatabaseException {
        String sql = "INSERT INTO payroll (employee_id, month, year, base_salary, overtime, " +
                "deductions, epf_employer, etf, net_salary, generated_by, generated_date) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getEmployeeId());
            ps.setInt(2, p.getMonth());
            ps.setInt(3, p.getYear());
            ps.setDouble(4, p.getBaseSalary());
            ps.setDouble(5, p.getOvertimePay());
            ps.setDouble(6, p.getEpfEmployee());
            ps.setDouble(7, p.getEpfEmployer());
            ps.setDouble(8, p.getEtf());
            ps.setDouble(9, p.getNetSalary());
            ps.setInt(10, p.getGeneratedBy());
            ps.setDate(11, p.getGeneratedAt() != null ?
                    Date.valueOf(p.getGeneratedAt().toLocalDate()) : Date.valueOf(java.time.LocalDate.now()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setId(keys.getInt(1));
            }
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to save payroll", e);
        }
    }

    // Private helper that maps a single row from a ResultSet into a Payroll object.
    // Called by both findByMonthYear() and findByEmployee() to avoid duplicating
    // column-to-field mapping logic. Silently ignores the full_name column if it
    // is not present in the ResultSet (e.g. when the employees table is not joined).
    // Throws java.sql.SQLException if any required column cannot be read.
    private Payroll mapRow(ResultSet rs) throws java.sql.SQLException {
        Payroll p = new Payroll();
        p.setId(rs.getInt("payroll_id"));
        p.setEmployeeId(rs.getInt("employee_id"));
        p.setMonth(rs.getInt("month"));
        p.setYear(rs.getInt("year"));
        p.setBaseSalary(rs.getDouble("base_salary"));
        p.setOvertimePay(rs.getDouble("overtime"));
        p.setEpfEmployee(rs.getDouble("deductions"));
        p.setEpfEmployer(rs.getDouble("epf_employer"));
        p.setEtf(rs.getDouble("etf"));
        p.setNetSalary(rs.getDouble("net_salary"));
        p.setGeneratedBy(rs.getInt("generated_by"));
        try { p.setEmployeeName(rs.getString("full_name")); } catch (java.sql.SQLException ignored) {}
        return p;
    }
}