package com.gallenzhang.register.server.web;

/**
 * @description: 请求接口
 * @className: com.gallenzhang.register.server.web.AbstractRequest
 * @author: gallenzhang
 * @createDate: 2021/9/13
 */
public abstract class AbstractRequest {

    /**
     * 服务名称
     */
    protected String serviceName;

    /**
     * 服务实例id
     */
    protected String serviceInstanceId;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getServiceInstanceId() {
        return serviceInstanceId;
    }

    public void setServiceInstanceId(String serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    @Override
    public String toString() {
        return "AbstractRequest{" +
                "serviceName='" + serviceName + '\'' +
                ", serviceInstanceId='" + serviceInstanceId + '\'' +
                '}';
    }
}
