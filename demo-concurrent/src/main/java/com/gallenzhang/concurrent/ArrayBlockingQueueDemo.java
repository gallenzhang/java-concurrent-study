package com.gallenzhang.concurrent;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @description:
 * @className: com.gallenzhang.concurrent.ArrayBlockingQueueDemo
 * @author: gallenzhang
 * @createDate: 2021/9/13
 */
public class ArrayBlockingQueueDemo {

    public static void main(String[] args) throws InterruptedException {
        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue(10);
        queue.put("张三");
        System.out.println(queue.take());
        System.out.println(queue.size());
        System.out.println(queue.iterator());
    }
}
