package com.gallenzhang.register.client;

/**
 * @description: RegisterClient启动方法
 * @className: com.gallenzhang.register.client.TestMain
 * @copyRight: www.shopee.com by SZDC-BankingGroup
 * @author: xiaoqiang.zhang
 * @createDate: 2021/8/20
 */
public class RegisterClientMain {

    public static void main(String[] args) throws InterruptedException {
        RegisterClient registerClient = new RegisterClient();
        registerClient.start();

        Thread.sleep(50 * 1000);

        System.out.println("客户端缓存注册表：" + registerClient.getRegistry());

        registerClient.shutdown();
    }
}
