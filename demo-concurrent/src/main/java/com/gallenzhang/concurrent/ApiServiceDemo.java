package com.gallenzhang.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CyclicBarrier;

/**
 * @description:
 * @className: com.gallenzhang.concurrent.ApiServiceDemo
 * @author: gallenzhang
 * @createDate: 2021/9/7
 */
public class ApiServiceDemo {

    public Map<String, Object> queryOrders() throws Exception {
        final List<Object> results = new ArrayList<>();
        final Map<String, Object> map = new ConcurrentHashMap<>();

        CyclicBarrier barrier = new CyclicBarrier(3, new Runnable() {
            @Override
            public void run() {
                map.put("price", results.get(0));
                map.put("order", results.get(1));
                map.put("stats", results.get(2));
            }
        });

        new Thread() {

            //每个线程都是一个独立的类，包含了自己的一份结果数据

            @Override
            public void run() {
                try {
                    System.out.println("请求价格服务......");
                    Thread.sleep(1000L);
                    results.add(new Object());
                    barrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        new Thread() {

            //每个线程都是一个独立的类，包含了自己的一份结果数据

            @Override
            public void run() {
                try {
                    System.out.println("请求订单服务......");
                    Thread.sleep(1000L);
                    results.add(new Object());
                    barrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        new Thread() {

            //每个线程都是一个独立的类，包含了自己的一份结果数据

            @Override
            public void run() {
                try {
                    System.out.println("请求订单统计服务......");
                    Thread.sleep(1000L);
                    results.add(new Object());
                    barrier.await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        while (map.size() < 3) {
            Thread.sleep(100);
        }

        return map;
    }
}
