package com.gallenzhang.concurrent;

import java.util.concurrent.Semaphore;

/**
 * @description:
 * @className: com.gallenzhang.concurrent.SemaphoreDemo
 * @author: gallenzhang
 * @createDate: 2021/9/8
 */
public class SemaphoreDemo {

    public static void main(String[] args) throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);

        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    System.out.println("线程1执行一个计算任务");
                    semaphore.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    System.out.println("线程2执行一个计算任务");
                    semaphore.release();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        semaphore.acquire(1);
        System.out.println("等待1个线程完成任务即可......");
    }
}
