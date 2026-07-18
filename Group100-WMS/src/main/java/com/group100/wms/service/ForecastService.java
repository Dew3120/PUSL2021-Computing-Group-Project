package com.group100.wms.service;

import com.group100.wms.core.DatabaseConnection;
import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.Forecast;
import com.group100.wms.repository.ForecastRepository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ForecastService {

    private final ForecastRepository forecastRepository;

    public ForecastService(ForecastRepository forecastRepository) {
        this.forecastRepository = forecastRepository;
    }

    public List<Forecast> getAllForecasts() throws DatabaseException {
        return forecastRepository.findAll();
    }

    public List<Forecast> getForecastsByItem(int itemId) throws DatabaseException {
        return forecastRepository.findByItemId(itemId);
    }

    public List<Forecast> getForecastsByWarehouse(int warehouseId) throws DatabaseException {
        return forecastRepository.findByWarehouseId(warehouseId);
    }

    public void saveForecast(Forecast forecast) throws DatabaseException {
        forecastRepository.deleteByItemAndWarehouse(
                forecast.getItemId(), forecast.getWarehouseId());
        forecastRepository.save(forecast);
    }

    public void runForecast(int itemId, int warehouseId) throws DatabaseException {
        List<Integer> demandSeries = loadDemandSeries(itemId, warehouseId);
        Forecast forecast = createForecastFromSeries(itemId, warehouseId, demandSeries, LocalDate.now());
        saveForecast(forecast);
    }

    static Forecast createForecastFromSeries(int itemId, int warehouseId,
                                             List<Integer> demandSeries,
                                             LocalDate runDate) {
        List<Integer> series = demandSeries == null ? List.of() : demandSeries;
        int predictedQty;
        double confidence;

        if (series.isEmpty()) {
            predictedQty = 10;
            confidence = 0.55;
        } else {
            double weightedTotal = 0;
            double weightSum = 0;
            for (int i = 0; i < series.size(); i++) {
                int weight = i + 1;
                weightedTotal += Math.max(0, series.get(i)) * weight;
                weightSum += weight;
            }
            double weightedAverage = weightedTotal / weightSum;
            double trend = series.size() > 1
                    ? (series.get(series.size() - 1) - series.get(0)) / (double) (series.size() - 1)
                    : 0.0;
            predictedQty = Math.max(1, (int) Math.round(weightedAverage + (trend * 0.25)));

            double mean = series.stream().mapToInt(Integer::intValue).average().orElse(0.0);
            double variance = series.stream()
                    .mapToDouble(v -> Math.pow(v - mean, 2))
                    .average().orElse(0.0);
            double coefficientOfVariation = mean <= 0 ? 1.0 : Math.sqrt(variance) / mean;
            confidence = clamp(0.55, 0.92,
                    0.62 + Math.min(series.size(), 6) * 0.04 - coefficientOfVariation * 0.12);
        }

        Forecast forecast = new Forecast();
        forecast.setItemId(itemId);
        forecast.setWarehouseId(warehouseId);
        forecast.setForecastDate(runDate.plusMonths(1).withDayOfMonth(1));
        forecast.setForecastedQuantity(predictedQty);
        forecast.setConfidence(confidence);
        forecast.setConfidenceLower(Math.max(0, predictedQty * (1 - (1 - confidence))));
        forecast.setConfidenceUpper(predictedQty * (1 + (1 - confidence)));
        forecast.setModelUsed(series.size() >= 3 ? "WMA_TREND" : "BASELINE_AVG");
        return forecast;
    }

    private List<Integer> loadDemandSeries(int itemId, int warehouseId) throws DatabaseException {
        List<Integer> series = loadIssueDemandSeries(itemId, warehouseId);
        if (!series.isEmpty()) return series;
        return loadForecastHistorySeries(itemId, warehouseId);
    }

    private List<Integer> loadIssueDemandSeries(int itemId, int warehouseId) throws DatabaseException {
        String sql = "SELECT YEAR(g.issued_date) AS y, MONTH(g.issued_date) AS m, "
                + "SUM(gi.quantity) AS qty "
                + "FROM gin_items gi "
                + "JOIN goods_issue_notes g ON gi.gin_id = g.gin_id "
                + "WHERE gi.item_id = ? AND g.warehouse_id = ? "
                + "GROUP BY YEAR(g.issued_date), MONTH(g.issued_date) "
                + "ORDER BY y, m";
        return runSeriesQuery(sql, itemId, warehouseId, "qty");
    }

    private List<Integer> loadForecastHistorySeries(int itemId, int warehouseId) throws DatabaseException {
        String sql = "SELECT forecast_year AS y, forecast_month AS m, "
                + "COALESCE(actual_qty, predicted_qty) AS qty "
                + "FROM forecast_history "
                + "WHERE item_id = ? AND warehouse_id = ? "
                + "ORDER BY forecast_year, forecast_month";
        return runSeriesQuery(sql, itemId, warehouseId, "qty");
    }

    private List<Integer> runSeriesQuery(String sql, int itemId, int warehouseId, String qtyColumn)
            throws DatabaseException {
        List<Integer> series = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, itemId);
            ps.setInt(2, warehouseId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    series.add((int) Math.round(rs.getDouble(qtyColumn)));
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Failed to load demand series", e);
        }
        return series;
    }

    private static double clamp(double min, double max, double value) {
        return Math.max(min, Math.min(max, value));
    }
}
