package com.group100.wms.repository;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.Forecast;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ForecastRepository {

    public List<Forecast> findAll() throws DatabaseException {
        List<Forecast> list = new ArrayList<>();
        String sql = "SELECT forecast_id, item_id, warehouse_id, predicted_qty, " +
                "confidence, generated_date, method FROM forecasts ORDER BY generated_date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch all forecasts", e);
        }
        return list;
    }

    public List<Forecast> findByWarehouseId(int warehouseId) throws DatabaseException {
        List<Forecast> list = new ArrayList<>();
        String sql = "SELECT forecast_id, item_id, warehouse_id, predicted_qty, " +
                "confidence, generated_date, method FROM forecasts WHERE warehouse_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, warehouseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch forecasts by warehouse", e);
        }
        return list;
    }

    public List<Forecast> findByItemId(int itemId) throws DatabaseException {
        List<Forecast> list = new ArrayList<>();
        String sql = "SELECT forecast_id, item_id, warehouse_id, predicted_qty, " +
                "confidence, generated_date, method FROM forecasts WHERE item_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to fetch forecasts by item", e);
        }
        return list;
    }

    public void save(Forecast forecast) throws DatabaseException {
        String sql = "INSERT INTO forecasts (item_id, warehouse_id, predicted_qty, " +
                "confidence, generated_date, method) VALUES (?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, forecast.getItemId());
            ps.setInt(2, forecast.getWarehouseId());
            ps.setInt(3, forecast.getForecastedQuantity());
            ps.setDouble(4, forecast.getConfidenceLower());
            ps.setDate(5, forecast.getForecastDate() != null
                    ? java.sql.Date.valueOf(forecast.getForecastDate()) : null);
            ps.setString(6, forecast.getModelUsed());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) forecast.setId(keys.getInt(1));
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to save forecast", e);
        }
    }

    public void deleteByItemAndWarehouse(int itemId, int warehouseId) throws DatabaseException {
        String sql = "DELETE FROM forecasts WHERE item_id = ? AND warehouse_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            ps.setInt(2, warehouseId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Failed to delete forecasts", e);
        }
    }

    private Forecast mapRow(ResultSet rs) throws SQLException {
        java.sql.Date gd = rs.getDate("generated_date");
        return new Forecast(
                rs.getInt("forecast_id"),
                rs.getInt("item_id"),
                rs.getInt("warehouse_id"),
                gd != null ? gd.toLocalDate() : null,
                rs.getInt("predicted_qty"),
                rs.getDouble("confidence"),
                rs.getDouble("confidence"),
                rs.getString("method"));
    }
}