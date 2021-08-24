package com.gallenzhang.register.client;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @description: 服务注册中心的客户端缓存的一个服务注册表
 * @className: com.gallenzhang.register.client.ClientCachedServiceRegistry
 * @copyRight: www.shopee.com by SZDC-BankingGroup
 * @author: xiaoqiang.zhang
 * @createDate: 2021/8/20
 */
public class CachedServiceRegistry {

    /**
     * 服务注册表拉取间隔时间
     */
    private static final Long SERVICE_REGISTRY_FETCH_INTERVAL = 30 * 1000L;

    /**
     * 客户端缓存的服务注册表
     */
    private Map<String, Map<String, ServiceInstance>> registry = new HashMap<String, Map<String, ServiceInstance>>();


    /**
     * 负责定时拉取增量注册表到客户端进行缓存的后台线程
     */
    private FetchDeltaRegistryWorker fetchDeltaRegistryWorker;

    /**
     * registerClient
     */
    private RegisterClient registerClient;

    /**
     * http通信组件
     */
    private HttpSender httpSender;

    /**
     * 构造函数
     *
     * @param registerClient
     * @param httpSender
     */
    public CachedServiceRegistry(RegisterClient registerClient, HttpSender httpSender) {
        this.fetchDeltaRegistryWorker = new FetchDeltaRegistryWorker();
        this.registerClient = registerClient;
        this.httpSender = httpSender;
    }

    /**
     * 初始化
     */
    public void initialize() {
        //启动全量拉取注册表的线程
        FetchFullRegistryWorker fetchFullRegistryWorker = new FetchFullRegistryWorker();
        fetchFullRegistryWorker.start();

        //启动增量拉取注册表的线程
        this.fetchDeltaRegistryWorker.start();
    }

    /**
     * 销毁这个组件
     */
    public void destroy() {
        this.fetchDeltaRegistryWorker.interrupt();
    }

    /**
     * 全量拉取注册表的后台线程
     */
    private class FetchFullRegistryWorker extends Thread {

        @Override
        public void run() {
            //拉取全量注册表
            registry = httpSender.fetchServiceRegistry();
        }
    }

    /**
     * 增量拉取注册表的后台线程
     */
    private class FetchDeltaRegistryWorker extends Thread {

        @Override
        public void run() {
            while (registerClient.isRunning()) {
                try {
                    Thread.sleep(SERVICE_REGISTRY_FETCH_INTERVAL);

                    //拉取回来的是最近3分钟变化的服务实例
                    LinkedList<RecentlyChangedServiceInstance> deltaServiceRegistry = httpSender.fetchDeltaServiceRegistry();

                    //一类是注册，一类是删除。
                    //如果是注册的话，就判断一下这个服务实例是否在这个本地缓存的注册表中。如果不在的话，就放到本地缓存注册表中去
                    //如果是删除的话，判断一下服务实例存在,就给删除掉

                    //这里会大量的修改本地缓存的注册表，所以这里需要加锁
                    synchronized (registry) {
                        mergeDeltaRegistry(deltaServiceRegistry);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 合并增量注册表到本地缓存注册表里去
     *
     * @param deltaServiceRegistry
     */
    private void mergeDeltaRegistry(LinkedList<RecentlyChangedServiceInstance> deltaServiceRegistry) {
        for (RecentlyChangedServiceInstance recentlyChangedItem : deltaServiceRegistry) {
            //如果是注册操作的话
            if (ServiceInstanceOperation.REGISTER.equals(recentlyChangedItem.serviceInstanceOperation)) {
                Map<String, ServiceInstance> serviceInstanceMap = registry.get(recentlyChangedItem.serviceInstance.getServiceName());
                if (serviceInstanceMap == null) {
                    serviceInstanceMap = new HashMap<String, ServiceInstance>();
                    registry.put(recentlyChangedItem.serviceInstance.getServiceName(), serviceInstanceMap);
                }

                ServiceInstance serviceInstance = serviceInstanceMap.get(recentlyChangedItem.serviceInstance.getServiceInstanceId());
                if (serviceInstance == null) {
                    serviceInstanceMap.put(recentlyChangedItem.serviceInstance.getServiceInstanceId(), recentlyChangedItem.serviceInstance);
                }

            } else if (ServiceInstanceOperation.REMOVE.equals(recentlyChangedItem.serviceInstanceOperation)) {
                //如果是删除操作
                Map<String, ServiceInstance> serviceInstanceMap = registry.get(recentlyChangedItem.serviceInstance.getServiceName());
                if (serviceInstanceMap != null) {
                    serviceInstanceMap.remove(recentlyChangedItem.serviceInstance.getServiceInstanceId());
                }
            }
        }
    }

    /**
     * 获取服务注册表
     *
     * @return
     */
    public Map<String, Map<String, ServiceInstance>> getRegistry() {
        return registry;
    }

    /**
     * 最近变化的服务实例
     */
    static class RecentlyChangedServiceInstance {
        /**
         * 服务实例
         */
        ServiceInstance serviceInstance;

        /**
         * 发生变更的时间戳
         */
        Long changedTimestamp;

        /**
         * 变更操作
         */
        String serviceInstanceOperation;

        public RecentlyChangedServiceInstance(ServiceInstance serviceInstance, Long changedTimestamp, String serviceInstanceOperation) {
            this.serviceInstance = serviceInstance;
            this.changedTimestamp = changedTimestamp;
            this.serviceInstanceOperation = serviceInstanceOperation;
        }
    }

    /**
     * 服务实例操作
     */
    class ServiceInstanceOperation {
        /**
         * 注册
         */
        public static final String REGISTER = "register";

        /**
         * 删除
         */
        public static final String REMOVE = "remove";
    }
}
