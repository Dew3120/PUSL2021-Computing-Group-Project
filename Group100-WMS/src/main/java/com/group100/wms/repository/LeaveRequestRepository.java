package com.group100.wms.repository;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.model.LeaveRequest;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaveRequestRepository {

    public List<LeaveRequest> findAll() throws SQLException {
        String sql = """
                SELECT lr.*, e.full_name, e.section
                FROM leave_requests lr
                JOIN employees e ON lr.employee_id = e.employee_id
                ORDER BY lr.request_date DESC, lr.created_at DESC
                """;
        List<LeaveRequest> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }

    public void save(LeaveRequest req) throws SQLException {
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

    public void approve(int requestId) throws SQLException {
        updateStatus(requestId, "APPROVED");
        applyToAttendance(requestId);
    }

    public void reject(int requestId) throws SQLException {
        updateStatus(requestId, "REJECTED");
    }

    private void updateStatus(int requestId, String status) throws SQLException {
        String sql = "UPDATE leave_requests SET status = ?, reviewed_at = NOW() WHERE request_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, requestId);
            ps.executeUpdate();
        }
    }

    private void applyToAttendance(int requestId) throws SQLException {
        String fetchSql = "SELECT employee_id, request_date, leave_type FROM leave_requests WHERE request_id = ?";
        int employeeId;
        java.time.LocalDate requestDate;
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
        String newStatus = leaveType.equals("HALF_DAY") ? "HALF_DAY" : "ABSENT";
        String checkSql = "SELECT attendance_id FROM attendance_records WHERE employee_id = ? AND date = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, employeeId);
            ps.setDate(2, java.sql.Date.valueOf(requestDate));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
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

    private LeaveRequest map(ResultSet rs) throws SQLException {
        LeaveRequest lr = new LeaveRequest();
        lr.setRequestId(rs.getInt("request_id"));
        lr.setEmployeeId(rs.getInt("employee_id"));
        lr.setEmployeeName(rs.getString("full_name"));
        lr.setSection(rs.getString("section"));
        java.sql.Date rd = rs.getDate("request_date");
        if (rd != null) lr.setRequestDate(rd.toLocalDate());
        lr.setLeaveType(rs.getString("leave_type"));
        lr.setReason(rs.getString("reason"));
        lr.setStatus(rs.getString("status"));
        lr.setCreatedBy(rs.getInt("created_by"));
        Timestamp ca = rs.getTimestamp("created_at");
        if (ca != null) lr.setCreatedAt(ca.toLocalDateTime());
        Timestamp ra = rs.getTimestamp("reviewed_at");
        if (ra != null) lr.setReviewedAt(ra.toLocalDateTime());
        return lr;
    }
}
