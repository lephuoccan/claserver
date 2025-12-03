package com.claserver.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.sql.Timestamp;

public class Dashboard {

    private int id;
    private int userId;
    private String name;
    private String sharedToken;
    private Timestamp createdAt;
    private Timestamp lastUpdated;

    private List<Device> devices = new ArrayList<>();

    public Dashboard(int id, int userId, String name, String sharedToken, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.createdAt = createdAt;
        this.sharedToken = sharedToken;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.lastUpdated = new Timestamp(System.currentTimeMillis());
    }

    public String getSharedToken() {
        return sharedToken;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getLastUpdated() {
        return lastUpdated;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void addDevice(Device dev) {
        devices.add(dev);
        this.lastUpdated = new Timestamp(System.currentTimeMillis());
    }

    public Device getDeviceByToken(String token) {
        for (Device d : devices) {
            if (d.getToken().equals(token)) return d;
        }
        return null;
    }

    public Device getDeviceById(int id) {
        for (Device d : devices) {
            if (d.getId() == id) return d;
        }
        return null;
    }
}
