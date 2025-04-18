package com.lb.im.server.application.netty.processor.impl;

import com.lb.im.common.domain.enums.IMCmdType;
import com.lb.im.common.domain.enums.IMSendCode;
import com.lb.im.common.domain.model.IMReceiveInfo;
import com.lb.im.common.domain.model.IMSendInfo;
import com.lb.im.common.domain.model.IMUserInfo;
import com.lb.im.server.application.netty.cache.UserChannelContextCache;
import com.lb.im.server.application.netty.processor.MessageProcessor;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 群消息处理器，实现消息处理器接口，负责处理接收到的群消息并分发给所有接收者。
 * 继承自BaseMessageProcessor，提供群组消息的异步处理能力。
 */
@Component
public class GroupMessageProcessor extends BaseMessageProcessor implements MessageProcessor<IMReceiveInfo> {
    private final Logger logger = LoggerFactory.getLogger(GroupMessageProcessor.class);

    /**
     * 异步处理接收到的群消息。
     * @param receiveInfo 消息接收信息对象，包含发送者、接收者列表和消息内容
     */
    @Async
    @Override
    public void process(IMReceiveInfo receiveInfo) {
        IMUserInfo sender = receiveInfo.getSender();
        List<IMUserInfo> receivers = receiveInfo.getReceivers();

        // 记录消息接收日志
        logger.info("GroupMessageProcessor.process|接收到群消息,发送消息用户:{}，接收消息用户数量:{}，消息内容:{}", sender.getUserId(), receivers.size(), receiveInfo.getData());

        // 遍历所有接收者并逐个处理消息发送
        receivers.forEach((receiver) -> {
            try {
                ChannelHandlerContext channelHandlerCtx = UserChannelContextCache.getChannelCtx(receiver.getUserId(), receiver.getTerminal());

                if (channelHandlerCtx != null) {
                    // 向用户推送消息
                    IMSendInfo<?> imSendInfo = new IMSendInfo<>(IMCmdType.GROUP_MESSAGE.getCode(), receiveInfo.getData());
                    channelHandlerCtx.writeAndFlush(imSendInfo);

                    // 发送成功确认消息
                    sendGroupMessageResult(receiveInfo, receiver, IMSendCode.SUCCESS);
                } else {
                    // 未找到用户连接时记录错误并发送失败确认
                    sendGroupMessageResult(receiveInfo, receiver, IMSendCode.NOT_FIND_CHANNEL);
                    logger.error("GroupMessageProcessor.process|未找到Channel,发送者:{}, 接收者:{}, 消息内容:{}", sender.getUserId(), receiver.getUserId(), receiveInfo.getData());
                }
            } catch (Exception e) {
                // 异常处理：记录错误并发送未知错误确认
                sendGroupMessageResult(receiveInfo, receiver, IMSendCode.UNKNOWN_ERROR);
                logger.error("GroupMessageProcessor.process|发送消息异常,发送者:{}, 接收者:{}, 消息内容:{}, 异常信息:{}", sender.getUserId(), receiver.getUserId(), receiveInfo.getData(), e.getMessage());
            }
        });
    }
}
