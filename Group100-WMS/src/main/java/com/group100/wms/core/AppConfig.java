package com.group100.wms.core;

public final class AppConfig {

    private AppConfig() {}

    public static final String DB_HOST = setting("wms.db.host", "WMS_DB_HOST", "127.0.0.1");
    public static final int DB_PORT = intSetting("wms.db.port", "WMS_DB_PORT", 3306);
    public static final String DB_NAME = setting("wms.db.name", "WMS_DB_NAME", "group100_wms");
    public static final String DB_URL = setting("wms.db.url", "WMS_DB_URL",
            "jdbc:mysql://" + DB_HOST + ":" + DB_PORT
                    + "/" + DB_NAME
                    + "?useSSL=false"
                    + "&allowPublicKeyRetrieval=true"
                    + "&serverTimezone=UTC");
    public static final String DB_USER = setting("wms.db.user", "WMS_DB_USER", "root");
    public static final String DB_PASSWORD = setting("wms.db.password", "WMS_DB_PASSWORD", "root");

    public static final int DB_POOL_MAX_SIZE = intSetting("wms.db.pool.max", "WMS_DB_POOL_MAX", 10);
    public static final int DB_POOL_MIN_IDLE = intSetting("wms.db.pool.minIdle", "WMS_DB_POOL_MIN_IDLE", 2);
    public static final long DB_IDLE_TIMEOUT_MS = longSetting("wms.db.idleTimeoutMs", "WMS_DB_IDLE_TIMEOUT_MS", 30_000L);
    public static final long DB_CONN_TIMEOUT_MS = longSetting("wms.db.connectionTimeoutMs", "WMS_DB_CONN_TIMEOUT_MS", 20_000L);
    public static final long DB_MAX_LIFETIME_MS = longSetting("wms.db.maxLifetimeMs", "WMS_DB_MAX_LIFETIME_MS", 600_000L);

    public static final int SESSION_TIMEOUT_MINUTES = intSetting("wms.session.timeoutMinutes", "WMS_SESSION_TIMEOUT_MINUTES", 15);
    public static final int LOW_STOCK_THRESHOLD = intSetting("wms.lowStockThreshold", "WMS_LOW_STOCK_THRESHOLD", 20);

    public static final double EPF_EMPLOYEE_RATE = doubleSetting("wms.epf.employeeRate", "WMS_EPF_EMPLOYEE_RATE", 0.08);
    public static final double EPF_EMPLOYER_RATE = doubleSetting("wms.epf.employerRate", "WMS_EPF_EMPLOYER_RATE", 0.12);
    public static final double ETF_RATE = doubleSetting("wms.etf.rate", "WMS_ETF_RATE", 0.03);
    public static final double OVERTIME_RATE = doubleSetting("wms.overtime.rate", "WMS_OVERTIME_RATE", 1.5);

    public static final String APP_NAME = "Group100 WMS";
    public static final String APP_VERSION = "1.0.0";

    private static String setting(String propertyName, String envName, String fallback) {
        String propertyValue = System.getProperty(propertyName);
        if (propertyValue != null && !propertyValue.isBlank()) return propertyValue;
        String envValue = System.getenv(envName);
        if (envValue != null && !envValue.isBlank()) return envValue;
        return fallback;
    }

    private static int intSetting(String propertyName, String envName, int fallback) {
        try {
            return Integer.parseInt(setting(propertyName, envName, String.valueOf(fallback)));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static long longSetting(String propertyName, String envName, long fallback) {
        try {
            return Long.parseLong(setting(propertyName, envName, String.valueOf(fallback)));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }

    private static double doubleSetting(String propertyName, String envName, double fallback) {
        try {
            return Double.parseDouble(setting(propertyName, envName, String.valueOf(fallback)));
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
