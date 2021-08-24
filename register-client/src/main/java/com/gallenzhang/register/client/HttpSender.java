package com.gallenzhang.register.client;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @description: 负责发送各种http请求的组件
 * @className: com.gallenzhang.register.client.HttpSender
 * @copyRight: www.shopee.com by SZDC-BankingGroup
 * @author: xiaoqiang.zhang
 * @createDate: 2021/8/20
 */
public class HttpSender {

    /**
     * 发送注册请求
     *
     * @param request
     * @return
     */
    public RegisterResponse register(RegisterRequest request) {
        //实际上会基于类似HttpClient这种开源的网络包
        //可以构造一个请求，里面放入这个服务实例的信息，比如服务名称、ip地址、端口号，然后通过这个请求发送出去
        System.out.println("服务实例[" + request + "], 发送请求进行注册...");

        //收到register-server响应之后，封装一个Response对象
        RegisterResponse response = new RegisterResponse();
        response.setStatus(RegisterResponse.SUCCESS);

        return response;
    }

    /**
     * 发送心跳请求
     *
     * @param request
     * @return
     */
    public HeartbeatResponse heartbeat(HeartbeatRequest request) {
        System.out.println("服务实例[" + request + "], 发送请求进行心跳...");

        HeartbeatResponse response = new HeartbeatResponse();
        response.setStatus(RegisterResponse.SUCCESS);

        return response;
    }

    /**
     * 全量拉取服务注册表
     *
     * @return
     */
    public Map<String, Map<String, ServiceInstance>> fetchServiceRegistry() {
        Map<String, Map<String, ServiceInstance>> registry = new HashMap<String, Map<String, ServiceInstance>>();

        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setHostName("finance-service-01");
        serviceInstance.setIp("192.168.1.101");
        serviceInstance.setPort(9012);
        serviceInstance.setServiceInstanceId("FINANCE-SERVICE-192.169.1.101:9012");
        serviceInstance.setServiceName("FINANCE-SERVICE");

        Map<String, ServiceInstance> serviceInstanceMap = new HashMap<String, ServiceInstance>();
        serviceInstanceMap.put("FINANCE-SERVICE-192.169.1.101:9012", serviceInstance);

        registry.put("FINANCE-SERVICE", serviceInstanceMap);

        System.out.println("拉取全量注册表：" + registry);

        return registry;
    }

    /**
     * 增量拉取服务注册表
     *
     * @return
     */
    public LinkedList<CachedServiceRegistry.RecentlyChangedServiceInstance> fetchDeltaServiceRegistry() {
        LinkedList<CachedServiceRegistry.RecentlyChangedServiceInstance> recentlyChangedQueue =
                new LinkedList<CachedServiceRegistry.RecentlyChangedServiceInstance>();

        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setHostName("order-service-01");
        serviceInstance.setIp("192.168.1.102");
        serviceInstance.setPort(9000);
        serviceInstance.setServiceInstanceId("ORDER-SERVICE-192.169.1.102:9000");
        serviceInstance.setServiceName("ORDER-SERVICE");

        CachedServiceRegistry.RecentlyChangedServiceInstance changedServiceInstance = new CachedServiceRegistry.RecentlyChangedServiceInstance(
                serviceInstance, System.currentTimeMillis(), "register");
        recentlyChangedQueue.add(changedServiceInstance);

        System.out.println("拉取增量注册表：" + recentlyChangedQueue);

        return recentlyChangedQueue;
    }

    /**
     * 服务下线
     *
     * @param serviceName
     * @param serviceInstanceId
     */
    public void cancel(String serviceName, String serviceInstanceId) {
        System.out.println("服务实例下线[" + serviceName + ", " + serviceInstanceId + "]");
    }

}
