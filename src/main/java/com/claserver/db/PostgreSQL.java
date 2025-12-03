package com.claserver.db;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class PostgreSQL {

    private static String url;
    private static String user;
    private static String pass;

    static {
        try {
            Properties prop = new Properties();
            InputStream input = PostgreSQL.class.getResourceAsStream("/application.properties");
            prop.load(input);

            url = prop.getProperty("db.url");
            user = prop.getProperty("db.username");
            pass = prop.getProperty("db.password");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(url, user, pass);
    }
}
