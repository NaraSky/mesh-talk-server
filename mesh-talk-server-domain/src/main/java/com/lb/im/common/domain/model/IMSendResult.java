package com.lb.im.common.domain.model;

import java.util.List;

/**
 * 响应结果数据模型
 */
public class IMSendResult<T> extends TopicMessage {

    private static final long serialVersionUID = 8026992050902848501L;

    // 发送的用户
    private IMUserInfo fromUser;
    // 接收的用户
    List<IMUserInfo> receivers;
    // 指令类型
    private Integer code;
    private T data;

    public IMSendResult() {
    }

    public IMSendResult(IMUserInfo fromUser, List<IMUserInfo> receivers, Integer code, T data) {
        this.fromUser = fromUser;
        this.receivers = receivers;
        this.code = code;
        this.data = data;
    }

    public IMUserInfo getFromUser() {
        return fromUser;
    }

    public void setFromUser(IMUserInfo fromUser) {
        this.fromUser = fromUser;
    }

    public List<IMUserInfo> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<IMUserInfo> receivers) {
        this.receivers = receivers;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
