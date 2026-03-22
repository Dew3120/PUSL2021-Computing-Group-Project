package com.group100.wms.core;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * HikariCP connection pool singleton.
 * Call initialise() once at startup from MainApp.
 * Always use try-with-resources when calling getConnection().
 *
 * OOP Concepts Used:
 * - Encapsulation: The dataSource is private and accessed through public methods.
 * - Abstraction: Hides complex database connection pooling logic from the rest of the application.
 * - Singleton Pattern (Design Concept): Ensures only one instance of the connection pool exists.
 */
public final class DatabaseConnection {

    // Stores the HikariCP DataSource (connection pool instance)
    private static HikariDataSource dataSource;

    // Private constructor to prevent object instantiation (Singleton pattern)
    private DatabaseConnection() {}

    // Initializes the database connection pool using configuration values
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

    // Provides a database connection from the pool
    public static Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new IllegalStateException(
                    "[DB] Pool not initialised. Call DatabaseConnection.initialise() first."
            );
        }
        return dataSource.getConnection();
    }

    // Closes the connection pool and releases all database resources
    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("[DB] Connection pool shut down.");
        }
    }
}
