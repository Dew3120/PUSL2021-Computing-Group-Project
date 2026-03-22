package com.group100.wms.core;

/**
 * App-wide constants — DB URL, ports, thresholds.
 * No instances allowed. All fields are public static final.
 *
 * OOP Concepts Used:
 * - Encapsulation: Configuration values are grouped in a single class for controlled access.
 * - Abstraction: Hides configuration details (DB, pool, payroll, etc.) from other parts of the system.
 * - No Inheritance or Polymorphism is used in this class.
 */
public final class AppConfig {

    // Private constructor to prevent instantiation of this utility class
    private AppConfig() {}

    // ── Database ──────────────────────────────────────────────────────────

    // Stores the database host address
    public static final String DB_HOST     = "127.0.0.1";

    // Stores the database port number
    public static final int    DB_PORT     = 3306;

    // Stores the database name
    public static final String DB_NAME     = "group100_wms";

    // Stores the full JDBC URL used to connect to the MySQL database
    public static final String DB_URL      = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT
            + "/" + DB_NAME
            + "?useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&serverTimezone=UTC";

    // Stores the database username
    public static final String DB_USER     = "root";

    // Stores the database password
    public static final String DB_PASSWORD = "root";

    // ── Connection pool ───────────────────────────────────────────────────

    // Maximum number of connections allowed in the pool
    public static final int  DB_POOL_MAX_SIZE    = 10;

    // Minimum number of idle connections maintained in the pool
    public static final int  DB_POOL_MIN_IDLE    = 2;

    // Time (in milliseconds) before an idle connection is removed
    public static final long DB_IDLE_TIMEOUT_MS  = 30_000L;

    // Maximum time (in milliseconds) to wait for a connection from the pool
    public static final long DB_CONN_TIMEOUT_MS  = 20_000L;

    // Maximum lifetime (in milliseconds) of a connection in the pool
    public static final long DB_MAX_LIFETIME_MS  = 600_000L;

    // ── Session ───────────────────────────────────────────────────────────

    // Stores session timeout duration in minutes
    public static final int SESSION_TIMEOUT_MINUTES = 15;

    // ── Inventory ─────────────────────────────────────────────────────────

    // Defines the stock level threshold to trigger low stock alerts
    public static final int LOW_STOCK_THRESHOLD = 20;

    // ── Payroll ───────────────────────────────────────────────────────────

    // Employee EPF (Employee Provident Fund) contribution rate
    public static final double EPF_EMPLOYEE_RATE = 0.08;

    // Employer EPF contribution rate
    public static final double EPF_EMPLOYER_RATE = 0.12;

    // ETF (Employee Trust Fund) contribution rate
    public static final double ETF_RATE          = 0.03;

    // Overtime pay rate multiplier
    public static final double OVERTIME_RATE     = 1.5;

    // ── Application ───────────────────────────────────────────────────────

    // Stores the application name
    public static final String APP_NAME    = "Group100 WMS";

    // Stores the application version
    public static final String APP_VERSION = "1.0.0";
}
