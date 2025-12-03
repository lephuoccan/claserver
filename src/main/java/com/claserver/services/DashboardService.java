package com.claserver.services;

import com.claserver.db.DashboardDAO;
import com.claserver.models.Dashboard;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class DashboardService {
    private final DashboardDAO dao = new DashboardDAO();
    private final Gson gson = new Gson();

    /**
     * Called from HttpHandler:
     * dashboardService.createDashboard(userId, name)
     *
     * Returns: { status: "ok", dashboard_id: N, shared_token: "..." } or error JSON.
     */
    public String createDashboard(int userId, String name) {
        try {
            Dashboard d = dao.createDashboard(userId, name);
            JsonObject r = new JsonObject();
            r.addProperty("status", "ok");
            r.addProperty("dashboard_id", d.getId());
            r.addProperty("shared_token", d.getSharedToken());
            return gson.toJson(r);
        } catch (Exception ex) {
            JsonObject r = new JsonObject();
            r.addProperty("status", "error");
            r.addProperty("msg", ex.getMessage());
            return gson.toJson(r);
        }
    }
}
