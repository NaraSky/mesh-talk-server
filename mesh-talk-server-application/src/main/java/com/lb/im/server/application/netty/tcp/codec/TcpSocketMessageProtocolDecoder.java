package com.lb.im.server.application.netty.tcp.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lb.im.common.domain.model.IMSendInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.util.List;

/**
 * 该类用于将WebSocket文本帧解码为IMSendInfo对象
 */
public class TcpSocketMessageProtocolDecoder extends MessageToMessageDecoder<TextWebSocketFrame> {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, TextWebSocketFrame textWebSocketFrame, List<Object> list) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        IMSendInfo imSendInfo = objectMapper.readValue(textWebSocketFrame.text(), IMSendInfo.class);
        list.add(imSendInfo);
    }
}
