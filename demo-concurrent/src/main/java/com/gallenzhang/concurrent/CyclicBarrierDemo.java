package com.gallenzhang.concurrent;

import java.util.concurrent.CyclicBarrier;

/**
 * @description:
 * @className: com.gallenzhang.concurrent.CyclicBarrierDemo
 * @author: gallenzhang
 * @createDate: 2021/9/7
 */
public class CyclicBarrierDemo {

    public static void main(String[] args) {
        CyclicBarrier barrier = new CyclicBarrier(3, new Runnable() {
            @Override
            public void run() {
                System.out.println("所有线程都完成了自己的任务，现在可以合并结果了......");
            }

            //Runnable不是作为一个线程来执行的，他其实就是一个代码，当所有线程都完成自己的任务之后，
            //就会触发这段代码的执行，不是一个独立的线程
        });

        new Thread() {
            @Override
            public void run() {
                try {
                    System.out.println("线程1执行自己的一部分工作......");
                    barrier.await();
                    System.out.println("最终结果合并完成，线程1可以退出......");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                try {
                    System.out.println("线程2执行自己的一部分工作......");
                    barrier.await();
                    System.out.println("最终结果合并完成，线程2可以退出......");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                try {
                    System.out.println("线程3执行自己的一部分工作......");
                    barrier.await();
                    System.out.println("最终结果合并完成，线程3可以退出......");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
