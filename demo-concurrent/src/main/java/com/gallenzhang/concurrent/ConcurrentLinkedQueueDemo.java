package com.gallenzhang.concurrent;

import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @description:
 * @className: com.gallenzhang.concurrent.ConcurrentLInkedQueueDemo
 * @author: gallenzhang
 * @createDate: 2021/9/11
 */
public class ConcurrentLinkedQueueDemo {
    public static void main(String[] args) {
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        queue.offer("张三");
        queue.offer("李四");
        queue.offer("王五");
        System.out.println(queue.poll());
        System.out.println(queue.peek());
        queue.remove("李四");
        System.out.println(queue);
        System.out.println(queue.size());
        System.out.println(queue.contains("王五"));

        Iterator<String> iterator = queue.iterator();
        while (iterator.hasNext()){
            System.out.println(iterator.next());
        }
    }
}