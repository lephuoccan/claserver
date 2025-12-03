package com.claserver.tasks;

import com.claserver.db.DeviceDAO;

public class HeartbeatChecker implements Runnable {

    private final DeviceDAO deviceDAO = new DeviceDAO();

    @Override
    public void run() {
        while (true) {
            try {
                deviceDAO.checkHeartbeat();
                Thread.sleep(5000); // 5 giây kiểm tra 1 lần
            } catch (Exception e) {
                System.out.println("HeartbeatChecker error: " + e.getMessage());
            }
        }
    }
}
