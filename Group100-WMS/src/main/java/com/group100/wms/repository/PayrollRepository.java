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