package com.gallenzhang.concurrent;

import java.util.concurrent.CountDownLatch;

/**
 * @description:
 * @className: com.gallenzhang.concurrent.CountDownLatchDemo
 * @author: gallenzhang
 * @createDate: 2021/9/7
 */
public class CountDownLatchDemo {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(2);

        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(1000L);
                    System.out.println("线程1开始执行，休眠1秒......");
                    Thread.sleep(1000L);
                    System.out.println("线程1准备执行countDown操作......");
                    latch.countDown();
                    System.out.println("线程1完成执行countDown操作......");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();

        new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(1000L);
                    System.out.println("线程2开始执行，休眠1秒......");
                    Thread.sleep(1000L);
                    System.out.println("线程2准备执行countDown操作......");
                    latch.countDown();
                    System.out.println("线程2完成执行countDown操作......");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();

        System.out.println("main线程准备执行CountDownLatch的await操作，将会同步阻塞等待......");

        latch.await();

        //Thread.join()，在有这个CountDownLatch API之前，同步阻塞某个线程执行完毕，Thread.j其实采用的都是oin()

        System.out.println("所有线程都完成countDown操作，main线程的await阻塞等待结束");
    }
}
