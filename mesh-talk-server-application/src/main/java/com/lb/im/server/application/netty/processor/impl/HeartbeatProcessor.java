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
 * 处理心跳消息的处理器，实现IMHeartbeatInfo类型消息的处理逻辑
 * 负责维护心跳计数并周期性更新用户在线状态
 */
@Component
public class HeartbeatProcessor implements MessageProcessor<IMHeartbeatInfo> {

    @Autowired
    private DistributedCacheService distributedCacheService;

    @Value("${heartbeat.count}")
    private Integer heartbeatCount;

    @Override
    public void process(ChannelHandlerContext ctx, IMHeartbeatInfo data) {
        // 响应WebSocket客户端
        responseWS(ctx);

        // 更新心跳计数器
        AttributeKey<Long> heartBeatAttr = AttributeKey.valueOf(IMConstants.HEARTBEAT_TIMES);
        Long heartBeatTimes = ctx.channel().attr(heartBeatAttr).get();
        ctx.channel().attr(heartBeatAttr).set(++heartBeatTimes);

        // 当达到指定心跳次数阈值时更新用户在线状态
        if (heartBeatTimes % heartbeatCount == 0) {
            AttributeKey<Long> userIdAttr = AttributeKey.valueOf(IMConstants.USER_ID);
            Long userId = ctx.channel().attr(userIdAttr).get();
            AttributeKey<Integer> terminalAttr = AttributeKey.valueOf(IMConstants.TERMINAL_TYPE);
            Integer terminal = ctx.channel().attr(terminalAttr).get();
            String redisKey = String.join(IMConstants.REDIS_KEY_SPLIT,
                                         IMConstants.IM_USER_SERVER_ID,
                                         userId.toString(),
                                         terminal.toString());
            distributedCacheService.expire(redisKey, IMConstants.ONLINE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        }
    }

    /**
     * 向WebSocket客户端发送心跳响应
     * @param ctx Netty通道上下文
     */
    public void responseWS(ChannelHandlerContext ctx) {
        IMSendInfo<?> imSendInfo = new IMSendInfo<>();
        imSendInfo.setCmd(IMCmdType.HEART_BEAT.getCode());
        ctx.channel().writeAndFlush(imSendInfo);
    }

    @Override
    public IMHeartbeatInfo transForm(Object obj) {
        Map<?, ?> map = (Map<?, ?>) obj;
        return BeanUtil.fillBeanWithMap(map, new IMHeartbeatInfo(), false);
    }
}
