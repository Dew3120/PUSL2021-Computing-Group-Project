"""Forecast evaluation helpers."""

from __future__ import annotations


def mape(actual: list[float], predicted: list[float]) -> float:
    pairs = [(a, p) for a, p in zip(actual, predicted) if a]
    if not pairs:
        return 0.0
    return sum(abs((a - p) / a) for a, p in pairs) / len(pairs) * 100


def classify_accuracy(accuracy: float) -> str:
    if accuracy >= 85:
        return "HIT"
    if accuracy >= 70:
        return "FAIR"
    return "MISS"
