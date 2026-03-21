package com.group100.wms.core;

/**
 * App-wide constants — DB URL, ports, thresholds.
 * No instances allowed. All fields are public static final.
 */
public final class AppConfig {

    private AppConfig() {}

    // ── Database ──────────────────────────────────────────────────────────
    public static final String DB_HOST     = "127.0.0.1";
    public static final int    DB_PORT     = 3306;
    public static final String DB_NAME     = "group100_wms";
    public static final String DB_URL      = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT
            + "/" + DB_NAME
            + "?useSSL=false"
            + "&allowPublicKeyRetrieval=true"
            + "&serverTimezone=UTC";
    public static final String DB_USER     = "root";
    public static final String DB_PASSWORD = "root";

    // ── Connection pool ───────────────────────────────────────────────────
    public static final int  DB_POOL_MAX_SIZE    = 10;
    public static final int  DB_POOL_MIN_IDLE    = 2;
    public static final long DB_IDLE_TIMEOUT_MS  = 30_000L;
    public static final long DB_CONN_TIMEOUT_MS  = 20_000L;
    public static final long DB_MAX_LIFETIME_MS  = 600_000L;

    // ── Session ───────────────────────────────────────────────────────────
    public static final int SESSION_TIMEOUT_MINUTES = 15;

    // ── Inventory ─────────────────────────────────────────────────────────
    public static final int LOW_STOCK_THRESHOLD = 20;

    // ── Payroll ───────────────────────────────────────────────────────────
    public static final double EPF_EMPLOYEE_RATE = 0.08;
    public static final double EPF_EMPLOYER_RATE = 0.12;
    public static final double ETF_RATE          = 0.03;
    public static final double OVERTIME_RATE     = 1.5;

    // ── Application ───────────────────────────────────────────────────────
    public static final String APP_NAME    = "Group100 WMS";
    public static final String APP_VERSION = "1.0.0";
}
