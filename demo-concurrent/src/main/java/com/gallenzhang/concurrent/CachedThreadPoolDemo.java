package com.gallenzhang.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @description:
 * @className: com.gallenzhang.concurrent.CachedThreadPoolDemo
 * @author: gallenzhang
 * @createDate: 2021/9/17
 */
public class CachedThreadPoolDemo {

    public static void main(String[] args) {
        //不限制线程的数量，你无论提交多少个线程，都会直接开辟创建一个新的线程来执行你的这个任务
        //非常适合短时间内突然涌入大量任务的场景，你的大量的线程如果之后空闲了，没有任务了，达到一定时间之后，就会自动释放掉。
        ExecutorService threadPool = Executors.newCachedThreadPool();

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
    }
}
