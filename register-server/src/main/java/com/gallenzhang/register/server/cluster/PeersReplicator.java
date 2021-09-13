package com.gallenzhang.register.server.cluster;

import com.gallenzhang.register.server.web.AbstractRequest;
import com.gallenzhang.register.server.web.CancelRequest;
import com.gallenzhang.register.server.web.HeartbeatRequest;
import com.gallenzhang.register.server.web.RegisterRequest;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @description: 集群同步组件
 * @className: com.gallenzhang.register.server.cluster.PeersReplicator
 * @author: gallenzhang
 * @createDate: 2021/9/13
 */
public class PeersReplicator {

    private static final PeersReplicator instance = new PeersReplicator();

    /**
     * 第一层队列：接收请求的高并发写入，无界队列
     */
    private ConcurrentLinkedQueue<AbstractRequest> acceptorQueue = new ConcurrentLinkedQueue();

    private PeersReplicator() {

    }

    public static PeersReplicator getInstance() {
        return instance;
    }

    /**
     * 同步服务注册请求
     */
    public void replicateRegister(RegisterRequest request) {
        acceptorQueue.offer(request);
    }

    /**
     * 同步服务下线请求
     */
    public void replicateCancel(CancelRequest request) {
        acceptorQueue.offer(request);
    }

    /**
     * 服务同步心跳请求
     */
    public void replicateHeartbeat(HeartbeatRequest request) {
        acceptorQueue.offer(request);
    }

}
