package com.lb.im.server.application.consumer;

import cn.hutool.core.util.StrUtil;
import com.lb.im.common.domain.constans.IMConstants;
import com.lb.im.common.domain.enums.IMCmdType;
import com.lb.im.common.domain.model.IMReceiveInfo;
import com.lb.im.server.application.netty.processor.MessageProcessor;
import com.lb.im.server.application.netty.processor.factory.ProcessorFactory;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQPushConsumerLifecycleListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 私有消息消费者，基于RocketMQ实现消息监听与处理
 * 监听指定消费者组和主题，处理私有消息队列中的消息
 */
@Component
@ConditionalOnProperty(name = "message.mq.type", havingValue = "rocketmq")
@RocketMQMessageListener(consumerGroup = IMConstants.IM_MESSAGE_PRIVATE_CONSUMER_GROUP, topic = IMConstants.IM_MESSAGE_PRIVATE_NULL_QUEUE)
public class PrivateMessageConsumer extends BaseMessageConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {

    private final Logger logger = LoggerFactory.getLogger(PrivateMessageConsumer.class);

    @Value("${server.id}")
    private Long serverId;

    @Override
    public void onMessage(String message) {
        if (StrUtil.isEmpty(message)) {
            logger.warn("PrivateMessageConsumer.onMessage|接收到的消息为空");
            return;
        }

        IMReceiveInfo imReceiveInfo = this.getReceiveMessage(message);
        if (imReceiveInfo == null) {
            logger.warn("PrivateMessageConsumer.onMessage|转化后的数据为空");
            return;
        }

        MessageProcessor processor = ProcessorFactory.getProcessor(IMCmdType.PRIVATE_MESSAGE);
        processor.process(imReceiveInfo);
    }

    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        try {
            // 拼接实际订阅的topic名称（格式：im_message_private_serverId）
            String topic = String.join(IMConstants.MESSAGE_KEY_SPLIT, IMConstants.IM_MESSAGE_PRIVATE_QUEUE, String.valueOf(serverId));
            consumer.subscribe(topic, "*");
        } catch (Exception e) {
            logger.error("PrivateMessageConsumer.prepareStart|异常:{}", e.getMessage());
        }
    }
}
