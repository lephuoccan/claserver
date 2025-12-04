package com.claserver.models;

import java.time.Instant;

public class Dashboard {
    private int id;
    private int userId;
    private String name;
    private String sharedToken;
    private Instant createdAt;

    // Constructor đầy đủ
    public Dashboard(int id, int userId, String name, String sharedToken, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.sharedToken = sharedToken;
        this.createdAt = createdAt;
    }

    // Getter / Setter nếu cần
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getSharedToken() { return sharedToken; }
    public Instant getCreatedAt() { return createdAt; }

    public void setName(String name) { this.name = name; }
    public void setSharedToken(String sharedToken) { this.sharedToken = sharedToken; }
}
