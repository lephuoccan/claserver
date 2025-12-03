package com.claserver.db;

import com.claserver.models.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserDAO {

    public boolean existsByUsername(String username) throws Exception {
        try (Connection conn = PostgreSQL.getConnection()) {
            String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
            return false;
        }
    }

    public boolean existsByEmail(String email) throws Exception {
        try (Connection conn = PostgreSQL.getConnection()) {
            String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
            return false;
        }
    }

    public void register(User user) throws Exception {
        try (Connection conn = PostgreSQL.getConnection()) {
            String sql = "INSERT INTO users(username, password, email) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getEmail());
            ps.executeUpdate();
        }
    }

    public boolean validateLogin(String usernameOrEmail, String password) throws Exception {
        try (Connection conn = PostgreSQL.getConnection()) {
            String sql = "SELECT password FROM users WHERE username = ? OR email = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, usernameOrEmail);
            ps.setString(2, usernameOrEmail);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String dbPass = rs.getString("password");
                return password.equals(dbPass);
            }
            return false;
        }
    }
}
