"""Forecasting helpers for the Group100 WMS prototype layer."""

from __future__ import annotations

from math import sqrt
from statistics import mean


def clamp(minimum: float, maximum: float, value: float) -> float:
    return max(minimum, min(maximum, value))


def weighted_moving_average_forecast(series: list[int]) -> dict[str, float | int | str]:
    cleaned = [max(0, int(value)) for value in series]
    if not cleaned:
        return {"predicted_qty": 10, "confidence": 0.55, "method": "BASELINE_AVG"}

    weighted_total = sum(value * (index + 1) for index, value in enumerate(cleaned))
    weight_sum = sum(range(1, len(cleaned) + 1))
    weighted_average = weighted_total / weight_sum
    trend = (cleaned[-1] - cleaned[0]) / (len(cleaned) - 1) if len(cleaned) > 1 else 0
    predicted = max(1, round(weighted_average + (trend * 0.25)))

    avg = mean(cleaned)
    variance = mean([(value - avg) ** 2 for value in cleaned]) if cleaned else 0
    coefficient_of_variation = sqrt(variance) / avg if avg else 1
    confidence = clamp(0.55, 0.92, 0.62 + min(len(cleaned), 6) * 0.04 - coefficient_of_variation * 0.12)

    return {
        "predicted_qty": int(predicted),
        "confidence": round(confidence, 4),
        "method": "WMA_TREND" if len(cleaned) >= 3 else "BASELINE_AVG",
    }
