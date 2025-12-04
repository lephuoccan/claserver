package com.claserver.db;

import com.claserver.models.User;
import java.sql.*;
import java.time.Instant;
import java.util.Objects;

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
        // long now = System.currentTimeMillis();
        // Lấy thời điểm hiện tại (UTC)
        Instant now = Instant.now(); 
        String sql = "INSERT INTO users(username, email, password_hash, shared_token, last_login) VALUES (?,?,?,?,?)";

        try (Connection conn = PostgreSQL.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, passwordHash);
            stmt.setString(4, sharedToken);
            stmt.setTimestamp(5, Timestamp.from(now)); //--- Last login = now
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
        Instant now = Instant.now();
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

            // Sử dụng getTimestamp() và toInstant() để tương thích với mọi driver
            Instant createdAt = Objects.requireNonNull(rs.getTimestamp("created_at")).toInstant();
            
            // last_login có thể NULL, nên kiểm tra null trước
            Timestamp lastLoginSql = rs.getTimestamp("last_login");
            Instant lastLogin = (lastLoginSql != null) ? lastLoginSql.toInstant() : null;
            
            // Update last login
            try (PreparedStatement u = conn.prepareStatement("UPDATE users SET last_login=? WHERE id=?")) {
                lastLogin = now;
                u.setTimestamp(1, Timestamp.from(lastLogin));
                u.setInt(2, id);
                u.executeUpdate();
            }

            return new User(id, username, email, passwordHash, sharedToken, createdAt, lastLogin);
        }
    }
}
