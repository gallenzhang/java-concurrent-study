package com.gallenzhang.register.server;

import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @description: 服务注册表
 * @className: com.gallenzhang.register.server.ServiceRegistry
 * @author: gallenzhang
 * @createDate: 2021/8/19
 */
public class ServiceRegistry {

    public static final Long RECENTLY_CHANGED_ITEM_CHECK_INTERVAL = 3000L;
    public static final Long RECENTLY_CHANGED_ITEM_EXPIRED = 3 * 60 * 1000L;


    /**
     * 注册表是一个单例
     */
    private static ServiceRegistry instance = new ServiceRegistry();


    /**
     * 核心的内存数据结构：注册表
     * <p>
     * Map：key是服务名称，value是这个服务的所有的服务实例
     * <p>
     * Map<String,ServiceInstance>: key是服务实例id，value是服务实例的信息
     */
    private Map<String, Map<String, ServiceInstance>> registry =
            new ConcurrentHashMap<>();

    /**
     * 最近变更的服务实例的队列
     */
    private LinkedList<RecentlyChangedServiceInstance> recentlyChangedQueue = new LinkedList<>();

    /**
     * 服务注册表的锁
     */
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    /**
     * 构造函数
     */
    private ServiceRegistry() {
        //启动后台线程监控最近变更的队列
        RecentlyChangedQueueMonitor recentlyChangedQueueMonitor = new RecentlyChangedQueueMonitor();
        recentlyChangedQueueMonitor.setDaemon(true);
        recentlyChangedQueueMonitor.start();
    }

    /**
     * 加读锁
     */
    public void readLock() {
        this.readLock.lock();
    }

    /**
     * 释放读锁
     */
    public void readUnlock() {
        this.readLock.unlock();
    }

    /**
     * 加写锁
     */
    public void writeLock() {
        this.writeLock.lock();
    }

    /**
     * 释放写锁
     */
    public void writeUnlock() {
        this.writeLock.unlock();
    }


    /**
     * 服务注册
     *
     * @param serviceInstance
     */
    public void register(ServiceInstance serviceInstance) {
        try {
            //加写锁
            this.writeLock();

            System.out.println("服务注册开始......[" + serviceInstance + "]");

            //将服务实例放入最近变更的队列中
            RecentlyChangedServiceInstance recentlyChangedServiceInstance = new RecentlyChangedServiceInstance(
                    serviceInstance,
                    System.currentTimeMillis(),
                    ServiceInstanceOperation.REGISTER);
            recentlyChangedQueue.offer(recentlyChangedServiceInstance);

            System.out.println("最近变更队列: " + recentlyChangedQueue);

            //将服务实例放入注册表中
            Map<String, ServiceInstance> serviceInstanceMap = registry.get(serviceInstance.getServiceName());
            if (serviceInstanceMap == null) {
                serviceInstanceMap = new ConcurrentHashMap<>();
                registry.put(serviceInstance.getServiceName(), serviceInstanceMap);
            }
            serviceInstanceMap.put(serviceInstance.getServiceInstanceId(), serviceInstance);

            System.out.println("服务实例完成注册......[" + serviceInstance + "]");
            System.out.println("注册表：" + registry);
        } finally {
            this.writeUnlock();
        }
    }

    /**
     * 获取服务实例
     *
     * @param serviceName
     * @param serviceInstanceId
     * @return
     */
    public ServiceInstance getServiceInstance(String serviceName, String serviceInstanceId) {
        Map<String, ServiceInstance> serviceInstanceMap = registry.get(serviceName);
        return serviceInstanceMap.get(serviceInstanceId);
    }

    /**
     * 获取整个注册表
     *
     * @return
     */
    public Map<String, Map<String, ServiceInstance>> getRegistry() {
        return registry;
    }

    /**
     * 获取最近有变化的注册表
     *
     * @return
     */
    public DeltaRegistry getDeltaRegistry() {
        Long totalCount = 0L;
        for (Map<String, ServiceInstance> serviceInstanceMap : registry.values()) {
            totalCount += serviceInstanceMap.size();
        }

        DeltaRegistry deltaRegistry = new DeltaRegistry(recentlyChangedQueue, totalCount);

        return deltaRegistry;
    }


    /**
     * 从注册表删除一个服务实例
     *
     * @param serviceName
     * @param serviceInstanceId
     */
    public void remove(String serviceName, String serviceInstanceId) {
        try {
            //加写锁
            this.writeLock();

            System.out.println("服务实例从注册表中摘除 [" + serviceName + ", " + serviceInstanceId + "] ");

            //从服务注册表删除服务实例
            Map<String, ServiceInstance> serviceInstanceMap = registry.get(serviceName);
            ServiceInstance serviceInstance = serviceInstanceMap.get(serviceInstanceId);

            //将服务实例放入最近变更的队列中
            RecentlyChangedServiceInstance recentlyChangedServiceInstance = new RecentlyChangedServiceInstance(
                    serviceInstance,
                    System.currentTimeMillis(),
                    ServiceInstanceOperation.REMOVE);
            recentlyChangedQueue.offer(recentlyChangedServiceInstance);

            System.out.println("最近变更队列: " + recentlyChangedQueue);

            //从服务注册表删除服务实例
            serviceInstanceMap.remove(serviceInstanceId);

            System.out.println("注册表：" + registry);
        } finally {
            this.writeUnlock();
        }
    }

    /**
     * 获取服务注册表的单例实例
     *
     * @return
     */
    public static ServiceRegistry getInstance() {
        return instance;
    }

    /**
     * 最近变化的服务实例
     */
    class RecentlyChangedServiceInstance {
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

    /**
     * 最近变更队列的监控线程
     */
    class RecentlyChangedQueueMonitor extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    synchronized (instance) {
                        RecentlyChangedServiceInstance recentlyChangedServiceInstance;
                        Long currentTimestamp = System.currentTimeMillis();
                        while ((recentlyChangedServiceInstance = recentlyChangedQueue.peek()) != null) {
                            //判断如果一个服务实例变更信息已经在队列里存在超过3分钟了，就从队列中移除
                            if (currentTimestamp - recentlyChangedServiceInstance.changedTimestamp >
                                    RECENTLY_CHANGED_ITEM_EXPIRED) {
                                recentlyChangedQueue.pop();
                            }
                        }
                    }

                    Thread.sleep(RECENTLY_CHANGED_ITEM_CHECK_INTERVAL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}