package com.claserver.db;

import java.sql.Connection;
import java.sql.Statement;

public class DatabaseInitializer {

    public static void init() {
        try (Connection conn = PostgreSQL.getConnection(); Statement stmt = conn.createStatement()) {

            // Tạo bảng users nếu chưa tồn tại
            String usersTable = "CREATE TABLE IF NOT EXISTS users (" +
                    "id SERIAL PRIMARY KEY," +
                    "username VARCHAR(50) UNIQUE NOT NULL," + // thêm username
                    "email VARCHAR(255) UNIQUE NOT NULL," +
                    "password VARCHAR(255) NOT NULL" +
                    ");";


            stmt.execute(usersTable);

            // Tạo bảng virtual_pins nếu chưa tồn tại
            String pinsTable = "CREATE TABLE IF NOT EXISTS virtual_pins (" +
                    "id SERIAL PRIMARY KEY," +
                    "user_id INT NOT NULL," +
                    "pin INT NOT NULL," +
                    "value TEXT," +
                    "CONSTRAINT fk_user FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE" +
                    ");";

            stmt.execute(pinsTable);

            System.out.println("[PostgreSQL] Tables initialized successfully.");

        } catch (Exception e) {
            System.err.println("[PostgreSQL] Error initializing tables: " + e.getMessage());
        }
    }
}
