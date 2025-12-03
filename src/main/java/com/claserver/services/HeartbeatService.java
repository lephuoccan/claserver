package com.claserver.services;

import com.claserver.db.DeviceDAO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class HeartbeatService {
    private final DeviceDAO dao = new DeviceDAO();
    private final Gson gson = new Gson();

    public String heartbeat(String token) {
        try {
            boolean ok = dao.heartbeat(token);
            JsonObject r = new JsonObject();
            if (!ok) {
                r.addProperty("status", "error");
                r.addProperty("msg", "invalid token");
            } else {
                r.addProperty("status", "ok");
            }
            return gson.toJson(r);
        } catch (Exception ex) {
            JsonObject r = new JsonObject();
            r.addProperty("status", "error");
            r.addProperty("msg", ex.getMessage());
            return gson.toJson(r);
        }
    }
}
