package com.claserver.utils;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static Properties prop = new Properties();

    static {
        try (InputStream in = ConfigLoader.class.getResourceAsStream("/application.properties")) {
            prop.load(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        return prop.getProperty(key);
    }
}
