package com.gallenzhang.register.server.web;

/**
 * @description: 注册请求
 * @className: com.gallenzhang.register.server.web.RegisterRequest
 * @author: gallenzhang
 * @createDate: 2021/8/19
 */
public class RegisterRequest extends AbstractRequest {

    /**
     * 服务所在机器的主机名
     */
    private String hostName;

    /**
     * 服务所在机器的ip地址
     */
    private String ip;

    /**
     * 服务监听着哪个端口号
     */
    private Integer port;

    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "serviceName='" + serviceName + '\'' +
                ", serviceInstanceId='" + serviceInstanceId + '\'' +
                ", hostName='" + hostName + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
