package com.gallenzhang.concurrent;

import java.util.concurrent.locks.LockSupport;

/**
 * @description:
 * @className: com.gallenzhang.concurrent.ParkDemo
 * @author: gallenzhang
 * @createDate: 2021/8/26
 */
public class ParkDemo {

    public static void main(String[] args) {
        Thread thread1 = new Thread() {
            @Override
            public void run() {
                System.out.println("挂起之前执行的操作");
                LockSupport.park(this);
                System.out.println("被唤醒之后执行的操作");
            }
        };

        thread1.start();

        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    try {
                        Thread.sleep(1000L);
                        System.out.println("等待" + (i + 1) + "秒");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                System.out.println("尝试唤醒第一个线程");
                LockSupport.unpark(thread1);
            }
        }.start();
    }
}
