package com.group100.wms.repository;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.PurchaseOrder;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PurchaseOrderRepository {

    public List<PurchaseOrder> findAll() throws DatabaseException {
        List<PurchaseOrder> list = new ArrayList<>();
        String sql = "SELECT p.po_id, p.supplier_id, p.warehouse_id, " +
                "p.order_date, p.expected_date, p.status, s.name AS supplier_name " +
                "FROM purchase_orders p " +
                "JOIN suppliers s ON p.supplier_id = s.supplier_id " +
                "ORDER BY p.order_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to fetch all POs", e);
        }
        return list;
    }

    public List<PurchaseOrder> findByStatus(String status) throws DatabaseException {
        List<PurchaseOrder> list = new ArrayList<>();
        String sql = "SELECT po_id, supplier_id, warehouse_id, order_date, expected_date, status " +
                "FROM purchase_orders WHERE status = ? ORDER BY order_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to fetch POs by status", e);
        }
        return list;
    }

    public Optional<PurchaseOrder> findById(int id) throws DatabaseException {
        String sql = "SELECT po_id, supplier_id, warehouse_id, order_date, expected_date, status " +
                "FROM purchase_orders WHERE po_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to find PO", e);
        }
        return Optional.empty();
    }

    public void save(PurchaseOrder po) throws DatabaseException {
        String sql = "INSERT INTO purchase_orders (supplier_id, warehouse_id, order_date, expected_date, status) " +
                "VALUES (?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, po.getSupplierId());
            ps.setInt(2, po.getWarehouseId());
            ps.setDate(3, po.getOrderDate() != null ? Date.valueOf(po.getOrderDate()) : null);
            ps.setDate(4, po.getExpectedDeliveryDate() != null ? Date.valueOf(po.getExpectedDeliveryDate()) : null);
            ps.setString(5, po.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) po.setId(keys.getInt(1));
            }
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to save PO", e);
        }
    }

    public void updateStatus(int id, String status) throws DatabaseException {
        String sql = "UPDATE purchase_orders SET status = ? WHERE po_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to update PO status", e);
        }
    }

    private PurchaseOrder mapRow(ResultSet rs) throws java.sql.SQLException {
        PurchaseOrder po = new PurchaseOrder();
        po.setId(rs.getInt("po_id"));
        po.setSupplierId(rs.getInt("supplier_id"));
        po.setWarehouseId(rs.getInt("warehouse_id"));
        java.sql.Date od = rs.getDate("order_date");
        java.sql.Date ed = rs.getDate("expected_date");
        if (od != null) po.setOrderDate(od.toLocalDate());
        if (ed != null) po.setExpectedDeliveryDate(ed.toLocalDate());
        po.setStatus(rs.getString("status"));
        try {
            po.setPoNumber(rs.getString("supplier_name"));
        } catch (java.sql.SQLException ignored) {}
        return po;
    }
}