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
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GroupMessageProcessor implements MessageProcessor<IMReceiveInfo> {
    private final Logger logger = LoggerFactory.getLogger(GroupMessageProcessor.class);

    @Autowired
    private MessageSenderService messageSenderService;

    @Async
    @Override
    public void process(IMReceiveInfo receiveInfo) {
        IMUserInfo fromUser = receiveInfo.getFromUser();
        List<IMUserInfo> receivers = receiveInfo.getReceivers();
        logger.info("GroupMessageProcessor.process|接收到群消息,发送消息用户:{}，接收消息用户数量:{}，消息内容:{}",
                    fromUser.getUserId(), receivers.size(), receiveInfo.getContent());
        receivers.forEach(receiver -> {
            try {
                ChannelHandlerContext channelHandlerCtx = UserChannelContextCache.getChannelCtx(receiver.getUserId(), receiver.getDeviceType());
                if (channelHandlerCtx != null) {
                    //向用户推送消息
                    IMSendInfo<?> imSendInfo = new IMSendInfo<>(IMCmdType.GROUP_MESSAGE.getCode(), receiveInfo.getContent());
                    channelHandlerCtx.writeAndFlush(imSendInfo);
                    //发送确认消息
                    sendResult(receiveInfo, receiver, IMSendCode.SUCCESS);
                } else {
                    //未找到用户的连接信息
                    sendResult(receiveInfo, receiver, IMSendCode.NOT_FIND_CHANNEL);
                    logger.error("GroupMessageProcessor.process|未找到Channel,发送者:{}, 接收者:{}, 消息内容:{}", fromUser.getUserId(), receiver.getUserId(), receiveInfo.getContent());
                }
            } catch (Exception e) {
                sendResult(receiveInfo, receiver, IMSendCode.UNKNOWN_ERROR);
                logger.error("GroupMessageProcessor.process|发送消息异常,发送者:{}, 接收者:{}, 消息内容:{}, 异常信息:{}",
                             fromUser.getUserId(), receiver.getUserId(), receiveInfo.getContent(), e.getMessage());
            }
        });
    }

    /**
     * 发送结果数据
     */
    private void sendResult(IMReceiveInfo imReceivenfo, IMUserInfo imUserInfo, IMSendCode imSendCode) {
        if (imReceivenfo.getSendResult()) {
            IMSendResult<?> imSendResult = new IMSendResult<>(imReceivenfo.getFromUser(), imUserInfo, imSendCode.code(), imReceivenfo.getContent());
            imSendResult.setDestination(IMConstants.IM_RESULT_GROUP_QUEUE);
            messageSenderService.send(imSendResult);
        }
    }
}
