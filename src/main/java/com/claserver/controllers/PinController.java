package com.claserver.controllers;

import com.claserver.services.PinService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.netty.handler.codec.http.FullHttpRequest;

public class PinController {

    private final PinService pin = new PinService();

    public String write(FullHttpRequest req) {
        JsonObject body = JsonParser.parseString(req.content().toString()).getAsJsonObject();
        return pin.write(body.get("token").getAsString(),
                body.get("pin").getAsInt(),
                body.get("value").getAsString());
    }

    public String read(FullHttpRequest req) {
        String uri = req.uri(); // /api/pin/read?token=xxx&pin=10
        return pin.read(uri);
    }
}
