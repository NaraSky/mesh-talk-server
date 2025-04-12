package com.lb.im.server.application.netty.processor.factory;

import com.lb.im.common.domain.enums.IMCmdType;
import com.lb.im.server.application.netty.processor.MessageProcessor;
import com.lb.im.server.application.netty.processor.impl.HeartbeatProcessor;
import com.lb.im.server.application.netty.processor.impl.LoginProcessor;
import com.lb.im.server.holder.SpringContextHolder;

public class ProcessorFactory {

    public static MessageProcessor<?> getProcessor(IMCmdType cmd) {
        switch (cmd) {
            //登录
            case LOGIN:
                return SpringContextHolder.getApplicationContext().getBean(LoginProcessor.class);
            //心跳
            case HEART_BEAT:
                return SpringContextHolder.getApplicationContext().getBean(HeartbeatProcessor.class);
                //单聊消息
            case PRIVATE_MESSAGE:
                //群聊消息
            case GROUP_MESSAGE:
            default:
                return null;
        }
    }
}
