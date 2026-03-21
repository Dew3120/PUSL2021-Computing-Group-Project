-- ============================================================
--  Group 100 — Centralized Apparel WMS
--  Migration: V1_1_0 — Add Forecast Confidence Enhancements
--  Date: 2026-01-10 (Sprint 3)
--  Description: Adds confidence interval columns and forecast
--               metadata to support ARIMA prediction reporting
-- ============================================================

USE group100_wms;

-- ── Add confidence interval bounds to forecasts ─────────────
ALTER TABLE forecasts
    ADD COLUMN confidence_lower DECIMAL(10,2) DEFAULT NULL
    AFTER confidence;

ALTER TABLE forecasts
    ADD COLUMN confidence_upper DECIMAL(10,2) DEFAULT NULL
    AFTER confidence_lower;

-- ── Add forecast horizon (weeks ahead) ──────────────────────
ALTER TABLE forecasts
    ADD COLUMN horizon_weeks INT DEFAULT 4
    AFTER method;

-- ── Add MAPE accuracy metric per forecast ───────────────────
ALTER TABLE forecasts
    ADD COLUMN mape DECIMAL(5,2) DEFAULT NULL
    AFTER horizon_weeks;

-- ── Add index for faster dashboard queries ──────────────────
CREATE INDEX idx_forecast_item_warehouse
    ON forecasts (item_id, warehouse_id, generated_date DESC);

-- ── Update existing forecasts with default confidence bounds ─
UPDATE forecasts
SET confidence_lower = predicted_qty * (1 - (1 - confidence)),
    confidence_upper = predicted_qty * (1 + (1 - confidence))
WHERE confidence_lower IS NULL;

-- ============================================================
--  Migration V1_1_0 complete
--  New columns: confidence_lower, confidence_upper,
--               horizon_weeks, mape
--  New index:   idx_forecast_item_warehouse
-- ============================================================