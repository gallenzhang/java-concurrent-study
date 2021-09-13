package com.gallenzhang.register.client;

import java.util.Queue;

/**
 * @description: 增量注册表
 * @className: com.gallenzhang.register.server.DeltaRegistry
 * @copyRight: www.shopee.com by SZDC-BankingGroup
 * @author: xiaoqiang.zhang
 * @createDate: 2021/8/24
 */
public class DeltaRegistry {

    private Queue<CachedServiceRegistry.RecentlyChangedServiceInstance> recentlyChangedQueue;
    private Long serviceInstanceTotalCount;

    public DeltaRegistry(Queue<CachedServiceRegistry.RecentlyChangedServiceInstance> recentlyChangedQueue, Long serviceInstanceTotalCount) {
        this.recentlyChangedQueue = recentlyChangedQueue;
        this.serviceInstanceTotalCount = serviceInstanceTotalCount;
    }

    public Queue<CachedServiceRegistry.RecentlyChangedServiceInstance> getRecentlyChangedQueue() {
        return recentlyChangedQueue;
    }

    public void setRecentlyChangedQueue(Queue<CachedServiceRegistry.RecentlyChangedServiceInstance> recentlyChangedQueue) {
        this.recentlyChangedQueue = recentlyChangedQueue;
    }

    public Long getServiceInstanceTotalCount() {
        return serviceInstanceTotalCount;
    }

    public void setServiceInstanceTotalCount(Long serviceInstanceTotalCount) {
        this.serviceInstanceTotalCount = serviceInstanceTotalCount;
    }
}
