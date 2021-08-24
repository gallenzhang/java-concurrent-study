package com.gallenzhang.register.server;

import java.util.HashMap;
import java.util.Map;

/**
 * @description: 完整的服务实例的信息
 * @className: com.gallenzhang.register.server.Applications
 * @copyRight: www.shopee.com by SZDC-BankingGroup
 * @author: xiaoqiang.zhang
 * @createDate: 2021/8/24
 */
public class Applications {

    private Map<String, Map<String, ServiceInstance>> registry = new HashMap<>();

    public Applications() {

    }

    public Applications(Map<String, Map<String, ServiceInstance>> registry) {
        this.registry = registry;
    }

    public Map<String, Map<String, ServiceInstance>> getRegistry() {
        return registry;
    }

    public void setRegistry(Map<String, Map<String, ServiceInstance>> registry) {
        this.registry = registry;
    }
}
