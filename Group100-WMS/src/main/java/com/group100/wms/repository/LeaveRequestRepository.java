package com.group100.wms.repository;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.model.LeaveRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// OOP Concepts used in this class:
// 1. Encapsulation: The class hides the complexity of SQL operations and uses private helper methods (map, updateStatus, applyToAttendance) to manage internal logic.
// 2. Abstraction: Provides a simplified interface for leave management (approve, reject, save) without exposing database implementation details to the caller.
public class LeaveRequestRepository {

    // Retrieves all leave requests from the database, including employee names and sections, ordered by date
    public List<LeaveRequest> findAll() throws SQLException {
        // Stores the SQL query string for joining leave requests with employee details
        String sql = """
                SELECT lr.*, e.full_name, e.section
                FROM leave_requests lr
                JOIN employees e ON lr.employee_id = e.employee_id
                ORDER BY lr.request_date DESC, lr.created_at DESC
                """;
        // Stores the list of LeaveRequest objects to be returned
        List<LeaveRequest> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    // Inserts a new leave request into the database with an initial 'PENDING' status
    public void save(LeaveRequest req) throws SQLException {
        // Stores the SQL query string for inserting a new leave request record
        String sql = """
                INSERT INTO leave_requests
                  (employee_id, request_date, leave_type, reason, status, created_by, created_at)
                VALUES (?, ?, ?, ?, 'PENDING', ?, NOW())
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, req.getEmployeeId());
            ps.setDate(2, java.sql.Date.valueOf(req.getRequestDate()));
            ps.setString(3, req.getLeaveType());
            ps.setString(4, req.getReason());
            ps.setInt(5, req.getCreatedBy());
            ps.executeUpdate();
        }
    }

    // High-level method to approve a leave request and automatically update the attendance record
    public void approve(int requestId) throws SQLException {
        updateStatus(requestId, "APPROVED");
        applyToAttendance(requestId);
    }

    // High-level method to reject a leave request
    public void reject(int requestId) throws SQLException {
        updateStatus(requestId, "REJECTED");
    }

    // Private helper method to update the status and review timestamp of a specific leave request
    private void updateStatus(int requestId, String status) throws SQLException {
        // Stores the SQL query string for updating the request status
        String sql = "UPDATE leave_requests SET status = ?, reviewed_at = NOW() WHERE request_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, requestId);
            ps.executeUpdate();
        }
    }

    // Internal logic to sync an approved leave request with the attendance_records table
    private void applyToAttendance(int requestId) throws SQLException {
        // SQL query to fetch data for the specific request
        String fetchSql = "SELECT employee_id, request_date, leave_type FROM leave_requests WHERE request_id = ?";
        // Stores the ID of the employee associated with the request
        int employeeId;
        // Stores the date for which the leave was requested
        java.time.LocalDate requestDate;
        // Stores the type of leave (e.g., FULL_DAY, HALF_DAY)
        String leaveType;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(fetchSql)) {
            ps.setInt(1, requestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return;
                employeeId  = rs.getInt("employee_id");
                requestDate = rs.getDate("request_date").toLocalDate();
                leaveType   = rs.getString("leave_type");
            }
        }
        // Determines the attendance status based on the leave type
        String newStatus = leaveType.equals("HALF_DAY") ? "HALF_DAY" : "ABSENT";
        // Query to check if an attendance record already exists for that date
        String checkSql = "SELECT attendance_id FROM attendance_records WHERE employee_id = ? AND date = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, employeeId);
            ps.setDate(2, java.sql.Date.valueOf(requestDate));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Stores the ID of the existing attendance record
                    int attId = rs.getInt("attendance_id");
                    try (Connection c2 = DatabaseConnection.getConnection();
                         PreparedStatement ps2 = c2.prepareStatement(
                             "UPDATE attendance_records SET status=?, notes=? WHERE attendance_id=?")) {
                        ps2.setString(1, newStatus);
                        ps2.setString(2, "Leave request approved by supervisor");
                        ps2.setInt(3, attId);
                        ps2.executeUpdate();
                    }
                } else {
                    try (Connection c2 = DatabaseConnection.getConnection();
                         PreparedStatement ps2 = c2.prepareStatement(
                             "INSERT INTO attendance_records (employee_id, date, clock_in, clock_out, status, notes) VALUES (?, ?, NULL, NULL, ?, 'Leave request approved by supervisor')")) {
                        ps2.setInt(1, employeeId);
                        ps2.setDate(2, java.sql.Date.valueOf(requestDate));
                        ps2.setString(3, newStatus);
                        ps2.executeUpdate();
                    }
                }
            }
        }
    }

    // Maps a row from the database ResultSet into a LeaveRequest model object
    private LeaveRequest map(ResultSet rs) throws SQLException {
        // Stores the new LeaveRequest instance being built
        LeaveRequest lr = new LeaveRequest();
        lr.setRequestId(rs.getInt("request_id"));
        lr.setEmployeeId(rs.getInt("employee_id"));
        lr.setEmployeeName(rs.getString("full_name"));
        lr.setSection(rs.getString("section"));
        // Temporary variable to handle null date conversion
        java.sql.Date rd = rs.getDate("request_date");
        if (rd != null) lr.setRequestDate(rd.toLocalDate());
        lr.setLeaveType(rs.getString("leave_type"));
        lr.setReason(rs.getString("reason"));
        lr.setStatus(rs.getString("status"));
        lr.setCreatedBy(rs.getInt("created_by"));
        // Temporary variable to handle null timestamp conversion for creation time
        Timestamp ca = rs.getTimestamp("created_at");
        if (ca != null) lr.setCreatedAt(ca.toLocalDateTime());
        // Temporary variable to handle null timestamp conversion for review time
        Timestamp ra = rs.getTimestamp("reviewed_at");
        if (ra != null) lr.setReviewedAt(ra.toLocalDateTime());
        return lr;
    }
}
