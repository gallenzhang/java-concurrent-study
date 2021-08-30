package com.gallenzhang.concurrent;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @description:
 * @className: com.gallenzhang.concurrent.ReentrantReadWriteLock
 * @author: gallenzhang
 * @createDate: 2021/8/30
 */
public class ReentrantReadWriteLockDemo {

    public static void main(String[] args) {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

        //写锁
        lock.writeLock().lock();
        lock.writeLock().unlock();

        //读锁
        lock.readLock().lock();
        lock.readLock().unlock();
    }
}
