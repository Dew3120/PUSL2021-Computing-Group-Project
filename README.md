[![Java](https://img.shields.io/badge/Java-17_LTS-orange)](https://openjdk.org/projects/jdk/17/)
[![JavaFX](https://img.shields.io/badge/JavaFX-17-purple)](https://openjfx.io/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36)](https://maven.apache.org/)
[![Python](https://img.shields.io/badge/Python-3.10-yellow)](https://www.python.org/)
[![Status](https://img.shields.io/badge/status-complete-brightgreen)]()

# Centralized Apparel Warehouse Management System

> PUSL2021 Computing Group Project | Group 100 | Year 2, Semester 2 (Batch 25/AY/AU/M)

A centralized, offline-first, AI-enhanced desktop Warehouse Management System built for Sri Lankan apparel manufacturers. Replaces manual bin cards, paper attendance registers, and spreadsheet-based payroll with a fully digital, role-based platform.

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Frontend | JavaFX 17 with FXML |
| Backend | Java 17 LTS |
| Database | MySQL 8.0 (16 tables, 3NF) |
| AI Module | Python 3.10 - ARIMA Forecasting |
| Biometric | ZKTeco Fingerprint SDK |
| Security | bcrypt, RBAC, audit logging |
| Reports | PDF (iText 7) & Excel (Apache POI) |
| Build | Maven 3.9+ |
| CI/CD | GitHub Actions |

## Key Features

- **Inventory Management** - FIFO-based stock issuing with real-time tracking
- **Inbound/Outbound** - Full goods receipt and dispatch workflow
- **AI Demand Forecasting** - ARIMA model with 4-week forward predictions per SKU
- **Biometric Attendance** - ZKTeco fingerprint integration for workforce tracking
- **Payroll Processing** - Automated salary calculation with EPF/ETF deductions
- **Role-Based Access** - Admin, Warehouse Manager, Supervisor, Accountant, Senior Manager
- **Reporting** - PDF and Excel export for all business data

## Quick Start

**Prerequisites:** JDK 17 LTS, MySQL 8.0, Python 3.10

```bash
cd Group100-WMS
./mvnw javafx:run
```

## Team

| Name | Role |
|------|------|
| Thisara Gnanasena | Project Lead & Documentation |
| Geekiyanage Fernando | Backend - Inventory / FIFO |
| Peduru Fernando | Backend - Inbound / Outbound |
| Yasuri Ukwattage | UI/UX Design & Frontend |
| Thena Silva | AI Forecasting Module |
| Mudalpath Mindula | Testing & QA |
| Warunika Kumarage | Labour Management & Payroll |

## License

Academic use - Plymouth University / NSBM Green University
