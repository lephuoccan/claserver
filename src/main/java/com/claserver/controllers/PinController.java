package com.claserver.controllers;

import com.claserver.services.PinService;

public class PinController {

    private final PinService pinService = new PinService();

    // POST /pin/write
    public String writePin(String token, int pin, String value) {
        return pinService.writePin(token, pin, value);
    }

    // POST /pin/read
    public String readPin(String token, int pin) {
        return pinService.readPin(token, pin);
    }
}
