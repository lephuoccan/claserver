package com.claserver.models;

public class VirtualPin {
    private int deviceId;
    private int pin;
    private String value;

    public VirtualPin(int deviceId, int pin, String value) {
        this.deviceId = deviceId;
        this.pin = pin;
        this.value = value;
    }

    public int getDeviceId() { return deviceId; }
    public int getPin() { return pin; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}
