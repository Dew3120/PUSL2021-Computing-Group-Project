# Centralized Apparel Warehouse Management System

A JavaFX + MySQL desktop Warehouse Management System built for apparel warehouse operations as part of the PUSL2021 Computing Group Project. The system supports role-based access, inventory control, inbound and outbound stock workflows, attendance, leave requests, payroll, reporting, audit logs, and demand forecasting dashboards.

## Tech Stack

- Java 17
- JavaFX 17 with FXML
- MySQL 8.0
- Maven Wrapper
- HikariCP connection pooling
- jBCrypt password hashing
- iText PDF export
- Apache POI Excel export
- Lightweight Python forecasting prototype helpers

## Core Features

- Login and role-based access control for Admin, Warehouse Manager, Supervisor, Accountant, and Senior Manager roles
- Inventory management with item, warehouse, supplier, and batch records
- FIFO stock deduction for outbound goods issue workflows
- Purchase Order and Goods Received Note workflows
- Goods Issue Note workflow with persisted FIFO batch allocations
- Inter-warehouse stock transfer workflow
- Employee directory and employee profile information
- Attendance compilation, validation, leave requests, and reports
- Payroll generation with EPF, ETF, overtime, and net salary calculations
- PDF and Excel report exports
- Audit logging for important system actions
- Forecast dashboard and demand forecast generation from transaction/history data

## Verified Local Database Snapshot

The completed local database used for final verification contains 18 tables and seeded operational data:

| Data area | Count |
|---|---:|
| Employees | 100 |
| Inventory items | 800 |
| Purchase orders | 54 |
| GRNs | 37 |
| GINs | 68 |
| Attendance records | 31,686 |
| Leave requests | 43 |
| Payroll records | 1,463 |
| Forecast history records | 315 |
| Audit logs | 174 |

## Project Structure

```text
src/main/java/com/group100/wms
|-- core          # config, DB connection, session, audit logging
|-- model         # domain models
|-- repository    # MySQL data access
|-- service       # business workflows
|-- ui            # JavaFX controllers
`-- util          # validation, hashing, PDF/Excel export

src/main/resources
|-- fxml          # JavaFX screens
|-- css           # UI styles
`-- images        # application assets

database
|-- migrations    # setup/update scripts
`-- schema        # individual table scripts

ai-module         # lightweight forecasting prototype helpers
docs              # completion/status notes
```
## Setup

Prerequisites:

- JDK 17
- MySQL 8.0
- Maven Wrapper included in the project
- Python 3.10+ only if running the optional AI helper scripts

Create and prepare the database:

```sql
CREATE DATABASE IF NOT EXISTS group100_wms;
```

Run the SQL scripts in this order:

```text
database/migrations/V1_0_0__initial_schema.sql
database/migrations/V1_2_0__add_inter_warehouse_transfer.sql
database/migrations/V1_3_0__align_live_completion_schema.sql
```

Configure database credentials using environment variables if your local credentials are not the defaults:

```powershell
$env:WMS_DB_HOST="127.0.0.1"
$env:WMS_DB_PORT="3306"
$env:WMS_DB_NAME="group100_wms"
$env:WMS_DB_USER="root"
$env:WMS_DB_PASSWORD="root"
```

Run the app:

```powershell
.\mvnw.cmd javafx:run
```

Run verification:

```powershell
.\mvnw.cmd clean test
```

## Current Verification

- Clean Maven compile: passed
- Unit tests: passed
- DB integration test shells: present but disabled by default because they require a seeded local MySQL database
- Live local database checked separately through MySQL Workbench/CLI

## Scope Note

Hardware biometric device integration is treated as future scope. The current completed app focuses on the JavaFX desktop WMS, database-backed warehouse workflows, payroll/attendance, reporting, and forecasting dashboard/workflow.

## Portfolio Summary

Database-backed JavaFX desktop WMS with 18 MySQL tables, 800 item records, 100 employee records, 31k+ attendance records, payroll, FIFO inventory, inbound/outbound stock workflows, audit logging, PDF/Excel exports, and forecasting history/dashboard support.