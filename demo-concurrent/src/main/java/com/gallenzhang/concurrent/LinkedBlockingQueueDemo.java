package com.gallenzhang.concurrent;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @description:
 * @className: com.gallenzhang.concurrent.LinkedBlockingQueueDemo
 * @author: gallenzhang
 * @createDate: 2021/9/12
 */
public class LinkedBlockingQueueDemo {

    public static void main(String[] args) throws InterruptedException {
        LinkedBlockingQueue queue = new LinkedBlockingQueue(10);
        queue.put("张三");
        queue.put("李四");
        System.out.println(queue.take());
        System.out.println(queue.size());
    }
}
