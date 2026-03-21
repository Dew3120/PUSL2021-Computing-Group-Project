package com.group100.wms.repository;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.Batch;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BatchRepository {

    // FIFO — oldest receipt_date first, only batches with stock remaining
    public List<Batch> findByItemFIFO(int itemId) throws DatabaseException {
        List<Batch> list = new ArrayList<>();
        String sql = "SELECT batch_id, po_id, item_id, quantity, available_qty, unit_cost, receipt_date " +
                "FROM batches WHERE item_id = ? AND available_qty > 0 ORDER BY receipt_date ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to get FIFO batches", e);
        }
        return list;
    }

    public List<Batch> findByItem(int itemId) throws DatabaseException {
        List<Batch> list = new ArrayList<>();
        String sql = "SELECT batch_id, po_id, item_id, quantity, available_qty, unit_cost, receipt_date " +
                "FROM batches WHERE item_id = ? ORDER BY receipt_date ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to get batches", e);
        }
        return list;
    }

    public Optional<Batch> findById(int batchId) throws DatabaseException {
        String sql = "SELECT batch_id, po_id, item_id, quantity, available_qty, unit_cost, receipt_date " +
                "FROM batches WHERE batch_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, batchId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(mapRow(rs));
            }
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to find batch", e);
        }
        return Optional.empty();
    }

    public void save(Batch batch) throws DatabaseException {
        String sql = "INSERT INTO batches (po_id, item_id, quantity, available_qty, unit_cost, receipt_date) " +
                "VALUES (?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, batch.getPoId());
            ps.setInt(2, batch.getItemId());
            ps.setInt(3, batch.getQuantity());
            ps.setInt(4, batch.getAvailableQty());
            ps.setDouble(5, batch.getUnitCost());
            ps.setDate(6, Date.valueOf(batch.getReceiptDate()));
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) batch.setId(keys.getInt(1));
            }
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to save batch", e);
        }
    }

    public void updateAvailableQty(int batchId, int newQty) throws DatabaseException {
        String sql = "UPDATE batches SET available_qty = ? WHERE batch_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, newQty);
            ps.setInt(2, batchId);
            ps.executeUpdate();
        } catch (java.sql.SQLException e) {
            throw new DatabaseException("Failed to update batch qty", e);
        }
    }

    private Batch mapRow(ResultSet rs) throws java.sql.SQLException {
        return new Batch(
                rs.getInt("batch_id"),
                rs.getInt("po_id"),
                rs.getInt("item_id"),
                rs.getInt("quantity"),
                rs.getInt("available_qty"),
                rs.getDouble("unit_cost"),
                rs.getDate("receipt_date").toLocalDate());
    }
}