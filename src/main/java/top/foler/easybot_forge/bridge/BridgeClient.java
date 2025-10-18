package top.foler.easybot_forge.bridge;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import top.foler.easybot_forge.bridge.adapter.OpCodeAdapter;
import top.foler.easybot_forge.bridge.packet.*;
import top.foler.easybot_forge.bridge.model.PlayerInfo;
import top.foler.easybot_forge.bridge.model.ServerInfo;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class BridgeClient {
    private static final Logger logger = LogUtils.getLogger();
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(OpCode.class, new OpCodeAdapter())
            .create();
    private volatile WebSocketClient client;
    private final ExecutorService executor;
    private final BridgeBehavior behavior;
    private final Object connectionLock = new Object();
    private final ConcurrentHashMap<String, CompletableFuture<String>> callbackTasks = new ConcurrentHashMap<>();
    private final ScheduledExecutorService timeoutScheduler = Executors.newSingleThreadScheduledExecutor();
    private final ScheduledExecutorService scheduler;
    private final long timeoutSeconds = 15;
    private int reconnectAttempts = 0;
    private static final int MAX_RECONNECT_ATTEMPTS = 10; // 最大重试次数
    private static final long INITIAL_RECONNECT_DELAY = 5; // 初始重试延迟（秒）
    private static final long MAX_RECONNECT_DELAY = 60; // 最大重试延迟（秒）
    private static final double BACKOFF_MULTIPLIER = 1.5; // 退避乘数
    private IdentifySuccessPacket identifySuccessPacket;
    private String token;
    private String uri;
    private boolean isConnected = false;
    private ScheduledExecutorService heartbeatScheduler = Executors.newSingleThreadScheduledExecutor();
    private boolean ready;
    private int heartbeatInterval = 120;
    
    /**
     * 启动心跳包发送
     */
    private void startHeartbeat() {
        heartbeatScheduler.scheduleAtFixedRate(() -> {
            try {
                if (client != null && client.isOpen()) {
                    sendPacket(new HeartbeatPacket());
                }
            } catch (Exception e) {
                logger.warn("发送心跳包时出错: " + e.getMessage());
            }
        }, heartbeatInterval, heartbeatInterval, TimeUnit.SECONDS);
    }
    
    // Getters and Setters
    public static Gson getGson() {
        return gson;
    }
    
    public IdentifySuccessPacket getIdentifySuccessPacket() {
        return identifySuccessPacket;
    }
    
    public void setIdentifySuccessPacket(IdentifySuccessPacket identifySuccessPacket) {
        this.identifySuccessPacket = identifySuccessPacket;
    }
    
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public boolean isReady() {
        return ready;
    }
    
    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    /**
     * 检查连接是否已建立
     */
    public boolean isConnected() {
        return client != null && isConnected && client.isOpen();
    }

    public BridgeClient(String uri, BridgeBehavior behavior) {
        this.uri = uri;
        this.behavior = behavior;
        this.executor = Executors.newSingleThreadExecutor();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        
        // 创建第一个client实例
        createNewClient();
        
        // 只在client不为null时尝试连接
        if (client != null) {
            connect();
        }
    }
    
    /**
     * 创建新的WebSocketClient实例
     */
    private void createNewClient() {
        try {
            this.client = new WebSocketClient(new URI(uri)) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    logger.info("已成功连接到服务器!");
                    synchronized (connectionLock) {
                        isConnected = true;
                        reconnectAttempts = 0; // 连接成功后重置重试计数
                    }
                }

                @Override
                public void onMessage(String message) {
                    BridgeClient.this.onMessage(message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    logger.info("连接已关闭: " + code + " - " + reason);
                    synchronized (connectionLock) {
                        isConnected = false;
                        ready = false;
                    }
                    // 尝试重新连接 - 先创建新实例再连接
                    attemptReconnect();
                }

                @Override
                public void onError(Exception ex) {
                    logger.error("发生错误: " + ex.getMessage());
                    ex.printStackTrace();
                }
            };
        } catch (URISyntaxException e) {
            logger.error("无效的WebSocket URI: " + e.getMessage());
            this.client = null;
        }
    }

    public <T> CompletableFuture<T> sendAndWaitForCallbackAsync(PacketWithCallBackId packet, Class<T> responseType) {
        String callbackId = UUID.randomUUID().toString();
        packet.setCallBackId(callbackId);

        CompletableFuture<String> future = new CompletableFuture<>();
        callbackTasks.put(callbackId, future);

        send(gson.toJson(packet));

        ScheduledFuture<?> timeoutFuture = timeoutScheduler.schedule(() -> {
            CompletableFuture<String> removedFuture = callbackTasks.remove(callbackId);
            if (removedFuture != null) {
                removedFuture.completeExceptionally(new TimeoutException("等待EasyBot返回结果超时!"));
            }
        }, timeoutSeconds, TimeUnit.SECONDS);

        return future.thenApply(result -> {
            timeoutFuture.cancel(false);
            return gson.fromJson(result, responseType);
        }).exceptionally(ex -> {
            timeoutFuture.cancel(false);
            throw new RuntimeException("Error waiting for callback", ex);
        });
    }

    /**
     * 建立WebSocket连接
     */
    private void connect() {
        WebSocketClient currentClient = client; // 避免在执行过程中client被替换
        if (currentClient != null) {
            executor.execute(() -> {
                try {
                    logger.info("尝试连接到服务器，当前重试次数: " + reconnectAttempts);
                    currentClient.connect();
                } catch (Exception e) {
                    logger.error("连接失败: " + e.getMessage());
                    e.printStackTrace();
                    // 连接失败后创建新实例并重新连接
                    attemptReconnect();
                }
            });
        }
    }
    
    /**
     * 尝试重新连接，使用指数退避策略
     */
    private void attemptReconnect() {
        reconnectAttempts++;
        
        if (reconnectAttempts > MAX_RECONNECT_ATTEMPTS) {
            logger.warn("已达到最大重试次数 (" + MAX_RECONNECT_ATTEMPTS + ")，将在冷却后继续尝试");
            reconnectAttempts = 0; // 重置重试计数
        }
        
        // 计算退避延迟时间
        long delaySeconds = (long) Math.min(
            INITIAL_RECONNECT_DELAY * Math.pow(BACKOFF_MULTIPLIER, reconnectAttempts - 1),
            MAX_RECONNECT_DELAY
        );
        
        logger.info("将在 " + delaySeconds + " 秒后尝试重新连接 (第 " + reconnectAttempts + " 次尝试)");
        
        scheduler.schedule(() -> {
            createNewClient();
            connect();
        }, delaySeconds, TimeUnit.SECONDS);
    }

    public void send(String message) {
        if (client != null && client.isOpen()) {
            client.send(message);
        }
    }

    public void onMessage(String message) {
        if (top.foler.easybot_forge.bridge.ClientProfile.isDebugMode()) {
            logger.info("收到消息: " + message);
        }
        Gson gson = this.gson;
        Packet packet = gson.fromJson(message, Packet.class);
        switch (packet.getOpCode()) {
            case Hello:
                HelloPacket helloPacket = gson.fromJson(message, HelloPacket.class);

                logger.info("已连接到主程序!");
                logger.info(">>>主程序连接信息<<<");
                logger.info("系统: " + helloPacket.getSystemName());
                logger.info("运行时版本: " + helloPacket.getDotnetVersion());
                logger.info("主程序版本: " + helloPacket.getVersion());
                logger.info("连接信息: " + helloPacket.getSessionId() + " (心跳:" + helloPacket.getInterval() + "s)");
                logger.info(">>>服务器信息<<<");
                logger.info("令牌: " + getToken());
                logger.info("服务器: " + top.foler.easybot_forge.bridge.ClientProfile.getServerDescription());
                logger.info("插件版本: " + top.foler.easybot_forge.bridge.ClientProfile.getPluginVersion());
                logger.info("支持命令: " + top.foler.easybot_forge.bridge.ClientProfile.isCommandSupported());
                logger.info("变量支持:" + top.foler.easybot_forge.bridge.ClientProfile.isPapiSupported());
                logger.info(">>>准备上传<<<");
                logger.info("上报身份中...");

                heartbeatInterval = helloPacket.getInterval() - 10;
                sendIdentifyPacket();
                break;
            case IdentifySuccess:
                IdentifySuccessPacket identifySuccessPacket = gson.fromJson(message, IdentifySuccessPacket.class);
                logger.info("身份验证成功! 服务器名: " + identifySuccessPacket.getServerName());
                logger.info("已连接到主程序!");
                startUpdateSyncSettings();
                startHeartbeat();
                ready = true;
                break;
            case Packet:
                handlePacket(message);
                break;
            case CallBack:
                PacketWithCallBackId packetWithCallBackId = gson.fromJson(message, PacketWithCallBackId.class);
                if (packetWithCallBackId.getCallBackId() != null) {
                    CompletableFuture<String> future = callbackTasks.remove(packetWithCallBackId.getCallBackId());
                    if (future != null) {
                        future.complete(message);
                    }
                }
                break;
            case HeartBeat:
                // 处理心跳包
                sendPacket(new HeartbeatPacket());
                break;
        }
    }

    private void handlePacket(String message) {
        Gson gson = this.gson;
        PacketWithCallBackId packet = gson.fromJson(message, PacketWithCallBackId.class);
        JsonObject callBack = new JsonObject();
        callBack.addProperty("op", OpCode.CallBack.getValue());
        callBack.addProperty("callback_id", packet.getCallBackId());
        callBack.addProperty("exec_op", packet.getOperation());

        switch (packet.getOperation()) {
            case "GET_SERVER_INFO":
            case "GET_INFO":
                ServerInfo info = behavior.getInfo();
                // 直接合并信息到回调对象
                callBack.addProperty("server_name", info.getServerName());
                callBack.addProperty("server_version", info.getServerVersion());
                // 添加必要的字段以避免反序列化错误
                callBack.addProperty("plugin_version", info.getPluginVersion());
                callBack.addProperty("is_papi_supported", info.isPapiSupported());
                callBack.addProperty("is_command_supported", info.isCommandSupported());
                // 通过behavior对象获取在线人数和最大人数
                callBack.addProperty("online_count", behavior.getOnlinePlayerCount());
                callBack.addProperty("max_players", behavior.getMaxPlayers());
                break;
            case "UN_BIND_NOTIFY":
                PlayerUnBindNotifyPacket unBindNotifyPacket = gson.fromJson(message, PlayerUnBindNotifyPacket.class);
                behavior.KickPlayer(unBindNotifyPacket.getPlayerName(), unBindNotifyPacket.getKickMessage());
                break;
            case "BIND_SUCCESS_NOTIFY":
                BindSuccessNotifyPacket bindSuccessNotifyPacket = gson.fromJson(message, BindSuccessNotifyPacket.class);
                behavior.BindSuccessBroadcast(bindSuccessNotifyPacket.getPlayerName(), bindSuccessNotifyPacket.getAccountId(), bindSuccessNotifyPacket.getAccountName());
                break;
            case "PLACEHOLDER_API_QUERY":
                PlaceholderApiQueryPacket placeholderApiQueryPacket = gson.fromJson(message, PlaceholderApiQueryPacket.class);
                try {
                    String papiQueryResult = behavior.papiQuery(placeholderApiQueryPacket.getPlayerName(), placeholderApiQueryPacket.getText());
                    callBack.addProperty("success", true);
                    callBack.addProperty("text", papiQueryResult);
                } catch (Exception ex) {
                    callBack.addProperty("success", false);
                    callBack.addProperty("text", ex.getLocalizedMessage());
                    logger.error("执行Papi查询命令失败: " + ex);
                }
                break;
            case "RUN_COMMAND":
                RunCommandPacket runCommandPacket = gson.fromJson(message, RunCommandPacket.class);
                try {
                    String runCommandResult = behavior.runCommand(runCommandPacket.getPlayerName(), runCommandPacket.getCommand(), runCommandPacket.isEnablePapi());
                    callBack.addProperty("success", !runCommandResult.contains("失败") && !runCommandResult.contains("错误"));
                    callBack.addProperty("text", runCommandResult);
                } catch (Exception ex) {
                    callBack.addProperty("success", false);
                    callBack.addProperty("text", ex.getLocalizedMessage());
                    logger.error("执行命令失败: " + ex);
                }
                break;
            case "SEND_TO_CHAT":
                SendToChatOldPacket sendToChatPacket = gson.fromJson(message, SendToChatOldPacket.class);
                JsonObject sendToChatPacketRaw = gson.fromJson(message, JsonObject.class);
                JsonElement extra = sendToChatPacketRaw.get("extra");
                if (extra == null || extra.isJsonNull()) {
                    behavior.SyncToChat(sendToChatPacket.getText());
                } else {
                    // 处理带extra的消息（简化版本）
                    behavior.SyncToChat(sendToChatPacket.getText());
                }
                break;
            case "SYNC_SETTINGS_UPDATED":
                UpdateSyncSettingsPacket updateSyncSettingsPacket = gson.fromJson(message, UpdateSyncSettingsPacket.class);
                // 进行类型转换
                top.foler.easybot_forge.bridge.ClientProfile.setSyncMessageMoney(updateSyncSettingsPacket.getSyncMoney() != 0);
                top.foler.easybot_forge.bridge.ClientProfile.setSyncMessageMode(String.valueOf(updateSyncSettingsPacket.getSyncMode()));
                break;
            case "PLAYER_LIST":
            case "GET_PLAYER_LIST":
                List<PlayerInfo> playerList = behavior.getPlayerList();
                // 转换为JSON数组
                JsonArray playerArray = new JsonArray();
                for (PlayerInfo playerInfo : playerList) {
                    JsonObject playerObj = new JsonObject();
                    playerObj.addProperty("player_name", playerInfo.getPlayerName());
                    playerObj.addProperty("player_uuid", playerInfo.getPlayerUuid());
                    playerObj.addProperty("ip", playerInfo.getIp());
                    playerObj.addProperty("skin_url", playerInfo.getSkinUrl());
                    playerObj.addProperty("is_bedrock_player", playerInfo.isBedrock());
                    playerArray.add(playerObj);
                }
                callBack.add("list", playerArray);
                break;
            default:
                logger.info("收到未知操作: " + packet.getOperation() + " 请确保你的插件是最新版本");
                break;
        }

        send(gson.toJson(callBack));
    }

    private void sendIdentifyPacket() {
        Gson gson = this.gson;
        IdentifyPacket packet = new IdentifyPacket(getToken());
        packet.setPluginVersion(top.foler.easybot_forge.bridge.ClientProfile.getPluginVersion());
        packet.setServerDescription(top.foler.easybot_forge.bridge.ClientProfile.getServerDescription());
        packet.setPapiSupported(top.foler.easybot_forge.bridge.ClientProfile.isPapiSupported());
        packet.setCommandSupported(top.foler.easybot_forge.bridge.ClientProfile.isCommandSupported());
        send(gson.toJson(packet));
    }

    public void startUpdateSyncSettings() {
        NeedSyncSettingsPacket packet = new NeedSyncSettingsPacket();
        packet.setCallBackId("");
        send(gson.toJson(packet));
    }

    public void sendPacket(Packet packet) {
        String json = gson.toJson(packet);
        send(json);
    }
    
    // 专门处理IdentifyPacket类型的方法
    public void sendPacket(IdentifyPacket packet) {
        String json = gson.toJson(packet);
        send(json);
    }
    
    // 专门处理HeartbeatPacket类型的方法
    public void sendPacket(HeartbeatPacket packet) {
        String json = gson.toJson(packet);
        send(json);
    }


}