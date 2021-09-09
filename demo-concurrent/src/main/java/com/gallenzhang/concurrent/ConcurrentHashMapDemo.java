package com.gallenzhang.concurrent;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @description:
 * @className: com.gallenzhang.concurrent.ConcurrentHashMapDemo
 * @author: gallenzhang
 * @createDate: 2021/9/8
 */
public class ConcurrentHashMapDemo {

    public static void main(String[] args) {
        ConcurrentHashMap<String,String> map = new ConcurrentHashMap<>();
        //数组每个元素的分段加锁，保证写数据的线程安全性，数据不会错乱
        map.put("k1","v1");
        //依赖于volatile读操作，保证你读到的是最新的数据结果，不加锁
        System.out.println(map.get("k1"));
        System.out.println(map.size());
    }
}
