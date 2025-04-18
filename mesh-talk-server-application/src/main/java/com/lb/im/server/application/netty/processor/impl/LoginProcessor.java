package com.lb.im.server.application.netty.processor.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.lb.im.common.cache.distribute.DistributedCacheService;
import com.lb.im.common.domain.constans.IMConstants;
import com.lb.im.common.domain.enums.IMCmdType;
import com.lb.im.common.domain.jwt.JwtUtils;
import com.lb.im.common.domain.model.IMLoginInfo;
import com.lb.im.common.domain.model.IMSendInfo;
import com.lb.im.common.domain.model.IMSessionInfo;
import com.lb.im.server.application.netty.cache.UserChannelContextCache;
import com.lb.im.server.application.netty.processor.MessageProcessor;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 登录消息处理器，负责处理用户登录请求的验证、Channel关联维护及异地登录处理。
 * 实现MessageProcessor接口，处理IMLoginInfo类型的消息。
 */
@Component
public class LoginProcessor implements MessageProcessor<IMLoginInfo> {

    private final Logger logger = LoggerFactory.getLogger(LoginProcessor.class);

    @Value("${jwt.accessToken.secret}")
    private String accessTokenSecret;

    @Value("${server.id}")
    private Long serverId;

    @Autowired
    private DistributedCacheService distributedCacheService;

    /**
     * 处理用户登录请求。
     * @param ctx Netty通道上下文
     * @param loginInfo 用户登录信息对象
     */
    @Override
    public synchronized void process(ChannelHandlerContext ctx, IMLoginInfo loginInfo) {
        // 验证登录Token有效性，失败则强制下线
        if (!JwtUtils.checkSign(loginInfo.getAccessToken(), accessTokenSecret)) {
            ctx.channel().close();
            logger.warn("LoginProcessor.process|用户登录信息校验未通过,强制用户下线,token:{}", loginInfo.getAccessToken());
        }

        // 解析Token中的用户会话信息
        String info = JwtUtils.getInfo(loginInfo.getAccessToken());
        IMSessionInfo sessionInfo = JSON.parseObject(info, IMSessionInfo.class);
        if (sessionInfo == null) {
            logger.warn("LoginProcessor.process|转化后的SessionInfo为空");
            return;
        }

        Long userId = sessionInfo.getUserId();
        Integer terminal = sessionInfo.getTerminal();
        logger.info("LoginProcessor.process|用户登录, userId:{}", userId);

        // 检查是否存在同终端的旧登录连接
        ChannelHandlerContext channelCtx = UserChannelContextCache.getChannelCtx(userId, terminal);
        if (channelCtx != null && !channelCtx.channel().id().equals(ctx.channel().id())) {
            // 强制下线旧设备并通知用户
            IMSendInfo<String> imSendInfo = new IMSendInfo<>(IMCmdType.FORCE_LOGOUT.getCode(), "您已在其他地方登录，将被强制下线");
            channelCtx.channel().writeAndFlush(imSendInfo);
            logger.info("LoginProcessor.process|异地登录，强制下线，userid:{}", userId);
        }

        // 缓存用户与Channel的映射关系
        UserChannelContextCache.addChannelCtx(userId, terminal, ctx);

        // 设置用户属性到Channel
        AttributeKey<Long> userIdAttr = AttributeKey.valueOf(IMConstants.USER_ID);
        ctx.channel().attr(userIdAttr).set(userId);
        AttributeKey<Integer> terminalAttr = AttributeKey.valueOf(IMConstants.TERMINAL_TYPE);
        ctx.channel().attr(terminalAttr).set(terminal);

        // 初始化心跳计数器
        AttributeKey<Long> heartbeatAttr = AttributeKey.valueOf(IMConstants.HEARTBEAT_TIMES);
        ctx.channel().attr(heartbeatAttr).set(0L);

        // 记录用户所属服务器ID到分布式缓存
        String redisKey = String.join(IMConstants.REDIS_KEY_SPLIT, IMConstants.IM_USER_SERVER_ID, userId.toString(), terminal.toString());
        distributedCacheService.set(redisKey, serverId, IMConstants.ONLINE_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        // 返回登录成功响应
        IMSendInfo<?> imSendInfo = new IMSendInfo<>();
        imSendInfo.setCmd(IMCmdType.LOGIN.getCode());
        ctx.channel().writeAndFlush(imSendInfo);
    }

    /**
     * 将原始消息对象转换为IMLoginInfo对象。
     * @param obj 原始消息对象（Map类型）
     * @return 转换后的IMLoginInfo实例
     */
    @Override
    public IMLoginInfo transForm(Object obj) {
        Map<?, ?> map = (Map<?, ?>) obj;
        return BeanUtil.fillBeanWithMap(map, new IMLoginInfo(), false);
    }
}
