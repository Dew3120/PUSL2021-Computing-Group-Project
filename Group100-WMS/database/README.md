# Group100 WMS Database

Database name: `group100_wms`

## Setup Order

1. Run `database/migrations/V1_0_0__initial_schema.sql`.
2. Run `database/migrations/V1_1_0__add_forecast_confidence.sql` if your MySQL version/project branch uses confidence interval columns.
3. Run `database/migrations/V1_2_0__add_inter_warehouse_transfer.sql`.
4. Run `database/migrations/V1_3_0__align_live_completion_schema.sql`.
5. Import seed/demo data only in a local development database.

## Verified Local Database Snapshot

The evaluated local database contained:

| Table | Rows |
|---|---:|
| roles | 5 |
| users | 5 |
| employees | 100 |
| warehouses | 2 |
| suppliers | 4 |
| items | 800 |
| purchase_orders | 54 |
| goods_received_notes | 37 |
| grn_items | 136 |
| batches | 136 |
| goods_issue_notes | 68 |
| gin_items | 158 |
| attendance_records | 31,686 |
| leave_requests | 43 |
| payroll | 1,463 |
| forecasts | 10 |
| forecast_history | 315 |
| audit_logs | 174 |

Do not commit local production passwords or personally sensitive seed data to a public repository.
