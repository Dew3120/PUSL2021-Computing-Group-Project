"""Command-line forecasting entry point.

Usage:
    python forecast.py 120 132 141 138 150
"""

from __future__ import annotations

import json
import sys
from forecasting_service import weighted_moving_average_forecast


def parse_series(args: list[str]) -> list[int]:
    values: list[int] = []
    for arg in args:
        try:
            values.append(int(float(arg)))
        except ValueError:
            continue
    return values


def main() -> int:
    series = parse_series(sys.argv[1:])
    result = weighted_moving_average_forecast(series)
    print(json.dumps(result, indent=2))
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
