package com.gallenzhang.register.server;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * @description: 心跳测量计数器
 * @className: com.gallenzhang.register.server.HeartbeatMessuredRate
 * @author: gallenzhang
 * @createDate: 2021/8/20
 */
public class HeartbeatCounter {

    /**
     * 单例实例
     */
    private static HeartbeatCounter instance = new HeartbeatCounter();

    /**
     * 最近一分钟的心跳次数
     */
    //private AtomicLong latestMinuteHeartbeatRate = new AtomicLong(0);
    private LongAdder latestMinuteHeartbeatRate = new LongAdder();

    /**
     * 最近一分钟的时间戳
     */
    private long latestMinuteTimestamp = System.currentTimeMillis();

    private HeartbeatCounter() {
        Daemon daemon = new Daemon();
        daemon.setDaemon(true);
        daemon.start();
    }

    /**
     * 获取单例实例
     *
     * @return
     */
    public static HeartbeatCounter getInstance() {
        return instance;
    }

    /**
     * 增加一次最近一分钟的心跳次数
     */
    public /**synchronized*/
    void increment() {
        //这个synchronized上锁，性能其实是很差的，因为可能会有很多的线程，不断的发送心跳请求，就会来增加心跳次数
        //多线程会卡在这里，一个一个排队。依次上锁，累加1，再次释放锁，性能会有问题
        //latestMinuteHeartbeatRate++;

        //如果你的服务实例有很多的话，1万个服务实例，每秒可能都有很多个请求过来更新心跳
        //如果在这里加了synchronized的话，会影响并发的性能
        //换成了AtomicLong原子类之后，不加锁，无锁化，CAS操作，保证原子性，还可以多线程并发
        //latestMinuteHeartbeatRate.incrementAndGet();

        latestMinuteHeartbeatRate.increment();
    }

    /**
     * 获取最近一分钟的心跳次数
     *
     * @return
     */
    public long get() {
        //return latestMinuteHeartbeatRate.get();

        return latestMinuteHeartbeatRate.longValue();
    }

    private class Daemon extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    synchronized (HeartbeatCounter.class) {
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - latestMinuteTimestamp > 60 * 1000) {
                            //latestMinuteHeartbeatRate = new AtomicLong(0);
                            latestMinuteHeartbeatRate = new LongAdder();
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

