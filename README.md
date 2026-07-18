<div align="center">

# Centralized Apparel Warehouse Management System

**A desktop Warehouse Management System for apparel manufacturing operations, built with JavaFX, MySQL, and a layered Java backend.**

[![Java](https://img.shields.io/badge/Java-17_LTS-orange)](https://openjdk.org/projects/jdk/17/)
[![JavaFX](https://img.shields.io/badge/JavaFX-17.0.2-purple)](https://openjfx.io/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-Wrapper-C71A36)](https://maven.apache.org/)
[![JUnit](https://img.shields.io/badge/Tests-JUnit_5-brightgreen)](https://junit.org/junit5/)
[![Status](https://img.shields.io/badge/Status-Portfolio_Ready-brightgreen)]()

PUSL2021 Computing Group Project - Group 100

</div>

## Overview

This project is a database-backed desktop WMS designed around the day-to-day operations of an apparel warehouse: receiving goods, tracking stock by batches, issuing inventory with FIFO logic, handling inter-warehouse transfers, compiling attendance, processing payroll, exporting reports, and reviewing demand forecasts.

The system was built as an academic software engineering project, but the implementation follows a production-style structure with dedicated model, repository, service, UI, utility, and database layers. It is suitable for demonstrating Java desktop development, SQL-backed business workflows, role-based access control, reporting, and end-to-end warehouse process modelling.

## Project Highlights

| Area | Details |
|---|---|
| Application type | JavaFX desktop application |
| Architecture | Layered MVC-style structure with `model`, `repository`, `service`, `ui`, and `util` packages |
| Database | MySQL schema with 18 operational tables |
| UI scale | 38 FXML screens covering dashboards, inventory, inbound, outbound, attendance, payroll, reports, and admin |
| Backend scale | 16 repositories and 10 service classes |
| Security | Role-based navigation, bcrypt password hashing, session handling, and audit logging |
| Reporting | PDF export with iText and Excel export with Apache POI |
| Verification | Maven/JUnit test suite passes locally |

## Core Features

### Inventory And Warehouse Operations

- Item, supplier, warehouse, and batch management
- FIFO stock deduction using batch availability
- Low-stock monitoring by warehouse
- Stock adjustment screens and warehouse-level inventory views
- Inter-warehouse transfer workflow

### Inbound And Outbound Workflows

- Purchase Order creation and tracking
- Goods Received Note workflow
- Goods Issue Note workflow
- Persisted FIFO batch allocations for issued goods
- Status dashboards and exportable operational tables

### Workforce And Payroll

- Employee directory and profile screens
- Attendance compilation and validation workflow
- Leave request management
- Payroll generation with EPF, ETF, overtime, gross salary, deductions, and net salary calculations
- Attendance and payroll report exports

### Forecasting And Reporting

- Forecast dashboard using existing transaction and forecast history data
- Lightweight Java-based demand forecast generation
- Forecast history support through the database layer
- Central report centre with PDF and Excel export options

### Administration

- Login and authentication state handling
- Role-based navigation for Admin, Warehouse Manager, Supervisor, Accountant, and Senior Manager
- User management
- Audit log review
- Centralized configuration for database and runtime settings

## Tech Stack

| Layer | Technology |
|---|---|
| Desktop UI | JavaFX 17, FXML, CSS |
| Application logic | Java 17 |
| Database | MySQL 8.0 |
| Persistence | JDBC repositories with HikariCP connection pooling |
| Security | jBCrypt password hashing |
| Reports | iText 7 for PDF, Apache POI for Excel |
| Testing | JUnit 5, Mockito |
| Build | Maven Wrapper |
| Optional helpers | Python forecasting prototype scripts |

## Verified Data Snapshot

The completed local verification database contains seeded operational data for realistic testing:

| Data area | Records |
|---|---:|
| Roles | 5 |
| Users | 5 |
| Employees | 100 |
| Warehouses | 2 |
| Suppliers | 4 |
| Inventory items | 800 |
| Purchase orders | 54 |
| Goods received notes | 37 |
| GRN line items | 136 |
| Batches | 136 |
| Goods issue notes | 68 |
| GIN line items | 158 |
| Attendance records | 31,686 |
| Leave requests | 43 |
| Payroll records | 1,463 |
| Forecasts | 10 |
| Forecast history records | 315 |
| Audit logs | 174 |

## Project Structure

```text
.
|-- Group100-WMS
|   |-- src/main/java/com/group100/wms
|   |   |-- core          # configuration, DB connection, session, audit logging
|   |   |-- model         # domain objects
|   |   |-- repository    # MySQL data access layer
|   |   |-- service       # business workflows and calculations
|   |   |-- ui            # JavaFX controllers
|   |   `-- util          # validation, password hashing, PDF/Excel helpers
|   |-- src/main/resources
|   |   |-- fxml          # JavaFX screen layouts
|   |   |-- css           # application styling
|   |   `-- images        # UI assets
|   |-- database
|   |   |-- migrations    # database setup/update scripts
|   |   |-- schema        # individual table scripts
|   |   `-- seed          # demo seed data
|   |-- ai-module         # optional Python forecasting helper scripts
|   `-- docs              # completion and portfolio notes
`-- README.md
```

## Getting Started

### Prerequisites

- JDK 17
- MySQL 8.0
- Git
- Maven is not required globally because the Maven Wrapper is included
- Python 3.10+ only if you want to run the optional forecasting helper scripts

### 1. Clone The Repository

```bash
git clone https://github.com/Dew3120/PUSL2021-Computing-Group-Project.git
cd PUSL2021-Computing-Group-Project/Group100-WMS
```

### 2. Create The Database

```sql
CREATE DATABASE IF NOT EXISTS group100_wms;
```

Run the database scripts in this order:

```text
database/migrations/V1_0_0__initial_schema.sql
database/migrations/V1_1_0__add_forecast_confidence.sql
database/migrations/V1_2_0__add_inter_warehouse_transfer.sql
database/migrations/V1_3_0__align_live_completion_schema.sql
```

Optional seed data is available in:

```text
database/seed/
```

### 3. Configure Database Credentials

The app defaults to:

```text
host: 127.0.0.1
port: 3306
database: group100_wms
user: root
password: root
```

You can override these values with environment variables.

PowerShell:

```powershell
$env:WMS_DB_HOST="127.0.0.1"
$env:WMS_DB_PORT="3306"
$env:WMS_DB_NAME="group100_wms"
$env:WMS_DB_USER="root"
$env:WMS_DB_PASSWORD="your_password"
```

Bash:

```bash
export WMS_DB_HOST="127.0.0.1"
export WMS_DB_PORT="3306"
export WMS_DB_NAME="group100_wms"
export WMS_DB_USER="root"
export WMS_DB_PASSWORD="your_password"
```

### 4. Run The Application

Windows:

```powershell
.\mvnw.cmd javafx:run
```

macOS/Linux:

```bash
./mvnw javafx:run
```

### 5. Run Tests

Windows:

```powershell
.\mvnw.cmd clean test
```

macOS/Linux:

```bash
./mvnw clean test
```

## Architecture

The project uses a layered structure to keep UI, business logic, and persistence responsibilities separate.

```text
FXML Views
   |
JavaFX Controllers
   |
Service Layer
   |
Repository Layer
   |
MySQL Database
```

This structure makes the system easier to test, extend, and explain as a portfolio project. Controllers handle user interaction, services coordinate business workflows, repositories isolate SQL access, and models represent warehouse domain objects.

## Verification Status

- Maven test suite: passing
- Java source conflict/fence scan: clean
- Database schema aligned with verified local MySQL tables
- Biometric hardware integration: future scope, not included in the completed verification score

## Scope Note

Hardware biometric device integration was part of the wider project concept, but it is intentionally treated as future scope in this repository. The completed implementation focuses on the JavaFX desktop WMS, MySQL-backed workflows, inventory control, attendance/payroll, reporting, audit logging, and forecasting dashboard support.

## Portfolio Summary

Centralized Apparel Warehouse Management System is a full-featured JavaFX and MySQL desktop application for apparel warehouse operations. It includes role-based access, FIFO inventory control, inbound and outbound workflows, inter-warehouse transfers, attendance validation, payroll generation, audit logging, PDF/Excel reporting, and forecasting dashboard support across 38 UI screens and an 18-table operational database.

## License

Academic project for University of Plymouth / NSBM Green University coursework.
