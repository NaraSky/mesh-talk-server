package com.lb.im.server.application.netty;

public interface IMNettyServer {

    boolean isReady();

    void start();

    void shutdown();
}
