-- ============================================================
--  Group 100 — Centralized Apparel WMS
--  Migration: V1_0_0 — Initial Schema
--  Date: 2025-12-06 (Sprint 1)
--  Description: Creates all 16 core tables for the WMS
-- ============================================================

CREATE DATABASE IF NOT EXISTS group100_wms
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_general_ci;

USE group100_wms;

-- ── 1. Roles ────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS roles (
                                     role_id   INT PRIMARY KEY AUTO_INCREMENT,
                                     role_name VARCHAR(50) NOT NULL UNIQUE
    ) ENGINE=InnoDB;

-- ── 2. Users ────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
                                     user_id       INT PRIMARY KEY AUTO_INCREMENT,
                                     username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role_id       INT NOT NULL,
    is_active     TINYINT(1) DEFAULT 1,
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(role_id)
    ) ENGINE=InnoDB;

-- ── 3. Employees ────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS employees (
                                         employee_id INT PRIMARY KEY AUTO_INCREMENT,
                                         user_id     INT NOT NULL,
                                         full_name   VARCHAR(100) NOT NULL,
    designation VARCHAR(50),
    daily_rate  DECIMAL(10,2) NOT NULL,
    is_active   TINYINT(1) DEFAULT 1,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
    ) ENGINE=InnoDB;

-- ── 4. Warehouses ───────────────────────────────────────────
CREATE TABLE IF NOT EXISTS warehouses (
                                          warehouse_id INT PRIMARY KEY AUTO_INCREMENT,
                                          name         VARCHAR(100) NOT NULL,
    location     VARCHAR(200)
    ) ENGINE=InnoDB;

-- ── 5. Suppliers ────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS suppliers (
                                         supplier_id INT PRIMARY KEY AUTO_INCREMENT,
                                         name        VARCHAR(100) NOT NULL,
    contact     VARCHAR(50),
    address     VARCHAR(200),
    email       VARCHAR(100)
    ) ENGINE=InnoDB;

-- ── 6. Items ────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS items (
                                     item_id      INT PRIMARY KEY AUTO_INCREMENT,
                                     sku          VARCHAR(20)  NOT NULL UNIQUE,
    name         VARCHAR(100) NOT NULL,
    description  VARCHAR(255),
    category     VARCHAR(50),
    colour       VARCHAR(30),
    unit         VARCHAR(10),
    warehouse_id INT,
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(warehouse_id)
    ) ENGINE=InnoDB;

-- ── 7. Purchase Orders ──────────────────────────────────────
CREATE TABLE IF NOT EXISTS purchase_orders (
                                               po_id        INT PRIMARY KEY AUTO_INCREMENT,
                                               supplier_id  INT NOT NULL,
                                               warehouse_id INT NOT NULL,
                                               order_date   DATE NOT NULL,
                                               expected_date DATE,
                                               status       VARCHAR(20) DEFAULT 'PENDING',
    FOREIGN KEY (supplier_id)  REFERENCES suppliers(supplier_id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(warehouse_id)
    ) ENGINE=InnoDB;

-- ── 8. Batches ──────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS batches (
                                       batch_id      INT PRIMARY KEY AUTO_INCREMENT,
                                       po_id         INT NOT NULL,
                                       item_id       INT NOT NULL,
                                       quantity      INT NOT NULL,
                                       available_qty INT NOT NULL,
                                       unit_cost     DECIMAL(10,2),
    receipt_date  DATE,
    FOREIGN KEY (po_id)   REFERENCES purchase_orders(po_id),
    FOREIGN KEY (item_id) REFERENCES items(item_id)
    ) ENGINE=InnoDB;

-- ── 9. Goods Received Notes ─────────────────────────────────
CREATE TABLE IF NOT EXISTS goods_received_notes (
                                                    grn_id       INT PRIMARY KEY AUTO_INCREMENT,
                                                    po_id        INT NOT NULL,
                                                    warehouse_id INT NOT NULL,
                                                    supplier_id  INT NOT NULL,
                                                    receipt_date DATE NOT NULL,
                                                    status       VARCHAR(20) DEFAULT 'PENDING',
    received_by  INT,
    FOREIGN KEY (po_id)        REFERENCES purchase_orders(po_id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(warehouse_id),
    FOREIGN KEY (supplier_id)  REFERENCES suppliers(supplier_id),
    FOREIGN KEY (received_by)  REFERENCES users(user_id)
    ) ENGINE=InnoDB;

-- ── 10. GRN Items ───────────────────────────────────────────
CREATE TABLE IF NOT EXISTS grn_items (
                                         grn_item_id INT PRIMARY KEY AUTO_INCREMENT,
                                         grn_id      INT NOT NULL,
                                         batch_id    INT NOT NULL,
                                         item_id     INT NOT NULL,
                                         quantity    INT NOT NULL,
                                         unit_cost   DECIMAL(10,2),
    FOREIGN KEY (grn_id)  REFERENCES goods_received_notes(grn_id),
    FOREIGN KEY (batch_id) REFERENCES batches(batch_id),
    FOREIGN KEY (item_id) REFERENCES items(item_id)
    ) ENGINE=InnoDB;

-- ── 11. Goods Issue Notes ───────────────────────────────────
CREATE TABLE IF NOT EXISTS goods_issue_notes (
                                                 gin_id           INT PRIMARY KEY AUTO_INCREMENT,
                                                 warehouse_id     INT NOT NULL,
                                                 destination      VARCHAR(100),
    destination_type VARCHAR(20),
    issued_by        INT,
    issued_date      DATE,
    status           VARCHAR(20) DEFAULT 'PENDING',
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(warehouse_id),
    FOREIGN KEY (issued_by)    REFERENCES users(user_id)
    ) ENGINE=InnoDB;

-- ── 12. GIN Items ───────────────────────────────────────────
CREATE TABLE IF NOT EXISTS gin_items (
                                         gin_item_id INT PRIMARY KEY AUTO_INCREMENT,
                                         gin_id      INT NOT NULL,
                                         item_id     INT NOT NULL,
                                         batch_id    INT NOT NULL,
                                         quantity    INT NOT NULL,
                                         FOREIGN KEY (gin_id)   REFERENCES goods_issue_notes(gin_id),
    FOREIGN KEY (item_id)  REFERENCES items(item_id),
    FOREIGN KEY (batch_id) REFERENCES batches(batch_id)
    ) ENGINE=InnoDB;

-- ── 13. Attendance Records ──────────────────────────────────
CREATE TABLE IF NOT EXISTS attendance_records (
                                                  attendance_id INT PRIMARY KEY AUTO_INCREMENT,
                                                  employee_id   INT NOT NULL,
                                                  date          DATE NOT NULL,
                                                  clock_in      TIME,
                                                  clock_out     TIME,
                                                  status        VARCHAR(20) DEFAULT 'PRESENT',
    approved_by   INT,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id),
    FOREIGN KEY (approved_by) REFERENCES users(user_id)
    ) ENGINE=InnoDB;

-- ── 14. Payroll ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS payroll (
                                       payroll_id     INT PRIMARY KEY AUTO_INCREMENT,
                                       employee_id    INT NOT NULL,
                                       month          INT NOT NULL,
                                       year           INT NOT NULL,
                                       base_salary    DECIMAL(12,2),
    overtime       DECIMAL(12,2) DEFAULT 0.00,
    deductions     DECIMAL(12,2) DEFAULT 0.00,
    epf_employer   DECIMAL(12,2) DEFAULT 0.00,
    etf            DECIMAL(12,2) DEFAULT 0.00,
    net_salary     DECIMAL(12,2),
    generated_by   INT,
    generated_date DATE,
    FOREIGN KEY (employee_id) REFERENCES employees(employee_id),
    FOREIGN KEY (generated_by) REFERENCES users(user_id)
    ) ENGINE=InnoDB;

-- ── 15. Forecasts ───────────────────────────────────────────
CREATE TABLE IF NOT EXISTS forecasts (
                                         forecast_id    INT PRIMARY KEY AUTO_INCREMENT,
                                         item_id        INT NOT NULL,
                                         warehouse_id   INT NOT NULL,
                                         predicted_qty  DECIMAL(10,2),
    confidence     DECIMAL(5,4),
    generated_date DATE,
    method         VARCHAR(20) DEFAULT 'ARIMA',
    FOREIGN KEY (item_id)      REFERENCES items(item_id),
    FOREIGN KEY (warehouse_id) REFERENCES warehouses(warehouse_id)
    ) ENGINE=InnoDB;

-- ── 16. Audit Logs ──────────────────────────────────────────
CREATE TABLE IF NOT EXISTS audit_logs (
                                          log_id     BIGINT PRIMARY KEY AUTO_INCREMENT,
                                          user_id    INT,
                                          action     VARCHAR(255) NOT NULL,
    table_name VARCHAR(50),
    record_id  INT,
    timestamp  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    details    TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_audit_user (user_id),
    INDEX idx_audit_table (table_name),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
    ) ENGINE=InnoDB;

-- ============================================================
--  Initial schema complete — 16 tables created
--  Run seed scripts next: database/seed/
-- ============================================================