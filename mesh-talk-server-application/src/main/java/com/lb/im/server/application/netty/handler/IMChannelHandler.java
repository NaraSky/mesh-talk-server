package com.lb.im.server.application.netty.handler;

import com.lb.im.common.cache.distribute.DistributedCacheService;
import com.lb.im.common.domain.constans.IMConstants;
import com.lb.im.common.domain.model.IMSendInfo;
import com.lb.im.server.application.netty.cache.UserChannelContextCache;
import com.lb.im.server.holder.SpringContextHolder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * IM消息通道处理器，负责处理IM消息的通道事件，管理用户连接的生命周期，处理心跳超时等异常场景。
 * 继承自SimpleChannelInboundHandler以处理IMSendInfo类型的消息。
 */
public class IMChannelHandler extends SimpleChannelInboundHandler<IMSendInfo> {

    private final Logger logger = LoggerFactory.getLogger(IMChannelHandler.class);

    /**
     * channelRead0：用于处理接收到的消息，例如登录请求或心跳消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, IMSendInfo imSendInfo) throws Exception {
        //TODO 处理登录和心跳消息
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("IMChannelHandler.exceptionCaught|异常:{}", cause.getMessage());
    }

    /**
     * handlerAdded：当用户连接时触发，可以记录连接信息
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        logger.info("IMChannelHandler.handlerAdded|{}连接", ctx.channel().id().asLongText());
    }

    /**
     * handlerRemoved：当用户断开连接时触发，可以清理缓存和分布式缓存中的连接信息
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        AttributeKey<Long> userIdAttr = AttributeKey.valueOf(IMConstants.USER_ID);
        Long userId = ctx.channel().attr(userIdAttr).get();

        AttributeKey<Integer> terminalAttr = AttributeKey.valueOf(IMConstants.TERMINAL_TYPE);
        Integer terminal = ctx.channel().attr(terminalAttr).get();

        ChannelHandlerContext channelCtx = UserChannelContextCache.getChannelCtx(userId, terminal);
        // 验证当前通道是否为目标通道，防止异地登录场景误删其他有效连接
        if (channelCtx != null && channelCtx.channel().id().equals(ctx.channel().id())) {
            // 执行资源清理操作：移除本地缓存记录、删除分布式缓存标识并记录日志
            UserChannelContextCache.removeChannelCtx(userId, terminal);
            DistributedCacheService distributedCacheService = SpringContextHolder.getBean(IMConstants.DISTRIBUTED_CACHE_REDIS_SERVICE_KEY);
            String redisKey = String.join(IMConstants.REDIS_KEY_SPLIT, IMConstants.IM_USER_SERVER_ID, userId.toString(), terminal.toString());
            distributedCacheService.delete(redisKey);
            logger.info("IMChannelHandler.handlerRemoved|断开连接, userId:{}, 终端类型:{}", userId, terminal);
        }
    }

    /**
     * 处理通道中的用户事件触发。主要处理空闲状态事件，当检测到读空闲超时时关闭连接。
     *
     * @param ctx 通道处理上下文，用于访问通道和事件信息
     * @param evt 触发的事件对象，可能包含空闲状态事件或其他类型事件
     * @throws Exception 可能抛出的异常由父类方法定义
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 处理空闲状态事件
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            // 处理读空闲超时事件
            if (state == IdleState.READER_IDLE) {
                AttributeKey<Long> attr = AttributeKey.valueOf(IMConstants.USER_ID);
                Long userId = ctx.channel().attr(attr).get();

                AttributeKey<Integer> terminalAttr = AttributeKey.valueOf(IMConstants.TERMINAL_TYPE);
                Integer terminal = ctx.channel().attr(terminalAttr).get();
                logger.info("IMChannelHandler.userEventTriggered|心跳超时.即将断开连接, userId:{}, 终端类型:{}", userId, terminal);
                ctx.channel().close();
            }
        } else {
            // 转发其他类型事件给父类处理
            super.userEventTriggered(ctx, evt);
        }
    }

}
