package com.group100.wms.repository;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.AttendanceRecord;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

// OOP Concepts used in this class:
// 1. Encapsulation: The class bundles data access logic for attendance and uses a private helper method (mapRow) to handle internal object mapping.
// 2. Abstraction: Simplifies database interactions by providing high-level methods like save() and update(), hiding the complexity of SQL queries and connection handling.
public class AttendanceRepository {

    // Retrieves all attendance records for a specific month and year, joining with employee names
    public List<AttendanceRecord> findByMonthYear(int month, int year) throws DatabaseException {
        // Stores the collection of attendance records found for the given period
        List<AttendanceRecord> list = new ArrayList<>();
        // Stores the SQL query string used to fetch and join attendance and employee data
        String sql = "SELECT a.attendance_id, a.employee_id, a.date, a.clock_in, a.clock_out, " +
                "a.status, a.approved_by, e.full_name " +
                "FROM attendance_records a " +
                "JOIN employees e ON a.employee_id = e.employee_id " +
                "WHERE MONTH(a.date) = ? AND YEAR(a.date) = ? " +
                "ORDER BY a.date, e.full_name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to fetch attendance", e);
        }
        return list;
    }

    // Retrieves all attendance history for a specific employee ID
    public List<AttendanceRecord> findByEmployee(int employeeId) throws DatabaseException {
        // Stores the collection of attendance records for the specific employee
        List<AttendanceRecord> list = new ArrayList<>();
        // Stores the SQL query string to filter attendance by employee ID
        String sql = "SELECT attendance_id, employee_id, date, clock_in, clock_out, status, approved_by " +
                "FROM attendance_records WHERE employee_id = ? ORDER BY date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to fetch employee attendance", e);
        }
        return list;
    }

    // Inserts a new attendance record into the database and retrieves the generated primary key
    public void save(AttendanceRecord r) throws DatabaseException {
        // Stores the SQL query string for inserting new attendance data
        String sql = "INSERT INTO attendance_records (employee_id, date, clock_in, clock_out, status, approved_by) " +
                "VALUES (?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, r.getEmployeeId());
            ps.setDate(2, Date.valueOf(r.getDate()));
            ps.setTime(3, r.getClockIn() != null ? Time.valueOf(r.getClockIn()) : null);
            ps.setTime(4, r.getClockOut() != null ? Time.valueOf(r.getClockOut()) : null);
            ps.setString(5, r.getStatus());
            ps.setInt(6, r.getApprovedBy());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) r.setId(keys.getInt(1));
            }
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to save attendance", e);
        }
    }

    // Updates the clock times, status, and approval details of an existing attendance record
    public void update(AttendanceRecord r) throws DatabaseException {
        // Stores the SQL query string for updating an existing record based on its ID
        String sql = "UPDATE attendance_records SET clock_in=?, clock_out=?, status=?, approved_by=? " +
                "WHERE attendance_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTime(1, r.getClockIn() != null ? Time.valueOf(r.getClockIn()) : null);
            ps.setTime(2, r.getClockOut() != null ? Time.valueOf(r.getClockOut()) : null);
            ps.setString(3, r.getStatus());
            ps.setInt(4, r.getApprovedBy());
            ps.setInt(5, r.getId());
            ps.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to update attendance", e);
        }
    }

    // Maps a row from the database ResultSet to an AttendanceRecord model object
    private AttendanceRecord mapRow(ResultSet rs) throws java.sql.SQLException {
        // Stores the temporary AttendanceRecord object being populated
        AttendanceRecord r = new AttendanceRecord();
        r.setId(rs.getInt("attendance_id"));
        r.setEmployeeId(rs.getInt("employee_id"));
        r.setDate(rs.getDate("date").toLocalDate());
        // Stores the SQL Time value for clock-in to check for nulls
        Time ci = rs.getTime("clock_in");
        // Stores the SQL Time value for clock-out to check for nulls
        Time co = rs.getTime("clock_out");
        r.setClockIn(ci != null ? ci.toLocalTime() : null);
        r.setClockOut(co != null ? co.toLocalTime() : null);
        r.setStatus(rs.getString("status"));
        r.setApprovedBy(rs.getInt("approved_by"));
        try { r.setEmployeeName(rs.getString("full_name")); } catch (java.sql.SQLException ignored) {}
        return r;
    }
}
