package com.claserver.models;

public class VirtualPin {

    private int pin;
    private String value;

    public VirtualPin() {}

    public VirtualPin(int pin, String value) {
        this.pin = pin;
        this.value = value;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
