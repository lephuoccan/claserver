package com.claserver.models;

import java.sql.Timestamp;

public class Device {
    private int id;
    private int dashboardId;
    private String name;
    private String token;
    private long lastHeartbeat;
    private long createdAt;
    private boolean online;

    public Device(int id, int dashboardId, String name, String token, long lastHeartbeat, long createdAt) {
        this.id = id;
        this.dashboardId = dashboardId;
        this.name = name;
        this.token = token;
        this.lastHeartbeat = lastHeartbeat;
        this.createdAt = createdAt;
    }

    public boolean isOnline() {
        return (System.currentTimeMillis() - lastHeartbeat) <= 15000;
    }

    public int getId() { return id; }
    public int getDashboardId() { return dashboardId; }
    public String getName() { return name; }
    public String getToken() { return token; }
    public long getLastHeartbeat() { return lastHeartbeat; }
    public void setLastHeartbeat(long lasthb) {this.lastHeartbeat = lasthb; }
    public boolean getOnline() { return online; }
}
