package com.gallenzhang.register.server;

import java.util.Map;

/**
 * @description: 这个controller是负责接收register-client发送过来的请求的
 * 在springcloud eureka中用的组件是jersey
 * jersey在国外很常用的一个restful框架，可以接受http请求
 * @className: com.gallenzhang.register.server.RegisterServerController
 * @author: gallenzhang
 * @createDate: 2021/8/19
 */
public class RegisterServerController {

    private ServiceRegistry registry = ServiceRegistry.getInstance();


    /**
     * 服务注册
     *
     * @param registerRequest
     * @return
     */
    public RegisterResponse register(RegisterRequest registerRequest) {
        RegisterResponse registerResponse = new RegisterResponse();

        try {
            //在注册表中加入这个服务实例
            ServiceInstance serviceInstance = new ServiceInstance();
            serviceInstance.setHostName(registerRequest.getHostName());
            serviceInstance.setIp(registerRequest.getIp());
            serviceInstance.setPort(registerRequest.getPort());
            serviceInstance.setServiceInstanceId(registerRequest.getServiceInstanceId());
            serviceInstance.setServiceName(registerRequest.getServiceName());

            registry.register(serviceInstance);

            //更新自我保护机制的阈值
            synchronized (SelfProtectionPolicy.class) {
                SelfProtectionPolicy selfProtectionPolicy = SelfProtectionPolicy.getInstance();
                selfProtectionPolicy.setExpectedHeartbeatRate(
                        selfProtectionPolicy.getExpectedHeartbeatRate() + 2);
                selfProtectionPolicy.setExpectedHeartbeatThreshold(
                        (long) (selfProtectionPolicy.getExpectedHeartbeatRate() * 0.85));
            }

            registerResponse.setStatus(RegisterResponse.SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            registerResponse.setStatus(RegisterResponse.FAILURE);
        }

        return registerResponse;
    }

    /**
     * 发送心跳
     *
     * @param heartbeatRequest
     * @return
     */
    public HeartbeatResponse heartbeat(HeartbeatRequest heartbeatRequest) {
        HeartbeatResponse heartbeatResponse = new HeartbeatResponse();

        try {
            //对服务实例进行续约
            ServiceInstance serviceInstance = registry.getServiceInstance(
                    heartbeatRequest.getServiceName(), heartbeatRequest.getServiceInstanceId());
            serviceInstance.renew();

            //记录下每分钟的心跳的次数
            HeartbeatMessuredRate heartbeatMessuredRate = HeartbeatMessuredRate.getInstance();
            heartbeatMessuredRate.increment();

            heartbeatResponse.setStatus(HeartbeatResponse.SUCCESS);

        } catch (Exception e) {
            e.printStackTrace();
            heartbeatResponse.setStatus(HeartbeatResponse.FAILURE);
        }

        return heartbeatResponse;
    }


    /**
     * 拉取服务注册表
     *
     * @return
     */
    public Map<String, Map<String, ServiceInstance>> fetchServiceRegistry() {
        return registry.getRegistry();
    }

    /**
     * 服务下线
     *
     * @param serviceName
     * @param serviceInstanceId
     */
    public void cancel(String serviceName, String serviceInstanceId) {
        //从服务注册表中摘除实例
        registry.remove(serviceName, serviceInstanceId);

        //更新自我保护机制的阈值
        synchronized (SelfProtectionPolicy.class) {
            SelfProtectionPolicy selfProtectionPolicy = SelfProtectionPolicy.getInstance();
            selfProtectionPolicy.setExpectedHeartbeatRate(
                    selfProtectionPolicy.getExpectedHeartbeatRate() + 2);
            selfProtectionPolicy.setExpectedHeartbeatThreshold(
                    (long) (selfProtectionPolicy.getExpectedHeartbeatRate() * 0.85));
        }
    }
}
