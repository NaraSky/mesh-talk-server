package com.lb.im.common.domain.enums;

/**
 * 发送状态码
 */
public enum IMSendCode {

    SUCCESS(0,"发送成功"),
    NOT_ONLINE(1,"对方当前不在线"),
    NOT_FIND_CHANNEL(2,"未找到对方的channel"),
    UNKNOWN_ERROR(9999,"未知异常");
    private final Integer code;
    private final String desc;

    IMSendCode(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer code(){
        return this.code;
    }

}