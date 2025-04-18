package com.lb.im.server.application.netty.processor.impl;

import com.lb.im.common.domain.constans.IMConstants;
import com.lb.im.common.domain.enums.IMSendCode;
import com.lb.im.common.domain.model.IMReceiveInfo;
import com.lb.im.common.domain.model.IMSendResult;
import com.lb.im.common.domain.model.IMUserInfo;
import com.lb.im.common.mq.MessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 处理基础消息发送结果的处理器
 */
public class BaseMessageProcessor {

    @Autowired
    private MessageSenderService messageSenderService;

    /**
     * 发送私聊消息结果
     *
     * @param receiveInfo 接收的消息信息对象
     * @param sendCode    消息发送状态码
     */
    protected void sendPrivateMessageResult(IMReceiveInfo receiveInfo, IMSendCode sendCode) {
        if (receiveInfo.getSendResult()) {
            // 构建并发送私聊消息结果
            IMSendResult<?> result = new IMSendResult<>(receiveInfo.getSender(), receiveInfo.getReceivers().get(0), sendCode.code(), receiveInfo.getData());
            String sendKey = IMConstants.IM_RESULT_PRIVATE_QUEUE;
            result.setDestination(sendKey);
            messageSenderService.send(result);
        }
    }

    /**
     * 发送群组消息结果
     *
     * @param imReceiveInfo 接收的消息信息对象
     * @param imUserInfo   用户信息对象
     * @param imSendCode   消息发送状态码
     */
    protected void sendGroupMessageResult(IMReceiveInfo imReceiveInfo, IMUserInfo imUserInfo, IMSendCode imSendCode) {
        if (imReceiveInfo.getSendResult()) {
            // 构建并发送群组消息结果
            IMSendResult<?> imSendResult = new IMSendResult<>(imReceiveInfo.getSender(), imUserInfo, imSendCode.code(), imReceiveInfo.getData());
            imSendResult.setDestination(IMConstants.IM_RESULT_GROUP_QUEUE);
            messageSenderService.send(imSendResult);
        }
    }
}
