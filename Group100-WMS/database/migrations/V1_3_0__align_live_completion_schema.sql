-- ============================================================
--  Group 100 - Centralized Apparel WMS
--  Migration: V1_3_0 - Completion Schema Alignment
--  Description: Aligns repository SQL files with the completed
--               local database used for final project evaluation.
-- ============================================================

USE group100_wms;

ALTER TABLE users
    ADD COLUMN IF NOT EXISTS full_name VARCHAR(100) NULL AFTER username,
    ADD COLUMN IF NOT EXISTS phone VARCHAR(20) NULL AFTER full_name,
    ADD COLUMN IF NOT EXISTS email VARCHAR(100) NULL AFTER phone,
    ADD COLUMN IF NOT EXISTS bio TEXT NULL AFTER email,
    ADD COLUMN IF NOT EXISTS department VARCHAR(50) NULL AFTER bio,
    ADD COLUMN IF NOT EXISTS date_joined DATE NULL AFTER department,
    ADD COLUMN IF NOT EXISTS nic VARCHAR(15) NULL AFTER date_joined,
    ADD COLUMN IF NOT EXISTS emergency_contact VARCHAR(100) NULL AFTER nic,
    ADD COLUMN IF NOT EXISTS skills VARCHAR(255) NULL AFTER emergency_contact,
    ADD COLUMN IF NOT EXISTS availability VARCHAR(20) NULL DEFAULT 'ONLINE' AFTER skills,
    ADD COLUMN IF NOT EXISTS last_active DATETIME NULL AFTER availability;

ALTER TABLE employees
    MODIFY COLUMN designation VARCHAR(100) NOT NULL,
    ADD COLUMN IF NOT EXISTS section VARCHAR(10) NULL AFTER designation,
    ADD COLUMN IF NOT EXISTS bank_name VARCHAR(100) NULL AFTER is_active,
    ADD COLUMN IF NOT EXISTS bank_branch VARCHAR(100) NULL AFTER bank_name,
    ADD COLUMN IF NOT EXISTS account_number VARCHAR(20) NULL AFTER bank_branch,
    ADD COLUMN IF NOT EXISTS nic VARCHAR(15) NULL AFTER account_number,
    ADD COLUMN IF NOT EXISTS resignation_date DATE NULL AFTER nic,
    ADD COLUMN IF NOT EXISTS date_of_birth DATE NULL AFTER resignation_date,
    ADD COLUMN IF NOT EXISTS age INT NULL AFTER date_of_birth,
    ADD COLUMN IF NOT EXISTS gender VARCHAR(10) NULL AFTER age,
    ADD COLUMN IF NOT EXISTS marital_status VARCHAR(15) NULL AFTER gender,
    ADD COLUMN IF NOT EXISTS address VARCHAR(255) NULL AFTER marital_status,
    ADD COLUMN IF NOT EXISTS city VARCHAR(50) NULL AFTER address,
    ADD COLUMN IF NOT EXISTS phone VARCHAR(15) NULL AFTER city,
    ADD COLUMN IF NOT EXISTS email VARCHAR(100) NULL AFTER phone,
    ADD COLUMN IF NOT EXISTS emergency_contact_name VARCHAR(100) NULL AFTER email,
    ADD COLUMN IF NOT EXISTS emergency_contact_phone VARCHAR(15) NULL AFTER emergency_contact_name,
    ADD COLUMN IF NOT EXISTS blood_group VARCHAR(5) NULL AFTER emergency_contact_phone,
    ADD COLUMN IF NOT EXISTS joined_date DATE NULL AFTER blood_group;

ALTER TABLE attendance_records
    MODIFY COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ABSENT',
    ADD COLUMN IF NOT EXISTS notes VARCHAR(255) NULL AFTER approved_by;

CREATE TABLE IF NOT EXISTS leave_requests (
    request_id INT PRIMARY KEY AUTO_INCREMENT,
    employee_id INT NOT NULL,
    request_date DATE NOT NULL,
    leave_type VARCHAR(20) NOT NULL DEFAULT 'HALF_DAY',
    reason VARCHAR(255),
    status VARCHAR(15) NOT NULL DEFAULT 'PENDING',
    created_by INT NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    reviewed_at DATETIME NULL,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id),
    FOREIGN KEY (created_by) REFERENCES users(user_id),
    INDEX idx_leave_status (status),
    INDEX idx_leave_date (request_date)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS forecast_history (
    history_id INT PRIMARY KEY AUTO_INCREMENT,
    item_id INT NOT NULL,
    warehouse_id INT NOT NULL,
    forecast_month TINYINT NOT NULL,
    forecast_year SMALLINT NOT NULL,
    predicted_qty DECIMAL(10,2) NOT NULL,
    actual_qty DECIMAL(10,2) NULL,
    accuracy DECIMAL(5,2) NULL,
    result VARCHAR(10) NULL,
    confidence DECIMAL(5,4) NULL,
    method VARCHAR(50) DEFAULT 'ARIMA',
    generated_date DATE NOT NULL,
    FOREIGN KEY (item_id) REFERENCES items(item_id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(warehouse_id),
    INDEX idx_forecast_history_period (forecast_year, forecast_month),
    INDEX idx_forecast_history_result (result)
) ENGINE=InnoDB;
