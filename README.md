# Centralized Apparel Warehouse Management System (WMS)
### **PUSL2021: Computing Group Project**
**Year 2, Semester 2 | Group 100**
**Batch:** 25/AY/AU/M | **Partner University:** Plymouth University

---

## Project Overview
The **Apparel Warehouse Management System** is a professional desktop-based solution developed to modernize the Sri Lankan garment sector's logistics. It replaces manual, paper-based workflows (like physical bin cards) with a centralized, real-time digital infrastructure. Designed specifically for small-to-medium factories, the system utilizes an **offline-first architecture** to ensure full operational reliability on a local server regardless of internet connectivity.



---

##  System Architecture & Tech Stack
* **Architecture Pattern:** Desktop Workstation Client-Server model operating over a Local Area Network (LAN).
* **Interface:** User-friendly, menu-driven graphical desktop interface.
* **Database:** Secure local database (MySQL/SQL Server) for centralized data storage and integrity.
* **Security:** Role-Based Access Control (RBAC), password hashing (bcrypt/SHA-256), and automated daily database backups.

---

##  Core Modules & Features

### 1. Inbound & Outbound Management
* **Inbound:** Digital registration of purchase orders and automated generation of Goods Received Notes (GRN) with technical material categorization.
* **Outbound:** Tracking of customer sales and inter-warehouse stock transfers with automated Goods Issue Note (GIN) generation.

### 2. Smart Inventory Control
* **Digital Bin Cards:** Replaces manual cards with real-time digital tracking across multiple warehouse locations.
* **FIFO Logic:** Automatically implements First-In-First-Out material issuing logic to optimize stock rotation and reduce waste.
* **Smart Alerts:** Real-time notifications for low stock levels and foreign currency price variations affecting procurement.

### 3. Labor & Payroll System
* **Biometric Integration:** Connects directly to fingerprint hardware to record worker attendance and eliminate manual logging errors.
* **Automated Payroll:** Calculates monthly salaries, overtime (OT), and absenteeism deductions based on verified biometric logs.

### 4. AI Forecasting & Insights
* **Demand Prediction:** Utilizes AI algorithms to forecast future material consumption based on historical sales and seasonal trends.
* **Market Monitoring:** Real-time insights into currency movements to assist in financial planning for raw material imports.

---

##  Group Members (Group 100)
| Name | Student Reference |
| :--- | :--- |
| **Thisara Gnanasena (Leader)** | 10967149 |
| Geekiyanage Fernando | 10967245 |
| Geekiyanage Fernando | 10967144 |
| Peduru Fernando | 10967146 |
| Warunika Kumarage | 10967172 |
| Mudalpath Mindula | 10967274 |
| Thena Silva | 10967070 |
| Yasuri Ukwattage | 10967216 |

**Module Leader:** Mr. Diluka Wijesinghe

---

## System Requirements
* **Operating Environment:** Windows-based desktop workstations.
* **Hardware:** Local server, LAN infrastructure, and compatible biometric fingerprint scanning devices.
* **Performance:** Interface load times under 4 seconds; payroll processing for 1,000+ employees in under 10 seconds.

---

**Academic Disclaimer:** This is an academic group project developed for the PUSL2021 module in partnership with Plymouth University.
