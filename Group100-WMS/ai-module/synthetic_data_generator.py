"""Synthetic demand data generator for local forecasting experiments."""

from __future__ import annotations

import random


def generate_monthly_demand(months: int = 12, start: int = 100, drift: int = 4) -> list[int]:
    value = start
    series: list[int] = []
    for _ in range(months):
        value = max(1, value + drift + random.randint(-12, 12))
        series.append(value)
    return series
