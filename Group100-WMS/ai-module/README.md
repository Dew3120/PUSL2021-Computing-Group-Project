# AI Forecasting Module

This folder contains the lightweight forecasting helpers used by the Group100 WMS forecasting workflow.

The production JavaFX app now generates and saves forecasts through `ForecastService` using demand history from GIN transactions and `forecast_history`. These Python helpers are kept as a reproducible prototype layer for experimenting with preprocessing, moving-average/trend forecasting, forecast writing, and accuracy evaluation without requiring external packages.

## Run a quick local forecast

```powershell
python forecast.py 120 132 141 138 150
```

The script prints a JSON forecast with `predicted_qty`, `confidence`, and method metadata.

## Dependencies

No third-party Python dependency is required for the lightweight prototype. Python 3.10+ is recommended.
