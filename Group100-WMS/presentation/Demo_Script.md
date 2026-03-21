# Demo Script - Group 100 Centralized Apparel WMS (10-15 minutes)

## Pre-Demo Checklist
- MySQL running with group100_wms database
- JAVA_HOME set to JDK 17
- App builds with mvnw.cmd javafx:run
- PDF and Excel viewers installed

## Demo Flow

### 1. Login and RBAC (2 min)
Login as admin / password123. Show all 8 sidebar buttons.
Talk about: bcrypt hashing, RBAC, session timeout, audit logging.

### 2. Admin Dashboard (1 min)
Show 4 KPI cards: Total Items 12, GRNs 2, GINs 3, Payroll count.

### 3. Inventory (1 min)
Click Inventory. Show 12 items with SKU, category, colour.
Talk about: FIFO issuing, real-time stock, multi-warehouse.

### 4. Inbound (1 min)
Click Inbound. Show 4 Purchase Orders and 2 GRNs.
Talk about: GRN generation, auto stock update.

### 5. Outbound + Shortage Detection (2 min)
Click Outbound. Show 3 GINs. Click New GIN.
Type Test Production, select Cotton Fabric Roll, enter 99999.
Click Add Item. Show red shortage warning and popup.

### 6. Attendance Validation (2 min)
Logout. Login as supervisor / password123.
Click Attendance. Show validation screen with colored rows.
Select a record. Click Approve PRESENT.

### 7. Payroll (1 min)
Logout. Login as accountant / password123.
Click Payroll. Select Month 2, Year 2026. Show 6 records.

### 8. Reports PDF and Excel (1 min)
Click Reports. Select Month 2, Year 2026.
Export Payroll PDF. Open and show the file.

### 9. AI Forecasting (1 min)
Logout. Login as srmanager / password123.
Click Forecasting. Show 10 ARIMA forecasts.

### 10. Admin - Users and Audit Log (1 min)
Logout. Login as admin. Click Admin.
Show 5 users. Click Create User. Click View Audit Log.

### 11. Session Timeout (mention only)
System auto-logs out after 15 minutes inactivity. Meets FR-AUTH-03.

## Fallback Plan
1. Check MySQL: mysql -u root -proot -e "USE group100_wms; SELECT COUNT(*) FROM users;"
2. Check JAVA_HOME: echo JAVA_HOME
3. Rebuild: mvnw.cmd clean javafx:run
4. Re-seed database if empty

## Q and A Talking Points
- Why not cloud? Sri Lankan SMEs have unreliable internet. Offline-first design.
- How does FIFO work? Batches by receipt_date ASC, oldest deducted first.
- Why ARIMA not LSTM? Less training data needed. 12.4% MAPE achieved.
- Security? bcrypt, RBAC, audit logs, failed login logging, 15-min timeout.
- Limitations? 5 documented: fingerprint hardware, currency rates, filterable reports, stock adjustment justification, system config screen.
