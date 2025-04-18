package com.lb.im.server.application.netty.processor.impl;

import cn.hutool.core.bean.BeanUtil;
import com.lb.im.common.cache.distribute.DistributedCacheService;
import com.lb.im.common.domain.constans.IMConstants;
import com.lb.im.common.domain.enums.IMCmdType;
import com.lb.im.common.domain.model.IMHeartbeatInfo;
import com.lb.im.common.domain.model.IMSendInfo;
import com.lb.im.server.application.netty.processor.MessageProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 处理心跳消息的处理器，实现 {@link MessageProcessor} 接口，用于处理 {@link IMHeartbeatInfo} 类型的消息。
 * 主要功能包括更新心跳计数、触发用户在线状态续命等操作。
 */
@Component
public class HeartbeatProcessor implements MessageProcessor<IMHeartbeatInfo> {
    @Autowired
    private DistributedCacheService distributedCacheService;

    @Value("${heartbeat.count}")
    private Integer heartbeatCount;

    /**
     * 处理心跳消息。
     *
     * @param ctx   Netty的ChannelHandlerContext，用于获取和操作网络通道
     * @param data  心跳消息数据对象 {@link IMHeartbeatInfo}
     */
    @Override
    public void process(ChannelHandlerContext ctx, IMHeartbeatInfo data) {
        /**
         * 响应WebSocket的心跳请求。
         */
        this.responseWS(ctx);

        /**
         * 更新通道的心跳计数属性。
         */
        AttributeKey<Long> heartBeatAttr = AttributeKey.valueOf(IMConstants.HEARTBEAT_TIMES);
        Long heartbeatTimes = ctx.channel().attr(heartBeatAttr).get();
        ctx.channel().attr(heartBeatAttr).set(++heartbeatTimes);

        /**
         * 当达到预设心跳次数阈值时，更新用户在线状态的缓存有效期。
         */
        if (heartbeatTimes % heartbeatCount == 0) {
            AttributeKey<Long> userIdAttr = AttributeKey.valueOf(IMConstants.USER_ID);
            Long userId = ctx.channel().attr(userIdAttr).get();
            AttributeKey<Integer> terminalAttr = AttributeKey.valueOf(IMConstants.TERMINAL_TYPE);
            Integer terminal = ctx.channel().attr(terminalAttr).get();
            String redisKey = String.join(IMConstants.REDIS_KEY_SPLIT, IMConstants.IM_USER_SERVER_ID, userId.toString(), terminal.toString());
            distributedCacheService.expire(redisKey, IMConstants.ONLINE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        }
    }

    /**
     * 发送心跳响应至WebSocket客户端。
     *
     * @param ctx Netty的ChannelHandlerContext，用于发送响应数据
     */
    private void responseWS(ChannelHandlerContext ctx) {
        IMSendInfo<?> imSendInfo = new IMSendInfo<>();
        imSendInfo.setCmd(IMCmdType.HEART_BEAT.getCode());
        ctx.channel().writeAndFlush(imSendInfo);
    }

    /**
     * 将原始对象转换为 {@link IMHeartbeatInfo} 实例。
     *
     * @param obj 原始数据对象，通常为Map类型
     * @return 转换后的 {@link IMHeartbeatInfo} 对象
     */
    @Override
    public IMHeartbeatInfo transForm(Object obj) {
        Map<?, ?> map = (Map<?, ?>) obj;
        return BeanUtil.fillBeanWithMap(map, new IMHeartbeatInfo(), false);
    }
}
