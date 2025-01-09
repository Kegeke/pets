package org.example.pets.db;

import org.example.pets.config.ConfigLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionManager {
    private final ConfigLoader configLoader;

    public ConnectionManager() {
        configLoader = new ConfigLoader();

        try {
            Class.forName(configLoader.getProperties("driverClassName"));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                configLoader.getProperties("url"),
                configLoader.getProperties("userName"),
                configLoader.getProperties("password"));
    }
}
