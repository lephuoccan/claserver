package com.claserver.db;

import com.claserver.models.User;
import java.sql.*;

public class UserDAO {

    public boolean exists(String username, String email) throws Exception {
        String sql = "SELECT COUNT(*) FROM users WHERE username=? OR email=?";
        try (Connection conn = PostgreSQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
            return false;
        }
    }

    public User createUser(String username, String email, String passwordHash) throws Exception {
        String sharedToken = java.util.UUID.randomUUID().toString();
        long now = System.currentTimeMillis();

        String sql = "INSERT INTO users(username, email, password_hash, shared_token, created_at, last_login) VALUES (?,?,?,?,to_timestamp(? / 1000.0),to_timestamp(? / 1000.0))";

        try (Connection conn = PostgreSQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, passwordHash);
            stmt.setString(4, sharedToken);
            stmt.setLong(5, now);
            stmt.setLong(6, now);
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                return new User(id, username, email, passwordHash, sharedToken, now, now);
            }
            throw new Exception("Failed to create user");
        }
    }

    public User login(String loginId, String passwordHash) throws Exception {
        String sql = "SELECT * FROM users WHERE (username=? OR email=?) AND password_hash=?";
        try (Connection conn = PostgreSQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, loginId);
            stmt.setString(2, loginId);
            stmt.setString(3, passwordHash);

            ResultSet rs = stmt.executeQuery();
            if (!rs.next()) throw new Exception("Invalid username/email or password");

            int id = rs.getInt("id");
            String username = rs.getString("username");
            String email = rs.getString("email");
            String sharedToken = rs.getString("shared_token");
            long createdAt = rs.getLong("created_at");
            long lastLogin = rs.getLong("last_login");

            // Update last login
            try (PreparedStatement u = conn.prepareStatement("UPDATE users SET last_login=? WHERE id=?")) {
                u.setLong(1, System.currentTimeMillis());
                u.setInt(2, id);
                u.executeUpdate();
            }

            return new User(id, username, email, passwordHash, sharedToken, createdAt, System.currentTimeMillis());
        }
    }
}
