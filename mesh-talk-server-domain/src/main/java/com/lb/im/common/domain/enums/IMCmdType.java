package com.lb.im.common.domain.enums;

public enum IMCmdType {

    LOGIN(0,"登录"),
    HEART_BEAT(1,"心跳"),
    FORCE_LOGOUT(2, "强制下线"),
    PRIVATE_MESSAGE(3,"私聊消息"),
    GROUP_MESSAGE(4,"群发消息");


    private final Integer code;

    private final String desc;

    IMCmdType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static IMCmdType getByCode(Integer code) {
        for (IMCmdType cmdType : IMCmdType.values()) {
            if (cmdType.getCode().equals(code)) {
                return cmdType;
            }
        }
        return null;
    }

    private Integer getCode() {
        return code;
    }
}
