package com.lb.im.common.domain.model;

import com.lb.im.common.domain.enums.IMDeviceType;

import java.util.LinkedList;
import java.util.List;

/**
 * 群组消息
 */
public class IMGroupMessage<T> {

    // 发送者
    private IMUserInfo fromUser;
    // 接收者id， 群组id，为空时则不会推送
    private List<Long> receiverIds = new LinkedList<>();
    // 接收者设备类型，默认全部终端
    private List<Integer> receiveDeviceTypes = IMDeviceType.getAllCode();
    // 是否发送给自己的其他终端
    private Boolean sendToself = true;
    // 是否需要回推发送结果
    private Boolean sendResult = true;
    // 消息内容
    private T content;

    public IMGroupMessage() {
    }

    public IMGroupMessage(IMUserInfo fromUser, List<Long> receiverIds, List<Integer> receiveDeviceTypes, Boolean sendToself, Boolean sendResult, T content) {
        this.fromUser = fromUser;
        this.receiverIds = receiverIds;
        this.receiveDeviceTypes = receiveDeviceTypes;
        this.sendToself = sendToself;
        this.sendResult = sendResult;
        this.content = content;
    }

    public IMUserInfo getFromUser() {
        return fromUser;
    }

    public void setFromUser(IMUserInfo fromUser) {
        this.fromUser = fromUser;
    }

    public List<Long> getReceiverIds() {
        return receiverIds;
    }

    public void setReceiverIds(List<Long> receiverIds) {
        this.receiverIds = receiverIds;
    }

    public List<Integer> getReceiveDeviceTypes() {
        return receiveDeviceTypes;
    }

    public void setReceiveDeviceTypes(List<Integer> receiveDeviceTypes) {
        this.receiveDeviceTypes = receiveDeviceTypes;
    }

    public Boolean getSendToself() {
        return sendToself;
    }

    public void setSendToself(Boolean sendToself) {
        this.sendToself = sendToself;
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

