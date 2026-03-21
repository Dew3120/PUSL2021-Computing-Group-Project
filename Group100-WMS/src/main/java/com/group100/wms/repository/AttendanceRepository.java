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

public class AttendanceRepository {

    public List<AttendanceRecord> findByMonthYear(int month, int year) throws DatabaseException {
        List<AttendanceRecord> list = new ArrayList<>();
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

    public List<AttendanceRecord> findByEmployee(int employeeId) throws DatabaseException {
        List<AttendanceRecord> list = new ArrayList<>();
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

    public void save(AttendanceRecord r) throws DatabaseException {
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

    public void update(AttendanceRecord r) throws DatabaseException {
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

    private AttendanceRecord mapRow(ResultSet rs) throws java.sql.SQLException {
        AttendanceRecord r = new AttendanceRecord();
        r.setId(rs.getInt("attendance_id"));
        r.setEmployeeId(rs.getInt("employee_id"));
        r.setDate(rs.getDate("date").toLocalDate());
        Time ci = rs.getTime("clock_in");
        Time co = rs.getTime("clock_out");
        r.setClockIn(ci != null ? ci.toLocalTime() : null);
        r.setClockOut(co != null ? co.toLocalTime() : null);
        r.setStatus(rs.getString("status"));
        r.setApprovedBy(rs.getInt("approved_by"));
        try { r.setEmployeeName(rs.getString("full_name")); } catch (java.sql.SQLException ignored) {}
        return r;
    }
}