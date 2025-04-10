package com.lb.im.common.domain.model;

/**
 * session信息
 */
public class IMSessionInfo {

    private Long userId;
    // 设备类型
    private Integer deviceType;

    public IMSessionInfo() {
    }

    public IMSessionInfo(Long userId, Integer deviceType) {
        this.userId = userId;
        this.deviceType = deviceType;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(Integer deviceType) {
        this.deviceType = deviceType;
    }
}
