package com.group100.wms.repository;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.GinItem;
import com.group100.wms.model.GoodsIssueNote;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GinRepository {

    public List<GoodsIssueNote> findAll() throws DatabaseException {
        List<GoodsIssueNote> list = new ArrayList<>();
        String sql = "SELECT gin_id, warehouse_id, destination, destination_type, " +
                "issued_by, issued_date, status " +
                "FROM goods_issue_notes ORDER BY issued_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to fetch GINs", e);
        }
        return list;
    }

    public Optional<GoodsIssueNote> findById(int ginId) throws DatabaseException {
        String sql = "SELECT gin_id, warehouse_id, destination, destination_type, " +
                "issued_by, issued_date, status " +
                "FROM goods_issue_notes WHERE gin_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ginId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    GoodsIssueNote gin = mapRow(rs);
                    gin.setItems(findItemsByGinId(conn, ginId));
                    return Optional.of(gin);
                }
            }
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to find GIN", e);
        }
        return Optional.empty();
    }

    public void save(GoodsIssueNote gin) throws DatabaseException {
        String sql = "INSERT INTO goods_issue_notes " +
                "(warehouse_id, destination, destination_type, issued_by, issued_date, status) " +
                "VALUES (?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection()) {
            boolean originalAutoCommit = conn.getAutoCommit();
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, gin.getWarehouseId());
                ps.setString(2, gin.getDestination());
                ps.setString(3, gin.getDestType());
                ps.setInt(4, gin.getIssuedBy());
                ps.setDate(5, Date.valueOf(gin.getIssuedDate()));
                ps.setString(6, gin.getStatus());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) gin.setId(keys.getInt(1));
                }
                saveItems(conn, gin);
                conn.commit();
            } catch (java.sql.SQLException e) {
                conn.rollback();
                throw e;
            } finally {
                conn.setAutoCommit(originalAutoCommit);
            }
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to save GIN", e);
        }
    }

    public void updateStatus(int ginId, String status) throws DatabaseException {
        String sql = "UPDATE goods_issue_notes SET status = ? WHERE gin_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, ginId);
            ps.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to update GIN status", e);
        }
    }

    private void saveItems(Connection conn, GoodsIssueNote gin) throws java.sql.SQLException {
        if (gin.getItems() == null || gin.getItems().isEmpty()) return;
        String sql = "INSERT INTO gin_items (gin_id, item_id, batch_id, quantity) VALUES (?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            for (GinItem item : gin.getItems()) {
                if (item.getBatchId() <= 0) {
                    throw new java.sql.SQLException("GIN item is missing FIFO batch allocation for item_id=" + item.getItemId());
                }
                ps.setInt(1, gin.getId());
                ps.setInt(2, item.getItemId());
                ps.setInt(3, item.getBatchId());
                ps.setInt(4, item.getQuantityIssued());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) item.setId(keys.getInt(1));
                }
                item.setGinId(gin.getId());
            }
        }
    }

    private List<GinItem> findItemsByGinId(Connection conn, int ginId) throws java.sql.SQLException {
        List<GinItem> items = new ArrayList<>();
        String sql = "SELECT gin_item_id, gin_id, item_id, batch_id, quantity FROM gin_items WHERE gin_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ginId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new GinItem(
                            rs.getInt("gin_item_id"),
                            rs.getInt("gin_id"),
                            rs.getInt("item_id"),
                            rs.getInt("batch_id"),
                            rs.getInt("quantity"),
                            0.0));
                }
            }
        }
        return items;
    }

    private GoodsIssueNote mapRow(ResultSet rs) throws java.sql.SQLException {
        return new GoodsIssueNote(
                rs.getInt("gin_id"),
                rs.getInt("warehouse_id"),
                rs.getString("destination"),
                rs.getString("destination_type"),
                rs.getInt("issued_by"),
                rs.getDate("issued_date").toLocalDate(),
                rs.getString("status"));
    }
}
