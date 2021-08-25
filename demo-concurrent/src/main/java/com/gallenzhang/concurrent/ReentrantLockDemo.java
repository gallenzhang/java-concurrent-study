package com.gallenzhang.concurrent;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @description:
 * @className: com.gallenzhang.concurrent.ReentrantLockDemo
 * @copyRight: www.shopee.com by SZDC-BankingGroup
 * @author: xiaoqiang.zhang
 * @createDate: 2021/8/25
 */
public class ReentrantLockDemo {

    static int data = 0;
    static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    lock.lock();
                    ReentrantLockDemo.data++;
                    System.out.println(ReentrantLockDemo.data);
                    lock.unlock();
                }
            }
        }.start();


        new Thread() {
            @Override
            public void run() {
                for (int i = 0; i < 10; i++) {
                    lock.lock();
                    ReentrantLockDemo.data++;
                    System.out.println(ReentrantLockDemo.data);
                    lock.unlock();
                }
            }
        }.start();
    }
}
