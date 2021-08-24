package com.gallenzhang.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @description:
 * @className: com.gallenzhang.concurrent.AtomicIntegerDemo
 * @copyRight: www.shopee.com by SZDC-BankingGroup
 * @author: xiaoqiang.zhang
 * @createDate: 2021/8/22
 */
public class AtomicIntegerDemo {

    static Integer i = 0;
    static AtomicInteger j = new AtomicInteger(0);

    public static void main(String[] args) {
        //synchronizedAdd();
        atomicAdd();
    }

    private static void synchronizedAdd() {
        for (int i = 0; i < 10; i++) {
            new Thread() {
                @Override
                public void run() {
                    //10个线程就要依次的慢慢的一个一个的进入锁代码块，然后依次对i变量进行i++操作
                    //每次操作完i++，就写回主存，下一个线程从主存来加载，再次i++，并发性降低了
                    synchronized (AtomicIntegerDemo.class) {
                        System.out.println(++AtomicIntegerDemo.i);
                    }
                }
            }.start();
        }
    }

    private static void atomicAdd() {
        for (int i = 0; i < 10; i++) {
            new Thread() {
                @Override
                public void run() {
                    System.out.println(AtomicIntegerDemo.j.incrementAndGet());
                }
            }.start();
        }
    }
}
