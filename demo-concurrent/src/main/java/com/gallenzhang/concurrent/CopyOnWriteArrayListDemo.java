package com.gallenzhang.concurrent;


import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @description:
 * @className: com.gallenzhang.concurrent.CopyOnWriteArrayListDemo
 * @author: gallenzhang
 * @createDate: 2021/9/10
 */
public class CopyOnWriteArrayListDemo {

    static List<String> list = new CopyOnWriteArrayList<>();

    public static void main(String[] args) {
        list.add("张三");
        list.set(0, "李四");
        list.remove(0);
        System.out.println(list);
    }
}
