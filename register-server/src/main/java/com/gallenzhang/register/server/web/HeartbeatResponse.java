package com.gallenzhang.register.server.web;

/**
 * @description: 心跳响应
 * @className: com.gallenzhang.register.server.web.HeartbeatResponse
 * @author: gallenzhang
 * @createDate: 2021/8/19
 */
public class HeartbeatResponse {

    public static final String SUCCESS = "success";
    public static final String FAILURE = "failure";

    /**
     * 心跳响应状态：SUCCESS、FAILURE
     */
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
