package com.gallenzhang.concurrent;

import java.util.concurrent.Exchanger;

/**
 * @description:
 * @className: com.gallenzhang.concurrent.ExchangerDemo
 * @author: gallenzhang
 * @createDate: 2021/9/8
 */
public class ExchangerDemo {

    public static void main(String[] args) {
        Exchanger<String> exchanger = new Exchanger<>();

        new Thread() {
            @Override
            public void run() {
                try {
                    String data = exchanger.exchange("线程1的数据");
                    System.out.println("线程1获取到线程2交换过来的数据：" + data);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                try {
                    String data = exchanger.exchange("线程2的数据");
                    System.out.println("线程2获取到线程1交换过来的数据：" + data);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
