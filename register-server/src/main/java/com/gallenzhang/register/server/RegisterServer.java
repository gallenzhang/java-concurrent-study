package com.gallenzhang.register.server;

import com.gallenzhang.register.server.core.ServiceAliveMonitor;
import com.gallenzhang.register.server.web.HeartbeatRequest;
import com.gallenzhang.register.server.web.RegisterRequest;
import com.gallenzhang.register.server.web.RegisterServerController;

import java.util.UUID;

/**
 * @description: 代表了服务注册中心
 * @className: com.gallenzhang.register.server.RegisterServer
 * @author: gallenzhang
 * @createDate: 2021/8/19
 */
public class RegisterServer {

    public static void main(String[] args) throws InterruptedException {

        RegisterServerController controller = new RegisterServerController();

        String serviceInstanceId = UUID.randomUUID().toString().replace("-", "");

        //模拟发起一个服务注册的请求
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setHostName("order-service-01");
        registerRequest.setIp("192.168.1.100");
        registerRequest.setPort(9012);
        registerRequest.setServiceInstanceId(serviceInstanceId);
        registerRequest.setServiceName("order-service");

        controller.register(registerRequest);


        //模拟进行一次心跳，完成续约
        HeartbeatRequest heartbeatRequest = new HeartbeatRequest();
        heartbeatRequest.setServiceName("order-service");
        heartbeatRequest.setServiceInstanceId(serviceInstanceId);

        controller.heartbeat(heartbeatRequest);

        //开启一个后台线程，检查微服务的存活状态
        ServiceAliveMonitor serviceAliveMonitor = new ServiceAliveMonitor();
        serviceAliveMonitor.start();

        /**
         * 一般来说像这种register-server，当然不会只有一个main线程作为工作线程
         * 一般来说是一个web工程，部署在一个web服务器里面
         * 他最核心的工作线程，就是专门用来接受和处理register-client发送过来的请求
         * 正常来说，只要有工作线程，是不会随便退出的
         * 当然如果说工作线程都停止了，那么daemon线程就会跟着jvm 进程一块退出
         *
         * 这个项目暂时是没有网络这块东西，就在main方法里加了一块while true，让main线程，也就是这个工作线程不要退出
         * 这样就可以保证这个服务一直运行，daemon线程也会正常的工作
         *
         * 如果说register-server中的核心工作线程，就是用于接收和处理请求的工作线程，他们都结束了，停止工作了，销毁了。
         * 此时register-server里就只剩下一个daemon线程了，难道还要阻止jvm进程退出吗？
         * 此时当然是应该跟着jvm进程一块退出了
         */
        while (true) {
            Thread.sleep(30 * 1000);
        }

    }
}
