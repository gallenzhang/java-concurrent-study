package com.gallenzhang.concurrent;

/**
 * @description:
 * @className: com.gallenzhang.concurrent.InterruptDemo02
 * @copyRight: www.shopee.com by SZDC-BankingGroup
 * @author: xiaoqiang.zhang
 * @createDate: 2021/8/20
 */
public class InterruptDemo02 {

    public static void main(String[] args) throws InterruptedException {
        MyThread thread = new MyThread();
        thread.start();

        Thread.sleep(1000);

        thread.setShouldRun(false);
        thread.interrupt();
    }

    private static class MyThread extends Thread {
        private Boolean shouldRun = true;

        @Override
        public void run() {
            while (shouldRun) {
                try {
                    System.out.println("线程1在执行工作...");
                    Thread.sleep(30 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void setShouldRun(Boolean shouldRun) {
            this.shouldRun = shouldRun;
        }
    }
}
