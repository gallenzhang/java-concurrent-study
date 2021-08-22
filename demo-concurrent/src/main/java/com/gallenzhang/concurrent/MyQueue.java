package com.gallenzhang.concurrent;

import java.util.LinkedList;

/**
 * @description:
 * @className: com.gallenzhang.concurrent.MyQueue
 * @author: gallenzhang
 * @createDate: 2021/8/18
 */
public class MyQueue {

    private final static int MAX_SIZE = 100;
    private LinkedList<String> queue = new LinkedList<String>();

    public synchronized void offer(String element) {
        try {
            if (queue.size() == MAX_SIZE) {
                //一个线程只要执行到这一步，说明已经获取到了一把锁。
                //让线程进入一个等待的状态，释放掉锁
                wait();
            }
            queue.addLast(element);
            notifyAll();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized String take() {
        //别的线程就可以在这里从去队列里take数据
        String element = null;
        try {
            if (queue.size() == 0) {
                //将这个锁释放掉，陷入等待中，等待别的线程在队列里放入数据
                wait();
            }

            element = queue.removeFirst();

            //唤醒当前在等待这个锁的那些线程
            notifyAll();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return element;
    }

}
