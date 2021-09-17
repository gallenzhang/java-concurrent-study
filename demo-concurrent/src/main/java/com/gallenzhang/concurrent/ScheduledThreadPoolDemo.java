package com.gallenzhang.concurrent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @className: com.gallenzhang.concurrent.ScheduledThreadPoolDemo
 * @author: gallenzhang
 * @createDate: 2021/9/17
 */
public class ScheduledThreadPoolDemo {

    public static void main(String[] args) {
        ScheduledExecutorService threadPoolExecutor = Executors.newScheduledThreadPool(10);
        threadPoolExecutor.schedule(new Runnable() {
            @Override
            public void run() {
                System.out.println("延迟5秒钟执行任务");
            }
        }, 5, TimeUnit.SECONDS);

        threadPoolExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                System.out.println("每隔3秒钟执行的任务");
            }
        },5,3,TimeUnit.SECONDS);
    }
}
