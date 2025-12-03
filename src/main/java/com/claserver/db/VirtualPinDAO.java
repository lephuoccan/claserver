package com.claserver.db;

import java.sql.*;

public class VirtualPinDAO {

    public void writePin(int deviceId, int pin, String value) throws Exception {
        String sql = "INSERT INTO virtual_pins(device_id, pin, value) VALUES (?, ?, ?) " +
                "ON CONFLICT(device_id, pin) DO UPDATE SET value=EXCLUDED.value";
        try (Connection conn = PostgreSQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, deviceId);
            ps.setInt(2, pin);
            ps.setString(3, value);
            ps.executeUpdate();
        }
    }

    public String readPin(int deviceId, int pin) throws Exception {
        String sql = "SELECT value FROM virtual_pins WHERE device_id=? AND pin=?";
        try (Connection conn = PostgreSQL.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, deviceId);
            ps.setInt(2, pin);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("value");
            return null;
        }
    }
}
