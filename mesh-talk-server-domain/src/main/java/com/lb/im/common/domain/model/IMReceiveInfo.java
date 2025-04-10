package com.lb.im.common.domain.model;

import java.util.List;

/**
 * 通用接收数据模型
 */
public class IMReceiveInfo<T> extends TopicMessage {

    private static final long serialVersionUID = 1L;
    // 命令类型
    private Integer cmd;
    // 发送的用户
    private IMUserInfo fromUser;
    // 接收的用户
    List<IMUserInfo> receivers;
    private Boolean sendResult;
    private T content;

    public IMReceiveInfo() {
    }

    public IMReceiveInfo(Integer cmd, IMUserInfo fromUser, List<IMUserInfo> receivers, Boolean sendResult, T content) {
        this.cmd = cmd;
        this.fromUser = fromUser;
        this.receivers = receivers;
        this.sendResult = sendResult;
        this.content = content;
    }

    public Integer getCmd() {
        return cmd;
    }

    public void setCmd(Integer cmd) {
        this.cmd = cmd;
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

    public Boolean getSendResult() {
        return sendResult;
    }

    public void setSendResult(Boolean sendResult) {
        this.sendResult = sendResult;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
