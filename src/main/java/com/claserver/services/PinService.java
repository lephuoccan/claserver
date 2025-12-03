package com.claserver.services;

import com.claserver.db.RedisClient;
import redis.clients.jedis.Jedis;

public class PinService {

    public String write(String token, int pin, String value) {
        try (Jedis jedis = RedisClient.get()) {
            jedis.set(token + ":V" + pin, value);
            return "{\"status\":\"ok\"}";
        }
    }

    public String read(String uri) {
        try (Jedis jedis = RedisClient.get()) {

            String token = uri.split("token=")[1].split("&")[0];
            String pin = uri.split("pin=")[1];

            String value = jedis.get(token + ":V" + pin);
            if (value == null) value = "0";

            return "{\"value\":\"" + value + "\"}";
        }
    }
}
