package com.gallenzhang.register.client;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicStampedReference;

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
     * 客户端缓存的所有服务实例信息
     * <p>
     * 使用AtomicReference优化了一下，多个地方多个线程同时对一个对象变量的引用进行赋值的时候，可能导致的并发冲突的问题，
     * 就用AtomicReference的CAS操作来解决，而没有使用加锁的重量级的方式
     * </p>
     */
    private AtomicStampedReference<Applications> applications = null;


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
     * 代表了当前的本地缓存的服务注册表的一个版本号
     */
    private AtomicLong applicationsVersion = new AtomicLong(0);

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
        this.applications = new AtomicStampedReference(new Applications(), 0);
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
            //这个操作要走网络，但是不知道为什么，此时就是一直卡住，数据没有返回，卡在这儿，卡了几分钟
            //此时的这个数据是一个旧的版本，里面仅仅包含了30个服务实例
            //恰好在增量注册表拉取的reconcileRegistry纠正完成之后，全量注册表的线程突然苏醒了（此时全量注册表已经有40个服务实例）
            //此时将30个服务实例的旧版本的数据赋值给了本地缓存注册表，这就会导致注册表版本混乱问题

            //一定要在发起网络请求之前，先拿到一个当时的版本号
            //接着在这里发起网络请求，此时可能会有别的线程来修改这个注册表，更新版本，在这个期间
            //必须是发起网络请求之后，这个注册表的版本没被别人修改过，此时他才能去修改
            //如果在这个期间，有人修改过注册表，版本不一样了，此时就直接if不成立，不要把你拉取到的旧版本的注册表给设置进去。
            fetchFullRegistry();
        }
    }

    /**
     * 拉取全量注册表到本地
     */
    private void fetchFullRegistry() {
        Long expectedVersion = applicationsVersion.get();
        //接着在这里发起网络请求，此时可能会有别的线程来修改这个注册表，更新版本，在这个期间
        Applications fetchedApplications = httpSender.fetchFullRegistry();
        //必须是发起网络请求之后，这个注册表的版本没被别人修改过，此时他才能去修改
        if (applicationsVersion.compareAndSet(expectedVersion, expectedVersion + 1)) {
            while (true) {
                Applications expectedApplications = applications.getReference();
                int expectedStamp = applications.getStamp();
                if (applications.compareAndSet(expectedApplications, fetchedApplications,
                        expectedStamp, expectedStamp + 1)) {
                    break;
                }
            }
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
                    //先拉了一个增量注册表，发现跟本地合并之后，条数不对
                    Long expectedVersion = applicationsVersion.get();
                    DeltaRegistry deltaRegistry = httpSender.fetchDeltaRegistry();
                    if (applicationsVersion.compareAndSet(expectedVersion, expectedVersion + 1)) {
                        //一类是注册，一类是删除。
                        //如果是注册的话，就判断一下这个服务实例是否在这个本地缓存的注册表中。如果不在的话，就放到本地缓存注册表中去
                        //如果是删除的话，判断一下服务实例存在,就给删除掉

                        //这里会大量的修改本地缓存的注册表，所以这里需要加锁
                        mergeDeltaRegistry(deltaRegistry);

                        //再检查一下，跟服务端的注册表的服务实例的数量相比，是否是一致的
                        //封装一下增量注册表的对象，也就是拉取增量注册表的时候，一方面返回那个数据，另外一方面要那个对应的register-server端的服务实例的数量
                        reconcileRegistry(deltaRegistry);
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
     * @param deltaRegistry
     */
    private void mergeDeltaRegistry(DeltaRegistry deltaRegistry) {
        synchronized (applications) {
            Map<String, Map<String, ServiceInstance>> registry = applications.getReference().getRegistry();

            for (RecentlyChangedServiceInstance recentlyChangedItem : deltaRegistry.getRecentlyChangedQueue()) {
                String serviceName = recentlyChangedItem.serviceInstance.getServiceName();
                String serviceInstanceId = recentlyChangedItem.serviceInstance.getServiceInstanceId();

                //如果是注册操作的话
                if (ServiceInstanceOperation.REGISTER.equals(recentlyChangedItem.serviceInstanceOperation)) {
                    Map<String, ServiceInstance> serviceInstanceMap = registry.get(serviceName);
                    if (serviceInstanceMap == null) {
                        serviceInstanceMap = new HashMap<>();
                        registry.put(serviceName, serviceInstanceMap);
                    }

                    ServiceInstance serviceInstance = serviceInstanceMap.get(serviceInstanceId);
                    if (serviceInstance == null) {
                        serviceInstanceMap.put(serviceInstanceId, recentlyChangedItem.serviceInstance);
                    }

                } else if (ServiceInstanceOperation.REMOVE.equals(recentlyChangedItem.serviceInstanceOperation)) {
                    //如果是删除操作
                    Map<String, ServiceInstance> serviceInstanceMap = registry.get(serviceName);
                    if (serviceInstanceMap != null) {
                        serviceInstanceMap.remove(serviceInstanceId);
                    }
                }
            }
        }
    }

    /**
     * 校对调整注册表
     *
     * @param deltaRegistry
     */
    private void reconcileRegistry(DeltaRegistry deltaRegistry) {
        Map<String, Map<String, ServiceInstance>> registry = applications.getReference().getRegistry();

        //获取服务端的实例数量
        Long serverSideTotalCount = deltaRegistry.getServiceInstanceTotalCount();

        //获取客户端的实例数量
        Long clientSideTotalCount = 0L;
        for (Map<String, ServiceInstance> serviceInstanceMap : registry.values()) {
            clientSideTotalCount += serviceInstanceMap.size();
        }

        if (!serverSideTotalCount.equals(clientSideTotalCount)) {
            //重新拉取全量注册表进行纠正
            //正常进行全量注册表最新数据的一个赋值，可能是包含了40个服务实例
            fetchFullRegistry();
        }
    }

    /**
     * 获取服务注册表
     *
     * @return
     */
    public Map<String, Map<String, ServiceInstance>> getRegistry() {
        return applications.getReference().getRegistry();
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
