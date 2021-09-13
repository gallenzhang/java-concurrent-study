package com.gallenzhang.register.server.cluster;

import com.gallenzhang.register.server.web.AbstractRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * @description: 集群同步batch
 * @className: com.gallenzhang.register.server.cluster.PeersReplicateBatch
 * @author: gallenzhang
 * @createDate: 2021/9/13
 */
public class PeersReplicateBatch {

    private List<AbstractRequest> requests = new ArrayList<>();

    public void add(AbstractRequest request) {
        this.requests.add(request);
    }

    public List<AbstractRequest> getRequests() {
        return requests;
    }

    public void setRequests(List<AbstractRequest> requests) {
        this.requests = requests;
    }
}
