# Easybot-Forge

Easybot-Forge 是一个为 Minecraft Forge 服务端开发的通信模组，用于实现服务端与外部应用程序之间的信息交互。

## 项目介绍

Easybot-Forge 是 Easybot 项目的 Forge 版本实现，专为 Minecraft Forge 服务端设计，提供了稳定的通信接口，使服务端能够与外部系统进行数据交换。

## 功能特性

- **服务端通信**：通过 WebSocket 实现与外部应用的实时通信
- **命令执行**：支持执行服务器命令并返回详细结果
- **玩家信息获取**：可以获取在线玩家列表和信息
- **消息同步**：支持将游戏内消息同步到外部应用
- **跨版本支持**：兼容 Minecraft 1.20.x 到 1.21.x 版本

## 技术规格

- **支持的 Minecraft 版本**：1.20.x - 1.21.x
- **支持的 Forge 版本**：47.x (1.20.x) - 52.x (1.21.x)
- **开发语言**：Java
- **核心依赖**：
  - Java-WebSocket 1.6.0
  - Gson 2.10.1

## 安装要求

1. Minecraft Forge 服务器（版本 47.x 到 52.x）
2. Java 17 或更高版本

## 安装方法
1. 阅读[Easybot文档](https://docs.inectar.cn/)
2. 下载最新版本的 Easybot-Forge 模组文件（.jar）
3. 将模组文件放入服务器的 `mods` 文件夹中
4. 启动服务器，模组将自动加载
5. 配置相关参数（WebSocket 连接信息等）

## 使用说明

### 配置文件

首次启动后，模组会生成配置文件。请根据需要修改配置文件中的相关参数，特别是 WebSocket 连接信息。

### 命令执行

模组支持通过 WebSocket 接口执行服务器命令，命令执行结果将作为响应返回。


## 构建说明

如需自行构建模组，请按照以下步骤操作：

1. 克隆项目仓库
2. 确保已安装 JDK 21
3. 使用 Gradle 构建项目：
   ```bash
   ./gradlew build
   ```
4. 构建完成后，模组文件将位于 `build/libs` 目录中

## 兼容性说明

- 与绝大多数forge-mod兼容

## 问题反馈

如遇任何问题或有功能建议，请在项目仓库提交 issue。

## 许可证

本项目采用 MIT 许可证 - 详见 LICENSE 文件

---

*感谢使用 Easybot-Forge 模组！*