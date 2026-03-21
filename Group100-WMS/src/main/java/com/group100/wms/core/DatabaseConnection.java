package com.group100.wms.core;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * HikariCP connection pool singleton.
 * Call initialise() once at startup from MainApp.
 * Always use try-with-resources when calling getConnection().
 */
public final class DatabaseConnection {

    private static HikariDataSource dataSource;

    private DatabaseConnection() {}

    public static void initialise() {
        if (dataSource != null && !dataSource.isClosed()) {
            return;
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(AppConfig.DB_URL);
        config.setUsername(AppConfig.DB_USER);
        config.setPassword(AppConfig.DB_PASSWORD);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");

        config.setMaximumPoolSize(AppConfig.DB_POOL_MAX_SIZE);
        config.setMinimumIdle(AppConfig.DB_POOL_MIN_IDLE);
        config.setIdleTimeout(AppConfig.DB_IDLE_TIMEOUT_MS);
        config.setConnectionTimeout(AppConfig.DB_CONN_TIMEOUT_MS);
        config.setMaxLifetime(AppConfig.DB_MAX_LIFETIME_MS);
        config.setPoolName("WMS-HikariPool");
        config.setConnectionTestQuery("SELECT 1");

        dataSource = new HikariDataSource(config);
        System.out.println("[DB] Connection pool initialised successfully.");
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new IllegalStateException(
                    "[DB] Pool not initialised. Call DatabaseConnection.initialise() first."
            );
        }
        return dataSource.getConnection();
    }

    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("[DB] Connection pool shut down.");
        }
    }
}