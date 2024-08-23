package com.maxim.currencyexchanger.Utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DatabaseConnectionPool {

    private static final HikariDataSource dataSource;

    static {
        HikariConfig config = new HikariConfig();
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        config.setJdbcUrl("jdbc:sqlite::resource:database.db");
        config.setUsername("");
        config.setPassword("");
        config.setMaximumPoolSize(100);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(15000);

        dataSource = new HikariDataSource(config);

    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}