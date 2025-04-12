package com.lb.im.server.application.netty.processor;

import io.netty.channel.ChannelHandlerContext;

/**
 * 消息处理器接口，用于处理不同类型的消息数据。
 */
public interface MessageProcessor<T> {

    /**
     * 处理接收到的消息数据。
     */
    default void process(ChannelHandlerContext ctx, T data) {
    }

    /**
     */
    default void process(T data) {

    }

    /**
     * 将对象转换为指定的泛型类型T。
     */
    default T transForm(Object obj) {
        return (T) obj;
    }
}
