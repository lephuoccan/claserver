package com.claserver.db;

import com.claserver.utils.ConfigLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    public static Connection getConnection() throws SQLException {
        String url = ConfigLoader.get("db.url");
        String username = ConfigLoader.get("db.username");
        String password = ConfigLoader.get("db.password");

        return DriverManager.getConnection(url, username, password);
    }
}
