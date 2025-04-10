package com.lb.im.common.domain.model;

/**
 * 通用发送数据模型
 */
public class IMSendInfo<T> {

    // 命令类型 IMCmdType枚举的值
    private Integer cmd;
    // 推送消息的数据
    private T data;

    public IMSendInfo() {
    }

    public IMSendInfo(Integer cmd, T data) {
        this.cmd = cmd;
        this.data = data;
    }

    public Integer getCmd() {
        return cmd;
    }

    public void setCmd(Integer cmd) {
        this.cmd = cmd;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
