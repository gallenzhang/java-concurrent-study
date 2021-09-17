package com.gallenzhang.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @description:
 * @className: com.gallenzhang.concurrent.ThreadPoolDemo
 * @author: gallenzhang
 * @createDate: 2021/9/15
 */
public class FixedThreadPoolDemo {

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(4);

        for (int i = 0; i < 10; i++) {
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("线程池异步执行任务......" + Thread.currentThread());
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        //必须是等待队列里所有的任务都执行完毕了，才可以关闭线程池
        threadPool.shutdown();
    }
}
