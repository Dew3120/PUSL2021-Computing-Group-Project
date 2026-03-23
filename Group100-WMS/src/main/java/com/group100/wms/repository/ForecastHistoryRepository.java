package com.group100.wms.repository;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.model.ForecastHistory;

// OOP Concepts: Encapsulation (private database operations), Abstraction (query logic hidden),
// Inheritance (extends repository pattern), Polymorphism (multiple query methods)
public class ForecastHistoryRepository {

    // Retrieves all forecast history records from database with item and warehouse names via JOINs
    public List<ForecastHistory> findAll() {
        List<ForecastHistory> list = new ArrayList<>();
        String sql = "SELECT fh.*, i.name AS item_name, w.name AS warehouse_name " +
                "FROM forecast_history fh " +
                "JOIN items i ON fh.item_id = i.item_id " +
                "JOIN warehouses w ON fh.warehouse_id = w.warehouse_id " +
                "ORDER BY fh.forecast_year DESC, fh.forecast_month DESC, i.name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Retrieves forecast history records filtered by result type (HIT, FAIR, or MISS)
    public List<ForecastHistory> findByResult(String result) {
        List<ForecastHistory> list = new ArrayList<>();
        String sql = "SELECT fh.*, i.name AS item_name, w.name AS warehouse_name " +
                "FROM forecast_history fh " +
                "JOIN items i ON fh.item_id = i.item_id " +
                "JOIN warehouses w ON fh.warehouse_id = w.warehouse_id " +
                "WHERE fh.result = ? " +
                "ORDER BY fh.forecast_year DESC, fh.forecast_month DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, result);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Retrieves forecast history records for a specific year and month combination
    public List<ForecastHistory> findByYearMonth(int year, int month) {
        List<ForecastHistory> list = new ArrayList<>();
        String sql = "SELECT fh.*, i.name AS item_name, w.name AS warehouse_name " +
                "FROM forecast_history fh " +
                "JOIN items i ON fh.item_id = i.item_id " +
                "JOIN warehouses w ON fh.warehouse_id = w.warehouse_id " +
                "WHERE fh.forecast_year = ? AND fh.forecast_month = ? " +
                "ORDER BY i.name";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Counts the total number of forecast history records matching a specific result type
    public int countByResult(String result) {
        String sql = "SELECT COUNT(*) FROM forecast_history WHERE result = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, result);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Calculates and returns the average accuracy percentage across all forecast history records
    public double averageAccuracy() {
        String sql = "SELECT AVG(accuracy) FROM forecast_history";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Maps a database result set row to a ForecastHistory model object with all fields populated
    private ForecastHistory mapRow(ResultSet rs) throws SQLException {
        ForecastHistory fh = new ForecastHistory();
        fh.setHistoryId(rs.getInt("history_id"));
        fh.setItemId(rs.getInt("item_id"));
        fh.setWarehouseId(rs.getInt("warehouse_id"));
        fh.setForecastMonth(rs.getInt("forecast_month"));
        fh.setForecastYear(rs.getInt("forecast_year"));
        fh.setPredictedQty(rs.getDouble("predicted_qty"));
        fh.setActualQty(rs.getDouble("actual_qty"));
        fh.setAccuracy(rs.getDouble("accuracy"));
        fh.setResult(rs.getString("result"));
        fh.setConfidence(rs.getDouble("confidence"));
        fh.setMethod(rs.getString("method"));
        Date d = rs.getDate("generated_date");
        if (d != null) fh.setGeneratedDate(d.toLocalDate());
        fh.setItemName(rs.getString("item_name"));
        fh.setWarehouseName(rs.getString("warehouse_name"));
        return fh;
    }
}