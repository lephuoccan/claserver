package com.claserver.services;

import com.claserver.db.PostgreSQL;
import java.sql.*;

public class UserService {

    public String register(String email, String pass) {
        try (Connection conn = PostgreSQL.getConnection()) {

            PreparedStatement check = conn.prepareStatement("SELECT id FROM users WHERE email=?");
            check.setString(1, email);
            ResultSet rs = check.executeQuery();
            if (rs.next()) return "{\"error\":\"Email exists\"}";

            PreparedStatement st = conn.prepareStatement(
                    "INSERT INTO users(email,password) VALUES (?,?)"
            );
            st.setString(1, email);
            st.setString(2, pass);
            st.executeUpdate();

            return "{\"status\":\"ok\"}";
        } catch (Exception e) {
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }

    public String login(String email, String pass) {
        try (Connection conn = PostgreSQL.getConnection()) {

            PreparedStatement st = conn.prepareStatement(
                    "SELECT id FROM users WHERE email=? AND password=?"
            );
            st.setString(1, email);
            st.setString(2, pass);
            ResultSet rs = st.executeQuery();

            if (rs.next())
                return "{\"token\":\"" + email + "_TOKEN\"}";

            return "{\"error\":\"Invalid credentials\"}";

        } catch (Exception e) {
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
    }
}
