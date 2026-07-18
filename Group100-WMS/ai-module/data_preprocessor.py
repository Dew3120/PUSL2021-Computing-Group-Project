"""Small preprocessing helpers for forecast input series."""

from __future__ import annotations


def clean_monthly_series(values: list[int | float | None]) -> list[int]:
    cleaned: list[int] = []
    for value in values:
        if value is None:
            continue
        cleaned.append(max(0, int(round(float(value)))))
    return cleaned
