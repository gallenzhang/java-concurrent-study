package com.gallenzhang.concurrent;

/**
 * @description:
 * @className: com.gallenzhang.concurrent.ThreadUnsafeDemo
 * @copyRight: www.shopee.com by SZDC-BankingGroup
 * @author: xiaoqiang.zhang
 * @createDate: 2021/8/21
 */
public class ThreadUnsafeDemo {

    private static int data = 0;

    public static void main(String[] args) throws InterruptedException {
        Thread thread1 = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    ThreadUnsafeDemo.data++;
                    System.out.println("线程1：" + data);
                }


            }
        };

        Thread thread2 = new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    ThreadUnsafeDemo.data++;
                    System.out.println("线程2：" + data);
                }
            }
        };

        thread1.start();
        thread2.start();

        thread1.join();
        thread2.join();
    }
}
