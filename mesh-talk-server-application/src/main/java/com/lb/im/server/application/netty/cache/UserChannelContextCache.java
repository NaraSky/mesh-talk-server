package com.lb.im.server.application.netty.cache;

import io.netty.channel.ChannelHandlerContext;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 用户通道上下文缓存，用于存储用户与设备类型对应的通道上下文信息。
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
     * @param deviceType 设备类型
     */
    public static void removeChannelCtx(Long userId, Integer deviceType) {
        // 检查参数有效性并移除对应的通道上下文
        if (userId != null && deviceType != null && channelMap.containsKey(userId)) {
            Map<Integer, ChannelHandlerContext> userChannelMap = channelMap.get(userId);
            if (userChannelMap.containsKey(deviceType)) {
                userChannelMap.remove(deviceType);
            }
        }
    }

    /**
     * 根据用户ID和设备类型获取对应的通道上下文。
     *
     * @param userId     用户ID
     * @param deviceType 设备类型
     * @return 对应的通道上下文，若不存在则返回null
     */
    public static ChannelHandlerContext getChannelCtx(Long userId, Integer deviceType) {
        // 检查参数有效性并获取对应的通道上下文
        if (userId != null && deviceType != null && channelMap.containsKey(userId)) {
            Map<Integer, ChannelHandlerContext> userChannelMap = channelMap.get(userId);
            if (userChannelMap.containsKey(deviceType)) {
                return userChannelMap.get(deviceType);
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
