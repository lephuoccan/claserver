package com.claserver.models;

import com.google.gson.Gson;

public class User {
    private int id;
    private String username;
    private String email;
    private String passwordHash;
    private String sharedToken;
    private long createdAt;
    private long lastLogin;

    public User(int id, String username, String email, String passwordHash, String sharedToken, long createdAt, long lastLogin) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.sharedToken = sharedToken;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }

    // Getters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getSharedToken() { return sharedToken; }
    public long getCreatedAt() { return createdAt; }
    public long getLastLogin() { return lastLogin; }

    // Convert user sang JSON
    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
