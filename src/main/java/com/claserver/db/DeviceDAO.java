package com.claserver.db;

import com.claserver.models.Device;
import java.sql.*;
import java.util.UUID;

public class DeviceDAO {

    public Device createDevice(int dashboardId, String name) throws Exception {
        String sql = "INSERT INTO devices(dashboard_id, name, token, last_heartbeat, created_at) VALUES (?, ?, ?, ?, ?)";
        String token = UUID.randomUUID().toString().replace("-", "");
        long now = System.currentTimeMillis();

        try (Connection conn = PostgreSQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, dashboardId);
            ps.setString(2, name);
            ps.setString(3, token);
            ps.setLong(4, now); // initial heartbeat
            ps.setLong(5, now);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                int id = rs.getInt(1);
                return new Device(id, dashboardId, name, token, now, now);
            }
            throw new Exception("Failed to create device");
        }
    }

    public int getDeviceIdByToken(String token) throws Exception {
        String sql = "SELECT id FROM devices WHERE token=?";
        try (Connection conn = PostgreSQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, token);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("id");
            return -1;
        }
    }

    public void heartbeat(String token) throws Exception {
        String sql = "UPDATE devices SET last_heartbeat=? WHERE token=?";
        try (Connection conn = PostgreSQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, System.currentTimeMillis());
            ps.setString(2, token);
            ps.executeUpdate();
        }
    }
}
