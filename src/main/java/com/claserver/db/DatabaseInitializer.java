package com.claserver.db;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void init() {
        try (Connection conn = PostgreSQL.getConnection(); Statement stmt = conn.createStatement()) {

            // Tạo bảng users nếu chưa tồn tại
            String usersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id SERIAL PRIMARY KEY," +  // ID tự tăng khi có dòng mới thêm vào
                    "username VARCHAR(50) UNIQUE NOT NULL," + // thêm username
                    "email VARCHAR(255) UNIQUE NOT NULL," + // thêm email
                    "password_hash VARCHAR(255) NOT NULL," +  // thêm password
                    "shared_token VARCHAR(64) UNIQUE NOT NULL," +
                    "created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP," +
                    "last_login TIMESTAMPTZ" +
                    ");";


            stmt.execute(usersTable);

            // Tạo bảng dashboards nếu chưa tồn tại
            String dashboardsTable = "CREATE TABLE IF NOT EXISTS dashboards (" +
                    "id SERIAL PRIMARY KEY," +
                    "user_id INT NOT NULL," +
                    "name VARCHAR(255) NOT NULL," +
                    "shared_token VARCHAR(255) UNIQUE NOT NULL," +
                    "created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP," +
                    " FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                    ");";


            stmt.execute(dashboardsTable);

            // Tạo bảng users nếu chưa tồn tại
            String devicesTable = "CREATE TABLE IF NOT EXISTS devices (" +
                    "id SERIAL PRIMARY KEY," +
                    "dashboard_id INT NOT NULL REFERENCES dashboards(id) ON DELETE CASCADE," +
                    "name VARCHAR(255)," +
                    "token VARCHAR(255) UNIQUE NOT NULL," +
                    "last_heartbeat TIMESTAMPTZ," +
                    "created_at TIMESTAMPTZ NOT NULL" +
                    ");";


            stmt.execute(devicesTable);

            // Tạo bảng virtual_pins nếu chưa tồn tại
            String virtual_pinsTable = "CREATE TABLE IF NOT EXISTS virtual_pins (" +
                    "id SERIAL PRIMARY KEY," +
                    "device_id INT NOT NULL REFERENCES devices(id) ON DELETE CASCADE," +
                    "pin INT NOT NULL," +
                    "value TEXT," +
                    "UNIQUE(device_id, pin)" +
                    ");";

            stmt.execute(virtual_pinsTable);

            System.out.println("[PostgreSQL] Tables initialized successfully.");

        } catch (Exception e) {
            System.err.println("[PostgreSQL] Error initializing tables: " + e.getMessage());
        }
    }
}
