package com.group100.wms.service;

import java.util.List;

import com.group100.wms.exception.DatabaseException;
import com.group100.wms.model.Forecast;
import com.group100.wms.repository.ForecastRepository;

// OOP Concepts: Encapsulation (private repository), Abstraction (business logic hidden from controller),
// Dependency Injection (repository injected), Inheritance (extends object pattern)
public class ForecastService {

    // Repository instance for accessing forecast data from database
    private final ForecastRepository forecastRepository;

    // Constructor accepting repository dependency for data access operations
    public ForecastService(ForecastRepository forecastRepository) {
        this.forecastRepository = forecastRepository;
    }

    // Retrieves all forecast records from the database
    public List<Forecast> getAllForecasts() throws DatabaseException {
        return forecastRepository.findAll();
    }

    // Retrieves all forecast records for a specific item ID
    public List<Forecast> getForecastsByItem(int itemId) throws DatabaseException {
        return forecastRepository.findByItemId(itemId);
    }

    // Retrieves all forecast records for a specific warehouse ID
    public List<Forecast> getForecastsByWarehouse(int warehouseId) throws DatabaseException {
        return forecastRepository.findByWarehouseId(warehouseId);
    }

    // Saves a forecast record after deleting any existing forecasts for the same item-warehouse combination
    public void saveForecast(Forecast forecast) throws DatabaseException {
        forecastRepository.deleteByItemAndWarehouse(
                forecast.getItemId(), forecast.getWarehouseId());
        forecastRepository.save(forecast);
    }

    // Executes the Python AI forecasting script to generate new forecast predictions for specified item and warehouse
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