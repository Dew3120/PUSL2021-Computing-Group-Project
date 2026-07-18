package com.group100.wms.service;

import com.group100.wms.model.Forecast;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ForecastServiceTest {

    @Test
    void createsTrendForecastFromDemandSeries() {
        Forecast forecast = ForecastService.createForecastFromSeries(
                12, 1, List.of(100, 110, 120, 140), LocalDate.of(2026, 7, 18));

        assertEquals(12, forecast.getItemId());
        assertEquals(1, forecast.getWarehouseId());
        assertEquals(LocalDate.of(2026, 8, 1), forecast.getForecastDate());
        assertTrue(forecast.getForecastedQuantity() >= 120);
        assertTrue(forecast.getConfidence() >= 0.55 && forecast.getConfidence() <= 0.92);
        assertEquals("WMA_TREND", forecast.getModelUsed());
    }

    @Test
    void usesBaselineWhenNoHistoryExists() {
        Forecast forecast = ForecastService.createForecastFromSeries(
                12, 1, List.of(), LocalDate.of(2026, 7, 18));

        assertEquals(10, forecast.getForecastedQuantity());
        assertEquals(0.55, forecast.getConfidence(), 0.001);
        assertEquals("BASELINE_AVG", forecast.getModelUsed());
    }
}
