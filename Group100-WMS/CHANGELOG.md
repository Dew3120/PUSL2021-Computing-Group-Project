# Changelog - Group 100 Centralized Apparel WMS

## [1.0.0] - 2026-03-19 (Final Release)

### Added
- Session timeout (15-minute inactivity auto-logout)
- Failed login attempt logging to audit_logs
- Audit Log viewer screen (Admin - View Audit Log)
- Supervisor Attendance Validation screen with approve/reject
- Color-coded attendance rows (red=ABSENT, yellow=HALF_DAY)
- Auto shortage detection before GIN creation
- New GIN button on Goods Issue Notes list
- Create User button on User Management screen
- View Audit Log button on User Management screen
- Back to User List button on User Form
- Role name display in User Form dropdown

### Fixed
- Dashboard now loads correctly on login (was blank)
- Warehouse Manager dashboard low stock fixed
- Supervisor dashboard low stock fixed
- OT hours formatted to 2 decimal places
- AuditLogRepository column name fix (log_id)

### Verified
- Reports PDF and Excel export working
- GRN List loading correctly
- Low Stock Alert screen working
- All 5 dashboard KPIs working

## [0.9.0] - 2026-03-18 (Pre-Release)

### Added
- All 5 role-specific dashboards
- Report Centre with PDF and Excel export
- RBAC sidebar with correct button visibility
- All 8 modules loading real data

## [0.8.0] - 2026-02-19 (Interim Submission)

### Added
- Authentication with bcrypt
- RBAC for 5 user roles
- Core Inventory with FIFO logic
- Inbound module (partial)
- AI Forecasting with ARIMA
- MySQL database (16 tables, 3NF)

## [0.5.0] - 2026-01-20 (Functional Spec Submission)

### Added
- Complete functional and technical specification
- UML diagrams (Use Case, Class, ER, Architecture, Network)
- 35 functional requirements documented

## [0.1.0] - 2025-10-31 (Proposal Submission)

### Added
- Project proposal with problem statement
- Requirements analysis
- Budget schedule (LKR 17,500)
- 13-week project timeline
