package com.gallenzhang.register.client;


/**
 * @description: 代表了一个服务实例，里面包含了一个服务实例的所有信息(服务名称、ip地址、hostname、端口号、服务实例id)
 * @className: com.gallenzhang.register.client.ServiceInstance
 * @copyRight: www.shopee.com by SZDC-BankingGroup
 * @author: xiaoqiang.zhang
 * @createDate: 2021/8/20
 */
public class ServiceInstance {


    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * ip地址
     */
    private String ip;

    /**
     * 主机名
     */
    private String hostName;

    /**
     * 端口号
     */
    private Integer port;

    /**
     * 服务实例ID
     */
    private String serviceInstanceId;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getServiceInstanceId() {
        return serviceInstanceId;
    }

    public void setServiceInstanceId(String serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    @Override
    public String toString() {
        return "ServiceInstance{" +
                "serviceName='" + serviceName + '\'' +
                ", ip='" + ip + '\'' +
                ", hostName='" + hostName + '\'' +
                ", port=" + port +
                ", serviceInstanceId='" + serviceInstanceId + '\'' +
                '}';
    }
}
