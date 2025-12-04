package com.claserver.db;

import com.claserver.models.Device;
import java.sql.*;
import java.time.Instant;
import java.util.UUID;

public class DeviceDAO {

    public Device createDevice(int dashboardId, String name) throws Exception {
        String sql = "INSERT INTO devices(dashboard_id, name, token, last_heartbeat, created_at) VALUES (?, ?, ?, ?, ?)";
        String token = UUID.randomUUID().toString().replace("-", "");
        Instant now = Instant.now();

        try (Connection conn = PostgreSQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, dashboardId);
            ps.setString(2, name);
            ps.setString(3, token);
            ps.setTimestamp(4, Timestamp.from(now)); // initial heartbeat
            ps.setTimestamp(5, Timestamp.from(now));
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

    // gọi mỗi 10 giây từ một scheduler để check heartbeat
    public void checkHeartbeat() throws Exception {
        String sql = "UPDATE devices " +
                     "SET status='offline' " +
                     "WHERE last_heartbeat < NOW() - INTERVAL '15 seconds'";
        try (Connection conn = PostgreSQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }
    
    public boolean heartbeat(String token) throws Exception {
        String sql = "UPDATE devices SET last_heartbeat = CURRENT_TIMESTAMP WHERE token=?";
        try (Connection conn = PostgreSQL.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            int affected = ps.executeUpdate();
            return affected > 0; // true nếu token tồn tại và update thành công
        }
    }

}
