package com.gallenzhang.register.server.core;


import com.gallenzhang.register.server.web.Applications;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.gallenzhang.register.server.core.ServiceRegistryCache.CacheKey.DELTA_SERVICE_REGISTRY;
import static com.gallenzhang.register.server.core.ServiceRegistryCache.CacheKey.FULL_SERVICE_REGISTRY;

/**
 * @description: 服务注册表的缓存
 * @className: com.gallenzhang.register.server.core.ServiceRegistryCache
 * @author: gallenzhang
 * @createDate: 2021/9/1
 */
public class ServiceRegistryCache {

    /**
     * 单例
     */
    private static final ServiceRegistryCache instance = new ServiceRegistryCache();

    /**
     * 缓存数据同步间隔
     */
    private static final Long CACHE_MAP_SYNC_INTERVAL = 30 * 1000L;

    /**
     * 缓存key
     */
    public static class CacheKey {
        /**
         * 全量注册表缓存key
         */
        public static final String FULL_SERVICE_REGISTRY = "full_service_registry";

        /**
         * 增量注册表缓存key
         */
        public static final String DELTA_SERVICE_REGISTRY = "delta_service_registry";
    }

    /**
     * 实际的注册表数据
     */
    private ServiceRegistry registry = ServiceRegistry.getInstance();

    /**
     * 只读缓存
     */
    private Map<String, Object> readOnlyMap = new HashMap<>();

    /**
     * 读写缓存
     */
    private Map<String, Object> readWriteMap = new HashMap<>();

    /**
     * cache map同步后台线程
     */
    private CacheMapSyncDaemon cacheMapSyncDaemon;

    /**
     * 对ReadWriteMap的内部锁
     */
    private Object lock = new Object();

    /**
     * 对readOnlyMap的读写锁
     */
    private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();
    private ReentrantReadWriteLock.WriteLock writeLock = readWriteLock.writeLock();


    /**
     * 构造函数
     */
    public ServiceRegistryCache() {
        //启动缓存数据同步后台线程
        this.cacheMapSyncDaemon = new CacheMapSyncDaemon();
        this.cacheMapSyncDaemon.setDaemon(true);
        this.cacheMapSyncDaemon.start();
    }

    /**
     * 根据缓存key来获取数据
     *
     * @param cacheKey
     * @return
     */
    public Object get(String cacheKey) {
        Object cacheValue = null;
        try {
            readLock.lock();

            cacheValue = readOnlyMap.get(cacheKey);
            if (cacheValue == null) {
                synchronized (lock) {
                    if (readOnlyMap.get(cacheKey) == null) {
                        cacheValue = readWriteMap.get(cacheKey);
                        if (cacheValue == null) {
                            cacheValue = getCacheValue(cacheKey);
                            readWriteMap.put(cacheKey, cacheValue);
                        }
                    }
                    readOnlyMap.put(cacheKey, cacheValue);
                }
            }
        } finally {
            readLock.unlock();
        }
        return cacheValue;
    }

    /**
     * 获取实际的缓存数据
     *
     * @param cacheKey
     * @return
     */
    public Object getCacheValue(String cacheKey) {
        try {
            registry.readLock();
            if (FULL_SERVICE_REGISTRY.equals(cacheKey)) {
                return new Applications(registry.getRegistry());
            } else if (CacheKey.DELTA_SERVICE_REGISTRY.equals(cacheKey)) {
                return registry.getDeltaRegistry();
            }
        } finally {
            registry.readUnlock();
        }

        return null;
    }

    /**
     * 过期掉对应的缓存
     */
    public void invalidate() {
        synchronized (lock) {
            readWriteMap.remove(FULL_SERVICE_REGISTRY);
            readWriteMap.remove(DELTA_SERVICE_REGISTRY);
        }
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static ServiceRegistryCache getInstance() {
        return instance;
    }

    /**
     * 同步两个缓存map的后台线程
     */
    class CacheMapSyncDaemon extends Thread {

        @Override
        public void run() {
            while (true) {
                try {
                    try {
                        writeLock.lock();

                        synchronized (lock) {
                            if (readWriteMap.get(FULL_SERVICE_REGISTRY) == null) {
                                readOnlyMap.put(FULL_SERVICE_REGISTRY, null);
                            }

                            if (readWriteMap.get(DELTA_SERVICE_REGISTRY) == null) {
                                readOnlyMap.put(DELTA_SERVICE_REGISTRY, null);
                            }
                        }
                    } finally {
                        writeLock.unlock();
                    }
                    Thread.sleep(CACHE_MAP_SYNC_INTERVAL);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
