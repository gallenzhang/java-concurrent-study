package com.gallenzhang.concurrent;

/**
 * @description:
 * @className: com.gallenzhang.concurrent.InterruptDemo01
 * @copyRight: www.shopee.com by SZDC-BankingGroup
 * @author: xiaoqiang.zhang
 * @createDate: 2021/8/20
 */
public class InterruptDemo01 {

    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    System.out.println("线程1在执行工作...");
                }
            }
        };

        thread.start();

        Thread.sleep(1);

        thread.interrupt();

    }
}
