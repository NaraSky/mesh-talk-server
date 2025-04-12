package com.lb.im.server.application.netty.processor.impl;

import com.lb.im.common.domain.constans.IMConstants;
import com.lb.im.common.domain.enums.IMCmdType;
import com.lb.im.common.domain.enums.IMSendCode;
import com.lb.im.common.domain.model.IMReceiveInfo;
import com.lb.im.common.domain.model.IMSendInfo;
import com.lb.im.common.domain.model.IMSendResult;
import com.lb.im.common.domain.model.IMUserInfo;
import com.lb.im.common.mq.MessageSenderService;
import com.lb.im.server.application.netty.cache.UserChannelContextCache;
import com.lb.im.server.application.netty.processor.MessageProcessor;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 处理私聊消息的处理器。
 * 实现MessageProcessor接口，负责接收私聊消息，查找接收者的Channel上下文，
 * 并将消息推送到目标客户端，或在失败时记录错误并发送发送结果。
 */
@Component
public class PrivateMessageProcessor implements MessageProcessor<IMReceiveInfo> {

    private final Logger logger = LoggerFactory.getLogger(PrivateMessageProcessor.class);

    @Autowired
    private MessageSenderService messageSenderService;

    @Override
    /**
     * 处理私聊消息的核心方法。
     *
     * @param receiveInfo 接收到的消息信息，包含发送者、接收者列表、消息内容等
     */
    public void process(IMReceiveInfo receiveInfo) {
        IMUserInfo fromUser = receiveInfo.getFromUser();
        List<IMUserInfo> receivers = receiveInfo.getReceivers();
        IMUserInfo receiver = receivers.get(0);
        logger.info("PrivateMessageProcessor.process|接收到消息,发送者:{}, 接收者:{}, 内容:{}",
                    fromUser.getUserId(), receiver.getUserId(), receiveInfo.getContent());
        try {
            ChannelHandlerContext channelHandlerContext = UserChannelContextCache
                    .getChannelCtx(receiver.getUserId(), receiver.getDeviceType());
            if (channelHandlerContext != null) {
                // 推送消息到接收者通道
                IMSendInfo<?> imSendInfo = new IMSendInfo<>(IMCmdType.PRIVATE_MESSAGE.getCode(), receiveInfo.getContent());
                channelHandlerContext.writeAndFlush(imSendInfo);
                sendResult(receiveInfo, IMSendCode.SUCCESS);
            } else {
                // 未找到接收者通道，记录错误并发送发送结果
                sendResult(receiveInfo, IMSendCode.NOT_FIND_CHANNEL);
                logger.error("PrivateMessageProcessor.process|未找到Channel, 发送者:{}, 接收者:{}, 内容:{}",
                             fromUser.getUserId(), receiver.getUserId(), receiveInfo.getContent());
            }
        } catch (Exception e) {
            // 处理发送异常，记录错误并发送发送结果
            sendResult(receiveInfo, IMSendCode.NOT_FIND_CHANNEL);
            logger.error("PrivateMessageProcessor.process|发送异常,发送者:{}, 接收者:{}, 内容:{}, 异常信息:{}",
                         fromUser.getUserId(), receiver.getUserId(), receiveInfo.getContent(), e.getMessage());
        }
    }

    /**
     * 发送消息发送结果到消息队列。
     *
     * @param receiveInfo 原始接收的消息信息
     * @param sendCode    发送结果状态码
     */
    private void sendResult(IMReceiveInfo receiveInfo, IMSendCode sendCode) {
        if (receiveInfo.getSendResult()) {
            IMSendResult<?> result = new IMSendResult<>(receiveInfo.getFromUser(),
                                                        receiveInfo.getReceivers(),
                                                        sendCode.code(), receiveInfo.getContent());
            String sendKey = IMConstants.IM_RESULT_PRIVATE_QUEUE;
            result.setDestination(sendKey);
            messageSenderService.send(result);
        }
    }
}
