package com.group100.wms.service;

import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.Forecast;
import com.group100.wms.repository.ForecastRepository;

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
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "python", "ai-module/forecast.py",
                    String.valueOf(itemId), String.valueOf(warehouseId));
            pb.redirectErrorStream(true);
            Process process = pb.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                System.err.println("[FORECAST] Python script exited with code: " + exitCode);
            }
        } catch (Exception e) {
            System.err.println("[FORECAST] Failed to run forecast script: " + e.getMessage());
        }
    }
}