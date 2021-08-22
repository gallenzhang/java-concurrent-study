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
        synchronizedAdd();
    }

    private static void synchronizedAdd() {
        for (int i = 0; i < 10; i++) {
            new Thread(){
                @Override
                public void run() {
                    System.out.println(++AtomicIntegerDemo.i);
                }
            }.start();
        }
    }
}
