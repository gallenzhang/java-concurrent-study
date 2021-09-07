package com.gallenzhang.dfs.datanode.server;

import java.util.concurrent.CountDownLatch;

/**
 * @description: 负责跟一组NameNode中的某一个进行通信的线程组件
 * @className: com.gallenzhang.dfs.datanode.server.NameNodeServiceActor
 * @author: gallenzhang
 * @createDate: 2021/9/7
 */
public class NameNodeServiceActor {

    /**
     * 向自己负责通信的那个NameNode进行注册
     *
     * @param latch
     */
    public void register(CountDownLatch latch) {
        Thread registerThread = new RegisterThread(latch);
        registerThread.start();
    }

    /**
     * 负责注册的线程
     */
    class RegisterThread extends Thread {

        CountDownLatch latch;

        public RegisterThread(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void run() {
            try {
                //发送rpc接口调用请求到NameNode去进行注册
                System.out.println("发送请求到NameNode进行注册......");
                Thread.sleep(1000);
                latch.countDown();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
