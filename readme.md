# IM Server Project

## 概述

IM Server Project 是一个高性能的即时通讯服务端项目，基于Netty框架和RocketMQ消息队列实现。支持TCP和WebSocket协议，提供用户登录、心跳维护、私有消息和群组消息等功能，适用于构建实时聊天应用。

## 功能模块

- **用户登录**：支持JWT令牌验证，管理用户会话和多设备登录。
- **心跳维护**：检测客户端存活状态，自动清理超时连接。
- **消息发送**：支持点对点私有消息和群组消息，异步处理确保高吞吐量。
- **连接管理**：管理用户连接生命周期，实时更新在线状态。

## 技术栈

- **Netty**: 高性能网络框架，处理TCP和WebSocket连接。
- **RocketMQ**: 分布式消息队列，实现消息异步处理。
- **Spring Boot**: 简化应用开发与部署。
- **JWT**: 用户身份验证。
- **Redis**: 分布式缓存，存储用户在线状态。

## 项目结构

- **`cache`**: 用户通道上下文缓存。
- **`consumer`**: RocketMQ消息消费者。
- **`processor`**: 消息处理器，处理登录、心跳、私聊、群聊等逻辑。
- **`netty`**: Netty服务器实现（TCP和WebSocket）。
- **`codec`**: 消息编解码器。
- **`runner`**: 应用启动器，管理Netty服务器生命周期。

