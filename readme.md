
# Mesh-Talk-Server: 你的即时通讯利器 🚀

![GitHub stars](https://img.shields.io/github/stars/NaraSky/mesh-talk-server?style=social)
![GitHub forks](https://img.shields.io/github/forks/NaraSky/mesh-talk-server?style=social)
![GitHub issues](https://img.shields.io/github/issues/NaraSky/mesh-talk-server)

---

## 🌟 简介

**Mesh-Talk-Server** 是一个高性能、模块化的即时通讯服务端，专为实时通讯应用打造。无论是私聊、群聊，还是多设备同步，Mesh-Talk-Server 都能轻松应对，让你的通讯体验如丝般顺滑！💬✨

---

## 🎉 核心特性

- **双协议支持**：同时支持 TCP 和 WebSocket，适应不同客户端需求。
- **高并发、低延迟**：基于 Netty 框架，轻松应对大规模用户连接。
- **异步消息处理**：集成 RocketMQ，消息处理如闪电般迅捷。
- **心跳与空闲检测**：智能管理用户在线状态，节省服务器资源。
- **安全认证**：使用 JWT 验证用户身份，保障通讯安全。
- **模块化设计**：清晰的分层结构，易于扩展和维护。

---

## 🛠️ 技术栈

- **后端框架**：Spring Boot
- **网络通信**：Netty
- **消息队列**：RocketMQ
- **缓存**：Redis
- **身份验证**：JWT

---

## 📦 快速开始

### 1. 克隆项目

```bash
git clone https://github.com/yourusername/mesh-talk-server.git
cd mesh-talk-server
```

### 2. 配置环境

- 确保已安装 Java 8+、Maven、Redis 和 RocketMQ。
- 在 `application.yml` 中配置 Redis 和 RocketMQ 连接信息。

### 3. 启动服务

```bash
mvn clean install
mvn spring-boot:run -pl mesh-talk-server-starter
```

### 4. 连接测试

- 使用支持 WebSocket 或 TCP 的客户端连接到服务端。
- 默认端口：WebSocket (`8080`), TCP (`8081`)。

---

## 📚 文档

- [项目结构](#项目结构)
- [配置说明](#配置说明)
- [API 文档](#api-文档)
- [常见问题](#常见问题)

---

## 🤝 贡献

欢迎提交 Issue 和 Pull Request，一起让 Mesh-Talk-Server 变得更好！🎉
