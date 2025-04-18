package com.lb.im.server.application.netty.processor.impl;

import com.lb.im.common.domain.enums.IMCmdType;
import com.lb.im.common.domain.enums.IMSendCode;
import com.lb.im.common.domain.model.IMReceiveInfo;
import com.lb.im.common.domain.model.IMSendInfo;
import com.lb.im.common.domain.model.IMUserInfo;
import com.lb.im.common.mq.MessageSenderService;
import com.lb.im.server.application.netty.cache.UserChannelContextCache;
import com.lb.im.server.application.netty.processor.MessageProcessor;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 处理私聊消息的处理器类，实现MessageProcessor接口
 * 负责接收私聊消息并推送至目标用户终端，同时处理消息发送结果反馈
 */
@Component
public class PrivateMessageProcessor extends BaseMessageProcessor implements MessageProcessor<IMReceiveInfo> {
    private final Logger logger = LoggerFactory.getLogger(PrivateMessageProcessor.class);

    @Autowired
    private MessageSenderService messageSenderService;

    /**
     * 处理私聊消息的核心方法
     * @param receiveInfo 消息接收信息对象，包含发送者、接收者列表和消息内容
     */
    @Override
    public void process(IMReceiveInfo receiveInfo) {
        IMUserInfo sender = receiveInfo.getSender();
        IMUserInfo receiver = receiveInfo.getReceivers().get(0);
        logger.info("PrivateMessageProcessor.process|接收到消息,发送者:{}, 接收者:{}, 内容:{}", sender.getUserId(), receiver.getUserId(), receiveInfo.getData());

        try {
            // 获取接收方的网络连接上下文
            ChannelHandlerContext channelHandlerContext = UserChannelContextCache.getChannelCtx(receiver.getUserId(), receiver.getTerminal());

            if (channelHandlerContext != null) {
                // 构建并推送消息到接收方
                IMSendInfo<?> imSendInfo = new IMSendInfo<>(IMCmdType.PRIVATE_MESSAGE.getCode(), receiveInfo.getData());
                channelHandlerContext.writeAndFlush(imSendInfo);

                // 反馈消息发送成功结果
                sendPrivateMessageResult(receiveInfo, IMSendCode.SUCCESS);
            } else {
                // 反馈未找到通道的错误结果
                sendPrivateMessageResult(receiveInfo, IMSendCode.NOT_FIND_CHANNEL);
                logger.error("PrivateMessageProcessor.process|未找到Channel, 发送者:{}, 接收者:{}, 内容:{}", sender.getUserId(), receiver.getUserId(), receiveInfo.getData());
            }
        } catch (Exception e) {
            // 异常情况处理及反馈
            sendPrivateMessageResult(receiveInfo, IMSendCode.UNKNOWN_ERROR);
            logger.error("PrivateMessageProcessor.process|发送异常,发送者:{}, 接收者:{}, 内容:{}, 异常信息:{}", sender.getUserId(), receiver.getUserId(), receiveInfo.getData(), e.getMessage());
        }
    }
}
