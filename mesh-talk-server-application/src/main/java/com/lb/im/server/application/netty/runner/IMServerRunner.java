package com.lb.im.server.application.netty.runner;

import com.lb.im.server.application.netty.IMNettyServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PreDestroy;
import java.util.List;

/**
 * Spring组件，负责启动和管理Netty服务器实例。实现CommandLineRunner接口在应用启动时初始化服务器，
 * 并在应用销毁时关闭所有服务器实例。
 */
@Component
public class IMServerRunner implements CommandLineRunner {

    @Autowired
    private List<IMNettyServer> imNettyServers;

    @Override
    public void run(String... args) throws Exception {
        if (!CollectionUtils.isEmpty(imNettyServers)) {
            // 启动所有已注入的Netty服务器实例
            for (IMNettyServer imNettyServer : imNettyServers) {
                imNettyServer.start();
            }
        }
    }

    public boolean isReady() {
        // 遍历检查每个服务器的就绪状态
        for (IMNettyServer imNettyServer : imNettyServers) {
            if (!imNettyServer.isReady()) {
                return false;
            }
        }
        return true;
    }

    @PreDestroy
    public void shutdown() {
        if (!CollectionUtils.isEmpty(imNettyServers)) {
            // 关闭所有已注入的Netty服务器实例
            imNettyServers.forEach(IMNettyServer::shutdown);
        }
    }

}
