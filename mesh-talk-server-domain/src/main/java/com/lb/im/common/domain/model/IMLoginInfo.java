package com.lb.im.common.domain.model;

/**
 * 登录信息
 */
public class IMLoginInfo {

    private String accessToken;

    public IMLoginInfo() {
    }

    public IMLoginInfo(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
