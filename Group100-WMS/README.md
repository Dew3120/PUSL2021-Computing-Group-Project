[![Java](https://img.shields.io/badge/Java-17_LTS-orange)](https://openjdk.org/projects/jdk/17/)
[![JavaFX](https://img.shields.io/badge/JavaFX-17-purple)](https://openjfx.io/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue)](https://www.mysql.com/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-C71A36)](https://maven.apache.org/)
[![Python](https://img.shields.io/badge/Python-3.10-yellow)](https://www.python.org/)
[![Status](https://img.shields.io/badge/status-complete-brightgreen)]()

# Group 100 - Centralized Apparel Warehouse Management System

A centralized, offline-first, AI-enhanced desktop Warehouse Management System built for Sri Lankan apparel manufacturers. Replaces manual bin cards, paper attendance registers, and spreadsheet-based payroll with a fully digital, role-based platform.

## Team

| Name | Student ID | Role |
|------|-----------|------|
| Thisara Gnanasena | 10967149 | Project Lead and Documentation |
| Geekiyanage Fernando | 10967245 | Backend - Inventory / FIFO |
| Geekiyanage Fernando | 10967144 | Database Design and Schema |
| Peduru Fernando | 10967146 | Backend - Inbound / Outbound |
| Yasuri Ukwattage | 10967216 | UI/UX Design and Frontend |
| Thena Silva | 10967070 | AI Forecasting Module |
| Mudalpath Mindula | 10967274 | Testing and Quality Assurance |
| Warunika Kumarage | 10967172 | Labour Management and Payroll |

Module: PUSL2021 - Computing Group Project (25/AY/AU/M)
University: Plymouth University / NSBM Green University
Programmer: Mr. Diluka Wijesinghe

## System Overview

| Property | Detail |
|----------|--------|
| Type | Desktop application (LAN, offline-first) |
| Architecture | 3-tier Client-Server |
| Frontend | JavaFX 17 with FXML |
| Backend | Java 17 LTS |
| Database | MySQL 8.0 (16 tables, 3NF) |
| AI Module | Python 3.10 - ARIMA forecasting |
| Biometric | ZKTeco fingerprint SDK |
| Security | bcrypt, RBAC, audit logging, 15-min timeout |
| Reports | PDF (iText 7) and Excel (Apache POI 5.2) |

## User Roles

| Role | Access |
|------|--------|
| Admin | Full system access |
| Warehouse Manager | Inventory, Inbound, Outbound, Attendance |
| Supervisor | Inventory, Outbound, Attendance Validation |
| Accountant | Inventory, Inbound, Outbound, Attendance, Payroll, Reports |
| Senior Manager | Inventory, Outbound, AI Forecasting, Reports |

## Quick Start

Prerequisites: JDK 17 LTS, MySQL 8.0, Python 3.10

### Run the application
`powershell
C:\Users\Dev\Downloads\OpenJDK17U-jdk_x64_windows_hotspot_17.0.18_8.msi = "C:\Program Files\Eclipse Adoptium\jdk-17.0.18.8-hotspot"
.\mvnw.cmd javafx:run
`

### Login credentials (all use password123)
- admin / password123
- manager / password123
- supervisor / password123
- accountant / password123
- srmanager / password123

## Technology Stack

| Component | Technology | Version |
|-----------|-----------|---------|
| Frontend | JavaFX | 17 |
| Backend | Java | 17 LTS |
| Database | MySQL | 8.0 |
| AI/ML | Python + statsmodels | 3.10 / 0.14.1 |
| Connection Pool | HikariCP | 5.1.0 |
| Password Security | jBCrypt | 0.4 |
| PDF Export | iText | 7.2.5 |
| Excel Export | Apache POI | 5.2.5 |
| Biometric | ZKTeco SDK | 3.0 |
| Build Tool | Maven | 3.9.6 |
| CI/CD | GitHub Actions | - |

## Key Algorithms

FIFO Inventory Issuing - Batches queried by receipt_date ASC, deducted sequentially.
ARIMA Demand Forecasting - AIC grid search, 4-week forward forecast per SKU.
Payroll Calculation - Base salary x working days, overtime x1.5, EPF 8%/12%, ETF 3%.

## License

Academic use - PUSL2021 Computing Group Project, Plymouth University / NSBM Green University.
