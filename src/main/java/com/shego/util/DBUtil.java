package com.shego.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBUtil {
    private DBUtil() {
    }

    private static String getConfig(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value != null && !value.trim().isEmpty()) {
            return value;
        }
        String envKey = key.toUpperCase().replace('.', '_');
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.trim().isEmpty()) {
            return envValue;
        }
        return defaultValue;
    }

    public static Connection getConnection() throws SQLException {
        String url = getConfig("shego.db.url", "jdbc:sqlserver://localhost:1433;databaseName=SheGo;encrypt=false");
        String user = getConfig("shego.db.user", "sa");
        String password = getConfig("shego.db.password", "change-me");
        return DriverManager.getConnection(url, user, password);
    }
}
