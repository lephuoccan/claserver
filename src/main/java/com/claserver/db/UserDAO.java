package com.claserver.db;

import com.claserver.models.User;
import java.sql.*;
import java.util.UUID;

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
        String sql = "INSERT INTO users(username,email,password_hash,shared_token,created_at) VALUES(?,?,?,?,?)";
        String sharedToken = UUID.randomUUID().toString().replace("-", "");
        long now = System.currentTimeMillis();

        try (Connection conn = PostgreSQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, passwordHash);
            ps.setString(4, sharedToken);
            ps.setLong(5, now);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                return new User(id, username, email, passwordHash, sharedToken, now, 0);
            }
            throw new Exception("Failed to create user");
        }
    }

    public User login(String loginId, String passwordHash) throws Exception {
        String sql = "SELECT id, username, email, shared_token, created_at, last_login FROM users WHERE (username=? OR email=?) AND password_hash=?";
        try (Connection conn = PostgreSQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, loginId);
            ps.setString(2, loginId);
            ps.setString(3, passwordHash);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) throw new Exception("Invalid login");

            int id = rs.getInt("id");
            String username = rs.getString("username");
            String email = rs.getString("email");
            String sharedToken = rs.getString("shared_token");
            long createdAt = rs.getLong("created_at");
            long lastLogin = rs.getLong("last_login");

            // Update last_login
            try (PreparedStatement u = conn.prepareStatement("UPDATE users SET last_login=? WHERE id=?")) {
                u.setLong(1, System.currentTimeMillis());
                u.setInt(2, id);
                u.executeUpdate();
            }

            return new User(id, username, email, passwordHash, sharedToken, createdAt, System.currentTimeMillis());
        }
    }
}
