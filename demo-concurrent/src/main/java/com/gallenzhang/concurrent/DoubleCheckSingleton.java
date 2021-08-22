package com.gallenzhang.concurrent;

/**
 * @description:
 * @className: com.gallenzhang.concurrent.DoubleCheckSingleton
 * @copyRight: www.shopee.com by SZDC-BankingGroup
 * @author: xiaoqiang.zhang
 * @createDate: 2021/8/21
 */
public class DoubleCheckSingleton {

    private static volatile DoubleCheckSingleton instance;

    private DoubleCheckSingleton() {

    }

    public static DoubleCheckSingleton getInstance() {
        if (instance == null) {
            //多个线程会卡在这儿
            synchronized (DoubleCheckSingleton.class) {
                //有一个线程先进来
                //第二个线程进来了，此时如果没有这个double check的判断的话，然后就会导致他再次创建了一次实例
                if (instance == null) {
                    //创建一个单例
                    instance = new DoubleCheckSingleton();
                }
            }
            //第一个线程出来了
        }
        return instance;
    }
}
