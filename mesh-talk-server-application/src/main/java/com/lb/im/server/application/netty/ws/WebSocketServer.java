package com.lb.im.server.application.netty.ws;

import com.lb.im.server.application.netty.IMNettyServer;
import com.lb.im.server.application.netty.ws.codec.WebSocketMessageProtocolDecoder;
import com.lb.im.server.application.netty.ws.codec.WebSocketMessageProtocolEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(prefix = "websocket", value = "enable", havingValue = "true", matchIfMissing = true)
public class WebSocketServer implements IMNettyServer {

    private final Logger logger = LoggerFactory.getLogger(WebSocketServer.class);

    private volatile boolean ready = false;

    @Value("${websocket.port}")
    private int port;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workGroup;

    @Override
    public boolean isReady() {
        return ready;
    }

    /**
     * 启动WebSocket服务器，绑定到指定端口并初始化Netty线程组及处理管道。
     * <p>
     * 该方法执行以下操作：
     * 1. 创建Boss和Worker线程组处理网络事件。
     * 2. 配置ChannelInitializer设置消息编解码、协议处理及业务处理器。
     * 3. 绑定服务器到指定端口并等待启动完成。
     */
    @Override
    public void start() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup();
        workGroup = new NioEventLoopGroup();

        // 配置Netty服务器参数，包括线程组、通道类型、处理管道及TCP选项
        bootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    // 添加处理的Handler，通常包括消息编解码、业务处理，也可以是日志、权限、过滤等
                    @Override
                    protected void initChannel(Channel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new IdleStateHandler(120, 0, 0, TimeUnit.SECONDS));
                        pipeline.addLast("http-codec", new HttpServerCodec());
                        pipeline.addLast("aggregator", new HttpObjectAggregator(65535));
                        pipeline.addLast("http-chunked", new ChunkedWriteHandler());
                        pipeline.addLast(new WebSocketServerProtocolHandler("/im"));
                        pipeline.addLast("encode", new WebSocketMessageProtocolEncoder());
                        pipeline.addLast("decode", new WebSocketMessageProtocolDecoder());
                        pipeline.addLast("handler", null);
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 5)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        // 绑定服务器到指定端口并处理启动异常
        try {
            bootstrap.bind(port).sync().channel();
            this.ready = true;
            logger.info("WebSocketServer.start|websocket server 初始化完成,端口：{}", port);
        } catch (InterruptedException e) {
            logger.info("WebSocketServer.start|websocket server 初始化异常", e);
        }
    }

    @Override
    public void shutdown() {
        if (bossGroup != null && !bossGroup.isShuttingDown() && !bossGroup.isShutdown()) {
            bossGroup.shutdownGracefully();
        }
        if (workGroup != null && !workGroup.isShuttingDown() && !workGroup.isShutdown()) {
            workGroup.shutdownGracefully();
        }
        this.ready = false;
        logger.info("WebSocketServer.shutdown|websocket server 停止");
    }
}
