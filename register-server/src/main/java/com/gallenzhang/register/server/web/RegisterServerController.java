package com.gallenzhang.register.server.web;


import com.gallenzhang.register.server.cluster.PeersReplicateBatch;
import com.gallenzhang.register.server.cluster.PeersReplicator;
import com.gallenzhang.register.server.core.*;

/**
 * @description: 这个controller是负责接收register-client发送过来的请求的
 * 在springcloud eureka中用的组件是jersey
 * jersey在国外很常用的一个restful框架，可以接受http请求
 * @className: com.gallenzhang.register.server.web.RegisterServerController
 * @author: gallenzhang
 * @createDate: 2021/8/19
 */
public class RegisterServerController {

    /**
     * 服务注册表
     */
    private ServiceRegistry registry = ServiceRegistry.getInstance();

    /**
     * 服务注册表的缓存
     */
    private ServiceRegistryCache registryCache = ServiceRegistryCache.getInstance();

    /**
     * 集群同步组件
     */
    private PeersReplicator peersReplicator = PeersReplicator.getInstance();

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

            //过期掉注册表缓存
            registryCache.invalidate();

            //进行集群同步
            peersReplicator.replicateRegister(registerRequest);

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

            if (serviceInstance != null) {
                serviceInstance.renew();
            }

            //记录下每分钟的心跳的次数
            HeartbeatCounter heartbeatCounter = HeartbeatCounter.getInstance();
            heartbeatCounter.increment();

            heartbeatResponse.setStatus(HeartbeatResponse.SUCCESS);

            //进行集群同步
            peersReplicator.replicateHeartbeat(heartbeatRequest);

        } catch (Exception e) {
            e.printStackTrace();
            heartbeatResponse.setStatus(HeartbeatResponse.FAILURE);
        }

        return heartbeatResponse;
    }


    /**
     * 拉取全量服务注册表
     *
     * @return
     */
    public Applications fetchFullServiceRegistry() {
        return (Applications) registryCache.get(ServiceRegistryCache.CacheKey.FULL_SERVICE_REGISTRY);

    }

    /**
     * 拉取增量服务注册表
     *
     * @return
     */
    public DeltaRegistry fetchDeltaServiceRegistry() {
        return (DeltaRegistry) registryCache.get(ServiceRegistryCache.CacheKey.DELTA_SERVICE_REGISTRY);
    }

    /**
     * 服务下线
     *
     * @param cancelRequest
     */
    public void cancel(CancelRequest cancelRequest) {
        //从服务注册表中摘除实例
        registry.remove(cancelRequest.getServiceName(), cancelRequest.getServiceInstanceId());

        //更新自我保护机制的阈值
        synchronized (SelfProtectionPolicy.class) {
            SelfProtectionPolicy selfProtectionPolicy = SelfProtectionPolicy.getInstance();
            selfProtectionPolicy.setExpectedHeartbeatRate(
                    selfProtectionPolicy.getExpectedHeartbeatRate() + 2);
            selfProtectionPolicy.setExpectedHeartbeatThreshold(
                    (long) (selfProtectionPolicy.getExpectedHeartbeatRate() * 0.85));
        }

        //过期掉注册表缓存
        registryCache.invalidate();

        //进行集群同步
        peersReplicator.replicateCancel(cancelRequest);
    }

    /**
     * 同步batch数据
     *
     * @param batch
     */
    public void replicateBatch(PeersReplicateBatch batch) {
        for (AbstractRequest request : batch.getRequests()) {
            if (request.getType().equals(AbstractRequest.REGISTER_REQUEST)) {
                register((RegisterRequest) request);
            }else if(request.getType().equals(AbstractRequest.CANCEL_REQUEST)){
                cancel((CancelRequest) request);
            }else if(request.getType().equals(AbstractRequest.HEARTBEAT_REQUEST)){
                heartbeat((HeartbeatRequest) request);
            }
        }
    }
}
