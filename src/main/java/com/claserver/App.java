package com.claserver;

import com.claserver.db.DatabaseInitializer;
import com.claserver.netty.HttpServer;
import com.claserver.netty.HttpsServer;
import com.claserver.tasks.HeartbeatChecker;

public class App {

    public static void main(String[] args) throws Exception {

        // Khởi tạo database
        DatabaseInitializer.init();
        new Thread(new com.claserver.tasks.HeartbeatChecker(), "heartbeat-checker").start();

        // Chạy HTTP server port 8080 trên thread riêng
        new Thread(() -> {
            try {
                new HttpServer(8080).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Chạy HTTPS server port 9443 trên thread riêng
        new Thread(() -> {
            try {
                new HttpsServer().start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

    }
}
