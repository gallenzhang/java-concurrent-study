package com.gallenzhang.concurrent;

import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @className: com.gallenzhang.concurrent.VolatileDemo
 * @author: gallenzhang
 * @createDate: 2021/8/18
 */
public class VolatileDemo {

    static volatile int flag = 0;

    public static void main(String[] args) {
        new Thread() {

            @Override
            public void run() {
                int localFlag = flag;
                while (true) {
                    if (localFlag != flag) {
                        System.out.println("读取到了修改后的标志位：" + flag);
                        localFlag = flag;
                    }
                }
            }
        }.start();

        new Thread() {
            @Override
            public void run() {
                int localFlag = flag;
                while (true) {
                    System.out.println("标志位被修改为了：" + ++localFlag);
                    flag = localFlag;

                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }
}
