package com.gallenzhang.register.server;

/**
 * @description: 注册响应
 * @className: com.gallenzhang.register.server.RegisterResponse
 * @author: gallenzhang
 * @createDate: 2021/8/19
 */
public class RegisterResponse {

    public static final String SUCCESS = "success";
    public static final String FAILURE = "failure";

    /**
     * 注册响应状态：SUCCESS、FAILURE
     */
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
