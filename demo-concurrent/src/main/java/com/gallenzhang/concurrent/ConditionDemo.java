package com.gallenzhang.concurrent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @description:
 * @className: com.gallenzhang.concurrent.ConditionDemo
 * @author: gallenzhang
 * @createDate: 2021/8/31
 */
public class ConditionDemo {

    static int data = 0;
    static ReentrantLock lock = new ReentrantLock();
    static Condition condition = lock.newCondition();

    public static void main(String[] args) throws Exception {
        new Thread(() -> {
            lock.lock();
            System.out.println("第一个线程加锁");
            try {
                System.out.println("第一个线程释放锁以及阻塞等待");
                condition.await();
                System.out.println("第一个线程重新获取到锁");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lock.unlock();
            System.out.println("第一个线程释放锁");
        }).start();

        Thread.sleep(3000L);


        new Thread(() -> {
            lock.lock();
            System.out.println("第二个线程加锁");
            System.out.println("第二个线程唤醒第一个线程");
            condition.signal();
            lock.unlock();
            System.out.println("第二个线程释放锁");
        }).start();
    }
}
