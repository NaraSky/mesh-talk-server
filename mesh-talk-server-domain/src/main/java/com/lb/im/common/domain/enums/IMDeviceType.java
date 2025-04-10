package com.lb.im.common.domain.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 设备类型
 */
public enum IMDeviceType {

    WEB(0, "web"),
    APP(1, "app");

    private final Integer code;
    private final String desc;

    IMDeviceType(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static IMDeviceType getByCode(Integer code) {
        for (IMDeviceType deviceType : IMDeviceType.values()) {
            if (deviceType.getCode().equals(code)) {
                return deviceType;
            }
        }
        return null;
    }

    public Integer getCode() {
        return code;
    }

    public static List<Integer> getAllCode() {
        return Arrays.stream(values()).map(IMDeviceType::getCode).collect(Collectors.toList());
    }
}
