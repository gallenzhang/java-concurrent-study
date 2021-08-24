package com.gallenzhang.register.server;

import java.util.LinkedList;

/**
 * @description: 增量注册表
 * @className: com.gallenzhang.register.server.DeltaRegistry
 * @copyRight: www.shopee.com by SZDC-BankingGroup
 * @author: xiaoqiang.zhang
 * @createDate: 2021/8/24
 */
public class DeltaRegistry {

    private LinkedList<ServiceRegistry.RecentlyChangedServiceInstance> recentlyChangedQueue;
    private Long serviceInstanceTotalCount;

    public DeltaRegistry(LinkedList<ServiceRegistry.RecentlyChangedServiceInstance> recentlyChangedQueue, Long serviceInstanceTotalCount) {
        this.recentlyChangedQueue = recentlyChangedQueue;
        this.serviceInstanceTotalCount = serviceInstanceTotalCount;
    }

    public LinkedList<ServiceRegistry.RecentlyChangedServiceInstance> getRecentlyChangedQueue() {
        return recentlyChangedQueue;
    }

    public void setRecentlyChangedQueue(LinkedList<ServiceRegistry.RecentlyChangedServiceInstance> recentlyChangedQueue) {
        this.recentlyChangedQueue = recentlyChangedQueue;
    }

    public Long getServiceInstanceTotalCount() {
        return serviceInstanceTotalCount;
    }

    public void setServiceInstanceTotalCount(Long serviceInstanceTotalCount) {
        this.serviceInstanceTotalCount = serviceInstanceTotalCount;
    }
}
