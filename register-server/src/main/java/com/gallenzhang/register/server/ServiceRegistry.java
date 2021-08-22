package com.gallenzhang.register.server;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 服务注册表
 * @className: com.gallenzhang.register.server.ServiceRegistry
 * @author: gallenzhang
 * @createDate: 2021/8/19
 */
public class ServiceRegistry {

    /**
     * 注册表是一个单例
     */
    private static ServiceRegistry instance = new ServiceRegistry();

    private ServiceRegistry() {

    }

    /**
     * 核心的内存数据结构：注册表
     * <p>
     * Map：key是服务名称，value是这个服务的所有的服务实例
     * <p>
     * Map<String,ServiceInstance>: key是服务实例id，value是服务实例的信息
     */
    private Map<String, Map<String, ServiceInstance>> registry =
            new HashMap<String, Map<String, ServiceInstance>>();

    /**
     * 服务注册
     *
     * @param serviceInstance
     */
    public synchronized void register(ServiceInstance serviceInstance) {
        Map<String, ServiceInstance> serviceInstanceMap = registry.get(serviceInstance.getServiceName());
        if (serviceInstanceMap == null) {
            serviceInstanceMap = new HashMap<String, ServiceInstance>();
            registry.put(serviceInstance.getServiceName(), serviceInstanceMap);
        }

        serviceInstanceMap.put(serviceInstance.getServiceInstanceId(), serviceInstance);

        System.out.println("服务实例完成注册......[" + serviceInstance + "]");
        System.out.println("注册表：" + registry);
    }

    /**
     * 获取服务实例
     *
     * @param serviceName
     * @param serviceInstanceId
     * @return
     */
    public synchronized ServiceInstance getServiceInstance(String serviceName, String serviceInstanceId) {
        Map<String, ServiceInstance> serviceInstanceMap = registry.get(serviceName);
        return serviceInstanceMap.get(serviceInstanceId);
    }

    /**
     * 获取整个注册表
     *
     * @return
     */
    public synchronized Map<String, Map<String, ServiceInstance>> getRegistry() {
        return registry;
    }

    /**
     * 从注册表删除一个服务实例
     *
     * @param serviceName
     * @param serviceInstanceId
     */
    public synchronized void remove(String serviceName, String serviceInstanceId) {
        System.out.println("服务实例从注册表中摘除 [" + serviceName + ", " + serviceInstanceId + "] ");
        Map<String, ServiceInstance> serviceInstanceMap = registry.get(serviceName);
        serviceInstanceMap.remove(serviceInstanceId);
    }

    /**
     * 获取服务注册表的单例实例
     *
     * @return
     */
    public static ServiceRegistry getInstance() {
        return instance;
    }
}
