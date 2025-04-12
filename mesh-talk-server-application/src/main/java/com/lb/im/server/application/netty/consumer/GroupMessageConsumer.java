package com.lb.im.server.application.netty.consumer;

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
 * 群组消息消费者，基于RocketMQ实现消息监听与处理功能。
 * 该消费者在配置项message.mq.type为rocketmq时生效，监听指定的消费者组和主题，负责接收并处理群组消息。
 * 继承BaseMessageConsumer以复用基础消息处理逻辑，实现RocketMQListener接口以接收消息，实现RocketMQPushConsumerLifecycleListener以配置消费者行为。
 */
@Component
@ConditionalOnProperty(name = "message.mq.type", havingValue = "rocketmq")
@RocketMQMessageListener(consumerGroup = IMConstants.IM_MESSAGE_GROUP_CONSUMER_GROUP, topic = IMConstants.IM_MESSAGE_GROUP_NULL_QUEUE)
public class GroupMessageConsumer extends BaseMessageConsumer implements RocketMQListener<String>, RocketMQPushConsumerLifecycleListener {
    private final Logger logger = LoggerFactory.getLogger(GroupMessageConsumer.class);

    @Value("${server.id}")
    private Long serverId;

    /**
     * 处理接收到的RocketMQ消息。
     *
     * @param message 接收到的消息内容
     */
    @Override
    public void onMessage(String message) {
        if (StrUtil.isEmpty(message)) {
            logger.warn("GroupMessageConsumer.onMessage|接收到的消息为空");
            return;
        }

        IMReceiveInfo imReceiveInfo = this.getReceiveMessage(message);
        if (imReceiveInfo == null) {
            logger.warn("GroupMessageConsumer.onMessage|转化后的数据为空");
            return;
        }

        // 获取对应的处理器并执行消息处理逻辑
        MessageProcessor processor = ProcessorFactory.getProcessor(IMCmdType.GROUP_MESSAGE);
        processor.process(imReceiveInfo);
    }

    /**
     * 消费者启动前的初始化配置。
     *
     * @param consumer RocketMQ推送消费者实例
     */
    @Override
    public void prepareStart(DefaultMQPushConsumer consumer) {
        // 动态生成订阅主题名称，格式为常量与服务器ID拼接
        try {
            String topic = String.join(IMConstants.MESSAGE_KEY_SPLIT, IMConstants.IM_MESSAGE_GROUP_QUEUE, String.valueOf(serverId));
            consumer.subscribe(topic, "*");
        } catch (Exception e) {
            // 捕获并记录配置过程中可能出现的异常
            logger.error("GroupMessageConsumer.prepareStart|异常:{}", e.getMessage());
        }
    }
}
