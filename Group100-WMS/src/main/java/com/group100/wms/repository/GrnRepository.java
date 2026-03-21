package com.group100.wms.repository;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.GoodsReceivedNote;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GrnRepository {

    public List<GoodsReceivedNote> findAll() throws DatabaseException {
        List<GoodsReceivedNote> list = new ArrayList<>();
        String sql = "SELECT g.grn_id, g.po_id, g.warehouse_id, g.supplier_id, " +
                "g.receipt_date, g.status, g.received_by, s.name AS supplier_name " +
                "FROM goods_received_notes g " +
                "JOIN suppliers s ON g.supplier_id = s.supplier_id " +
                "ORDER BY g.receipt_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to fetch GRNs", e);
        }
        return list;
    }

    public Optional<GoodsReceivedNote> findById(int grnId) throws DatabaseException {
        String sql = "SELECT grn_id, po_id, warehouse_id, supplier_id, " +
                "receipt_date, status, received_by " +
                "FROM goods_received_notes WHERE grn_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, grnId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to find GRN", e);
        }
        return Optional.empty();
    }

    public void save(GoodsReceivedNote grn) throws DatabaseException {
        String sql = "INSERT INTO goods_received_notes " +
                "(po_id, warehouse_id, supplier_id, receipt_date, status, received_by) " +
                "VALUES (?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, grn.getPoId());
            ps.setInt(2, grn.getWarehouseId());
            ps.setInt(3, grn.getSupplierId());
            ps.setDate(4, Date.valueOf(grn.getReceivedDate()));
            ps.setString(5, grn.getStatus());
            ps.setInt(6, grn.getReceivedBy());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) grn.setId(keys.getInt(1));
            }
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to save GRN", e);
        }
    }

    public void updateStatus(int grnId, String status) throws DatabaseException {
        String sql = "UPDATE goods_received_notes SET status = ? WHERE grn_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, grnId);
            ps.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to update GRN status", e);
        }
    }

    private GoodsReceivedNote mapRow(ResultSet rs) throws java.sql.SQLException {
        GoodsReceivedNote g = new GoodsReceivedNote(
                rs.getInt("grn_id"),
                rs.getInt("po_id"),
                rs.getInt("warehouse_id"),
                rs.getInt("supplier_id"),
                rs.getDate("receipt_date").toLocalDate(),
                rs.getString("status"),
                rs.getInt("received_by"));
        try { g.setSupplierName(rs.getString("supplier_name")); }
        catch (java.sql.SQLException ignored) {}
        return g;
    }
}

