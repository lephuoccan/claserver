package com.claserver.db;

import com.claserver.models.Dashboard;
import java.sql.*;
import java.util.UUID;

public class DashboardDAO {

    public Dashboard createDashboard(int userId, String name) throws Exception {
        String sql = "INSERT INTO dashboards(user_id, name, shared_token, created_at) VALUES (?, ?, ?, ?)";
        String sharedToken = UUID.randomUUID().toString().replace("-", "");
        long now = System.currentTimeMillis();

        try (Connection conn = PostgreSQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, userId);
            ps.setString(2, name);
            ps.setString(3, sharedToken);
            ps.setLong(4, now);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                return new Dashboard(id, userId, name, sharedToken, now);
            }
            throw new Exception("Failed to create dashboard");
        }
    }

    public Dashboard getDashboardBySharedToken(String sharedToken) throws Exception {
        String sql = "SELECT id, user_id, name, shared_token, created_at FROM dashboards WHERE shared_token=?";
        try (Connection conn = PostgreSQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sharedToken);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Dashboard(rs.getInt("id"), rs.getInt("user_id"), rs.getString("name"),
                        rs.getString("shared_token"), rs.getLong("created_at"));
            }
            return null;
        }
    }
}
