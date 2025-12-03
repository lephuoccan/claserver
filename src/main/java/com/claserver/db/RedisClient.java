package com.claserver.db;

import redis.clients.jedis.Jedis;

import java.io.InputStream;
import java.util.Properties;

public class RedisClient {

    private static String host = "127.0.0.1";
    private static int port = 6379;

    static {
        try {
            Properties prop = new Properties();
            InputStream input = RedisClient.class.getResourceAsStream("/application.properties");
            prop.load(input);

            host = prop.getProperty("redis.host", "127.0.0.1");
            port = Integer.parseInt(prop.getProperty("redis.port", "6379"));

            System.out.println("[Redis] Using host=" + host + " port=" + port);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lấy Jedis connection
    public static Jedis get() {
        return new Jedis(host, port);
    }

    // Test connection (tuỳ chọn)
    public static void testConnection() {
        try (Jedis jedis = get()) {
            String pong = jedis.ping();
            System.out.println("[Redis] Ping: " + pong);
        } catch (Exception e) {
            System.err.println("[Redis] Error: " + e.getMessage());
        }
    }
}
