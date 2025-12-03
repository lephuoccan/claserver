package com.claserver.services;

import com.claserver.db.DeviceDAO;
import com.claserver.models.Device;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class DeviceService {
    private final DeviceDAO dao = new DeviceDAO();
    private final Gson gson = new Gson();

    /**
     * Called from HttpHandler:
     * deviceService.createDevice(dashboardId, name)
     *
     * Returns: { status: "ok", device_id: N, token: "..." } or error.
     */
    public String createDevice(int dashboardId, String name) {
        try {
            Device d = dao.createDevice(dashboardId, name);
            JsonObject r = new JsonObject();
            r.addProperty("status", "ok");
            r.addProperty("device_id", d.getId());
            r.addProperty("token", d.getToken());
            return gson.toJson(r);
        } catch (Exception ex) {
            JsonObject r = new JsonObject();
            r.addProperty("status", "error");
            r.addProperty("msg", ex.getMessage());
            return gson.toJson(r);
        }
    }

    /**
     * Update heartbeat in-memory (called by heartbeat endpoint or when device does pin read/write)
     */
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
