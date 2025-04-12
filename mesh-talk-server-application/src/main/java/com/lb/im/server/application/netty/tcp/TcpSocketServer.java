package com.lb.im.server.application.netty.tcp;

import com.lb.im.server.application.netty.IMNettyServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@ConditionalOnProperty(prefix = "tcpsocket", name = "enable", havingValue = "true", matchIfMissing = true)
public class TcpSocketServer implements IMNettyServer {

    private final Logger logger = LoggerFactory.getLogger(TcpSocketServer.class);

    private volatile boolean ready = false;

    @Value("${tcpsocket.port}")
    private int port;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    @Override
    public boolean isReady() {
        return ready;
    }

    /**
     * 启动TCP服务器，初始化线程组并绑定监听端口。
     * 配置通道处理器和连接选项，设置空闲状态检测。
     */
    @Override
    public void start() {
        ServerBootstrap bootstrap = new ServerBootstrap();
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel channel) throws Exception {
                        ChannelPipeline pipeline = channel.pipeline();
                        // 添加空闲状态处理器，检测读空闲超时（120秒）
                        pipeline.addLast(new IdleStateHandler(120, 0, 0, TimeUnit.SECONDS));
                        // 添加编解码处理器（当前占位符，需替换为实际处理器）
                        pipeline.addLast("encode", null);
                        // 添加业务处理handler（当前占位符，需替换为实际处理器）
                        pipeline.addLast("decode", null);
                        pipeline.addLast("handler", null);
                    }
                })
                // 设置服务器Socket选项：连接队列最大长度
                .option(ChannelOption.SO_BACKLOG, 5)
                // 设置客户端连接保持活动
                .childOption(ChannelOption.SO_KEEPALIVE, true);
        try {
            // 绑定端口并等待启动完成
            bootstrap.bind(port).sync().channel();
            this.ready = true;
            logger.info("TcpSocketServer.start|服务启动正常,监听端口:{}", port);
        } catch (InterruptedException e) {
            // 捕获启动异常并记录
            logger.error("TcpSocketServer.start|服务启动异常:{}", e.getMessage());
        }
    }

    @Override
    public void shutdown() {
        if (bossGroup != null && !bossGroup.isShuttingDown() && !bossGroup.isShutdown()) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null && !workerGroup.isShuttingDown() && !workerGroup.isShutdown()) {
            workerGroup.shutdownGracefully();
        }
        ready = false;
        logger.info("TcpSocketServer shutdown");
    }
}
