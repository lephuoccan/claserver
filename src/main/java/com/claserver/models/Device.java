package com.claserver.models;

import java.sql.Timestamp;
import java.time.Instant;

public class Device {
    private int id;
    private int dashboardId;
    private String name;
    private String token;
    private Instant lastHeartbeat;
    private Instant createdAt;
    private boolean online;

    public Device(int id, int dashboardId, String name, String token, Instant lastHeartbeat, Instant createdAt) {
        this.id = id;
        this.dashboardId = dashboardId;
        this.name = name;
        this.token = token;
        this.lastHeartbeat = lastHeartbeat;
        this.createdAt = createdAt;
    }

    // public boolean isOnline() {
    //     return (Timestamp.from(Instant.now()) - lastHeartbeat) <= 15000;
    // }

    public int getId() { return id; }
    public int getDashboardId() { return dashboardId; }
    public String getName() { return name; }
    public String getToken() { return token; }
    public Instant getLastHeartbeat() { return lastHeartbeat; }
    public void setLastHeartbeat(Instant lasthb) {this.lastHeartbeat = lasthb; }
    public boolean getOnline() { return online; }
}
