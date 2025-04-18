package com.lb.im.server.application.netty.cache;

import io.netty.channel.ChannelHandlerContext;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户通道上下文缓存，用于存储用户与设备类型对应的通道上下文信息。
 * ChannelHandlerContext是Netty框架中的核心类，表示网络通信通道的处理上下文，用于：
 *  维护通道（Channel）与处理链（Pipeline）的关联关系
 *  转发入站/出站事件到对应的处理器
 *  提供对通道和事件的控制能力（如写数据、绑定端口等）
 */
public class UserChannelContextCache {

    private static Map<Long, Map<Integer, ChannelHandlerContext>> channelMap = new ConcurrentHashMap<>();

    /**
     * 将通道上下文添加到指定用户和设备类型的缓存中。
     *
     * @param userId  用户ID
     * @param channel 设备类型
     * @param ctx     通道上下文
     */
    public static void addChannelCtx(Long userId, Integer channel, ChannelHandlerContext ctx) {
        channelMap.computeIfAbsent(userId, key -> new ConcurrentHashMap<>()).put(channel, ctx);
    }

    /**
     * 根据用户ID和设备类型移除对应的通道上下文。
     *
     * @param userId     用户ID
     * @param terminal 设备类型
     */
    public static void removeChannelCtx(Long userId, Integer terminal) {
        // 检查参数有效性并移除对应的通道上下文
        if (userId != null && terminal != null && channelMap.containsKey(userId)) {
            Map<Integer, ChannelHandlerContext> userChannelMap = channelMap.get(userId);
            if (userChannelMap.containsKey(terminal)) {
                userChannelMap.remove(terminal);
            }
        }
    }

    /**
     * 根据用户ID和设备类型获取对应的通道上下文。
     *
     * @param userId     用户ID
     * @param terminal 设备类型
     * @return 对应的通道上下文，若不存在则返回null
     */
    public static ChannelHandlerContext getChannelCtx(Long userId, Integer terminal) {
        // 检查参数有效性并获取对应的通道上下文
        if (userId != null && terminal != null && channelMap.containsKey(userId)) {
            Map<Integer, ChannelHandlerContext> userChannelMap = channelMap.get(userId);
            if (userChannelMap.containsKey(terminal)) {
                return userChannelMap.get(terminal);
            }
        }
        return null;
    }

    /**
     * 根据用户ID获取该用户的所有设备类型的通道上下文。
     *
     * @param userId 用户ID
     * @return 包含设备类型和通道上下文的映射，若用户不存在则返回null
     */
    public static Map<Integer, ChannelHandlerContext> getChannelCtx(Long userId) {
        if (userId == null) {
            return null;
        }
        return channelMap.get(userId);
    }

}
