package com.lb.im.server.application.netty.tcp.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lb.im.common.domain.model.IMSendInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.List;

/**
 * 该编码器将IMSendInfo对象转换为JSON字符串，并封装到WebSocket文本帧中
 */
public class TcpSocketMessageProtocolEncoder extends MessageToMessageEncoder<IMSendInfo> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, IMSendInfo imSendInfo, List<Object> list) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String text = objectMapper.writeValueAsString(imSendInfo);
        TextWebSocketFrame textWebSocketFrame = new TextWebSocketFrame(text);
        list.add(textWebSocketFrame);
    }
}
