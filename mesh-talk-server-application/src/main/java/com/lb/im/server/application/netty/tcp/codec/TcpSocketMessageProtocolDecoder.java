package com.lb.im.server.application.netty.tcp.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lb.im.common.domain.constans.IMConstants;
import com.lb.im.common.domain.model.IMSendInfo;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.ReplayingDecoder;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 该类用于将WebSocket文本帧解码为IMSendInfo对象
 */
public class TcpSocketMessageProtocolDecoder extends ReplayingDecoder {

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {
        if (byteBuf.readableBytes() < IMConstants.MIN_READABLE_BYTES) {
            return;
        }
        //获取数据包长度
        int length = byteBuf.readInt();
        ByteBuf contentBuf = byteBuf.readBytes(length);
        String content = contentBuf.toString(StandardCharsets.UTF_8);
        ObjectMapper objectMapper = new ObjectMapper();
        IMSendInfo imSendInfo = objectMapper.readValue(content, IMSendInfo.class);
        list.add(imSendInfo);
    }
}
