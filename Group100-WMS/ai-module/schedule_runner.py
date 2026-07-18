"""Simple scheduler entry point for manual monthly forecast runs."""

from __future__ import annotations

from forecasting_service import weighted_moving_average_forecast


def run_once(series: list[int]) -> dict[str, float | int | str]:
    return weighted_moving_average_forecast(series)
