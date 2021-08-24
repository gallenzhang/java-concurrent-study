package com.gallenzhang.register.client;


import java.util.Map;
import java.util.UUID;

/**
 * @description: 在服务上被创建和启动，负责跟register-server进行通信
 * @className: com.gallenzhang.register.client.RegisterClient
 * @copyRight: www.shopee.com by SZDC-BankingGroup
 * @author: xiaoqiang.zhang
 * @createDate: 2021/8/20
 */
public class RegisterClient {

    public static final String SERVICE_NAME = "order-service";
    public static final String IP = "192.168.1.100";
    public static final Integer PORT = 8012;
    public static final String HOST_NAME = "order-service01";
    public static final Long HEARTBEAT_INTERVAL = 30 * 1000L;

    /**
     * 服务实例id
     */
    private String serviceInstanceId;

    /**
     * http通信组件
     */
    private HttpSender httpSender;

    /**
     * 心跳线程
     */
    private HeartbeatWorker heartbeatWorker;

    /**
     * 服务实例是否在运行
     */
    private volatile Boolean isRunning;

    /**
     * 客户端缓存的注册表
     */
    private CachedServiceRegistry registry;

    public RegisterClient() {
        this.serviceInstanceId = UUID.randomUUID().toString().replace("-", "");
        this.httpSender = new HttpSender();
        this.heartbeatWorker = new HeartbeatWorker();
        this.isRunning = true;
        this.registry = new CachedServiceRegistry(this, httpSender);
    }

    /**
     * 启动RegisterClient组件
     */
    public void start() {
        try {
            //一旦启动了这个组件之后，他就负责在服务上干两个事情
            //第一个事情，就是开启了一个线程向register-server去发送请求，注册这个服务
            //第二个事情，就是在注册成功之后，开启另外一个线程去发送心跳
            RegisterWorker registerWorker = new RegisterWorker();
            registerWorker.start();
            registerWorker.join();

            //启动心跳线程，定时发送心跳信息
            heartbeatWorker.start();

            //初始化客户端缓存的服务注册表组件
            this.registry.initialize();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止RegisterClient组件
     */
    public void shutdown() {
        this.isRunning = false;
        this.heartbeatWorker.interrupt();
        this.registry.destroy();
        this.httpSender.cancel(SERVICE_NAME, serviceInstanceId);
    }

    /**
     * 心跳线程
     */
    private class HeartbeatWorker extends Thread {
        @Override
        public void run() {
            //如果说注册成功了，就进入while true 死循环
            HeartbeatRequest heartbeatRequest = new HeartbeatRequest();
            heartbeatRequest.setServiceName(SERVICE_NAME);
            heartbeatRequest.setServiceInstanceId(serviceInstanceId);

            HeartbeatResponse heartbeatResponse = null;

            while (isRunning) {
                try {
                    heartbeatResponse = httpSender.heartbeat(heartbeatRequest);
                    System.out.println("心跳的结果为：" + heartbeatResponse.getStatus() + "...");
                    Thread.sleep(HEARTBEAT_INTERVAL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 服务注册线程
     */
    private class RegisterWorker extends Thread {
        @Override
        public void run() {
            //应该是获取当前机器的信息，包括当前机器的ip地址、hostname、以及配置这个服务监听的端口号，可以从配置文件中获取
            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setServiceName(SERVICE_NAME);
            registerRequest.setIp(IP);
            registerRequest.setPort(PORT);
            registerRequest.setServiceInstanceId(serviceInstanceId);
            registerRequest.setHostName(HOST_NAME);

            RegisterResponse registerResponse = httpSender.register(registerRequest);

            System.out.println("服务注册的结果是：" + registerResponse.getStatus() + "...");
        }
    }

    /**
     * 返回RegisterClient是否正在运行
     *
     * @return
     */
    public Boolean isRunning() {
        return isRunning;
    }

    /**
     * 获取服务注册表
     *
     * @return
     */
    public Map<String, Map<String, ServiceInstance>> getRegistry() {
        return registry.getRegistry();
    }
}
