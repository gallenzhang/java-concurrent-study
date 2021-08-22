package com.gallenzhang.register.server;

/**
 * @description: 心跳测量计数器
 * @className: com.gallenzhang.register.server.HeartbeatMessuredRate
 * @author: gallenzhang
 * @createDate: 2021/8/20
 */
public class HeartbeatMessuredRate {

    /**
     * 单例实例
     */
    private static HeartbeatMessuredRate instance = new HeartbeatMessuredRate();

    /**
     * 最近一分钟的心跳次数
     */
    private long latestMinuteHeartbeatRate = 0L;

    /**
     * 最近一分钟的时间戳
     */
    private long latestMinuteTimestamp = System.currentTimeMillis();

    private HeartbeatMessuredRate() {
        Daemon daemon = new Daemon();
        daemon.setDaemon(true);
        daemon.start();
    }

    /**
     * 获取单例实例
     *
     * @return
     */
    public static HeartbeatMessuredRate getInstance() {
        return instance;
    }

    /**
     * 增加一次最近一分钟的心跳次数
     */
    public void increment() {
        synchronized (HeartbeatMessuredRate.class) {
            latestMinuteHeartbeatRate++;
        }
    }

    /**
     * 获取最近一分钟的心跳次数
     *
     * @return
     */
    public synchronized long get() {
        return latestMinuteHeartbeatRate;
    }

    private class Daemon extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    synchronized (HeartbeatMessuredRate.class) {
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - latestMinuteTimestamp > 60 * 1000) {
                            latestMinuteHeartbeatRate = 0;
                            latestMinuteTimestamp = System.currentTimeMillis();
                        }
                    }
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

