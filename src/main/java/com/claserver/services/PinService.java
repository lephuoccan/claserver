package com.claserver.services;

import com.claserver.db.DeviceDAO;
import com.claserver.db.VirtualPinDAO;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class PinService {
    private final DeviceDAO deviceDAO = new DeviceDAO();
    private final VirtualPinDAO pinDAO = new VirtualPinDAO();
    private final Gson gson = new Gson();

    /**
     * Called from HttpHandler:
     * pinService.writePin(token, pin, value)
     *
     * Returns { status: "ok" } or error.
     * Also updates heartbeat for token.
     */
    public String writePin(String token, int pin, String value) {
        try {
            int deviceId = deviceDAO.getDeviceIdByToken(token);
            if (deviceId == -1) {
                JsonObject r = new JsonObject();
                r.addProperty("status", "error");
                r.addProperty("msg", "invalid token");
                return gson.toJson(r);
            }

            // validate pin index
            if (pin < 0 || pin > 127) {
                JsonObject r = new JsonObject();
                r.addProperty("status", "error");
                r.addProperty("msg", "pin must be between 0 and 127");
                return gson.toJson(r);
            }

            pinDAO.writePin(deviceId, pin, value);
            deviceDAO.heartbeat(token);

            JsonObject r = new JsonObject();
            r.addProperty("status", "ok");
            return gson.toJson(r);
        } catch (Exception ex) {
            JsonObject r = new JsonObject();
            r.addProperty("status", "error");
            r.addProperty("msg", ex.getMessage());
            return gson.toJson(r);
        }
    }

    /**
     * Called from HttpHandler:
     * pinService.readPin(token, pin)
     *
     * Returns { status:"ok", pin: N, value: "..." } or error.
     * Also updates heartbeat for token.
     */
    public String readPin(String token, int pin) {
        try {
            int deviceId = deviceDAO.getDeviceIdByToken(token);
            if (deviceId == -1) {
                JsonObject r = new JsonObject();
                r.addProperty("status", "error");
                r.addProperty("msg", "invalid token");
                return gson.toJson(r);
            }

            if (pin < 0 || pin > 127) {
                JsonObject r = new JsonObject();
                r.addProperty("status", "error");
                r.addProperty("msg", "pin must be between 0 and 127");
                return gson.toJson(r);
            }

            String value = pinDAO.readPin(deviceId, pin);
            deviceDAO.heartbeat(token);

            JsonObject r = new JsonObject();
            r.addProperty("status", "ok");
            r.addProperty("pin", pin);
            r.addProperty("value", value == null ? "" : value);
            return gson.toJson(r);
        } catch (Exception ex) {
            JsonObject r = new JsonObject();
            r.addProperty("status", "error");
            r.addProperty("msg", ex.getMessage());
            return gson.toJson(r);
        }
    }
}
