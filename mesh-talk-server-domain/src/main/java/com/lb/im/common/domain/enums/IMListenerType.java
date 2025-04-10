package com.lb.im.common.domain.enums;

/**
 * 监听类型
 */
public enum IMListenerType {

    ALL(0,"全部消息"),
    PRIVATE_MESSAGE(1,"私聊消息"),
    GROUP_MESSAGE(2,"群聊消息");

    private final Integer code;

    private final String desc;

    IMListenerType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer code(){
        return this.code;
    }
}
