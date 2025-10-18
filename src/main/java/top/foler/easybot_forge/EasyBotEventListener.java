package top.foler.easybot_forge;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.foler.easybot_forge.bridge.packet.PlayerInfoWithRaw;
import net.minecraftforge.fml.common.Mod;
import top.foler.easybot_forge.bridge.BridgeClient;
import top.foler.easybot_forge.bridge.packet.SyncMessagePacket;
import top.foler.easybot_forge.bridge.packet.PlayerJoinPacket;
import top.foler.easybot_forge.bridge.packet.PlayerLeavePacket;
import top.foler.easybot_forge.bridge.packet.SyncDeathMessagePacket;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

/**
 * EasyBot事件监听器，用于处理游戏事件并同步到机器人
 */
@Mod.EventBusSubscriber(modid = Easybot_forge.MODID)
public class EasyBotEventListener {
    private static final Logger logger = LogUtils.getLogger();
    private static BridgeClient bridgeClient;
    private static MinecraftServer server;

    /**
     * 设置Bridge客户端
     */
    public static void setBridgeClient(BridgeClient client) {
        bridgeClient = client;
    }

    /**
     * 设置Minecraft服务器实例
     */
    public static void setServer(MinecraftServer minecraftServer) {
        server = minecraftServer;
    }

    /**
     * 处理服务器启动事件
     */
    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        server = event.getServer();
        if (Config.isDebug()) {
            logger.info("EasyBot事件监听器已初始化");
        }
    }

    /**
     * 处理玩家聊天事件
     */
    @SubscribeEvent
    public static void onPlayerChat(ServerChatEvent event) {
        if (bridgeClient != null && bridgeClient.isConnected()) {
            String message = event.getMessage().getString();
            String playerName = event.getPlayer().getGameProfile().getName();
            
            // 创建并发送同步消息包
            SyncMessagePacket packet = new SyncMessagePacket();
            PlayerInfoWithRaw playerInfo = new PlayerInfoWithRaw();
            playerInfo.setName(playerName);
            packet.setPlayer(playerInfo);
            packet.setMessage(message);
            bridgeClient.sendPacket(packet);
        }
    }

    /**
     * 处理玩家加入事件
     */
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (bridgeClient != null && bridgeClient.isConnected()) {
            // 获取玩家信息
            String playerName = event.getEntity().getGameProfile().getName();
            String playerUUID = event.getEntity().getUUID().toString();
            
            // 创建并发送玩家加入包
            PlayerJoinPacket packet = new PlayerJoinPacket(playerName, playerUUID);
            bridgeClient.sendPacket(packet);
        }
    }

    /**
     * 处理玩家离开事件
     */
    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        if (bridgeClient != null && bridgeClient.isConnected()) {
            // 获取玩家信息
            String playerName = event.getEntity().getGameProfile().getName();
            String playerUUID = event.getEntity().getUUID().toString();
            
            // 创建并发送玩家离开包
            PlayerLeavePacket packet = new PlayerLeavePacket(playerName, playerUUID);
            bridgeClient.sendPacket(packet);
        }
    }

    /**
     * 处理玩家死亡事件
     */
    @SubscribeEvent
    public static void onPlayerDeath(PlayerEvent.PlayerRespawnEvent event) {
        // 注意：在Forge中，玩家死亡后会触发respawn事件，可以在这里处理死亡消息
        // 但要准确获取死亡原因，可能需要监听LivingDeathEvent
        if (bridgeClient != null && bridgeClient.isConnected()) {
            // 获取玩家信息
            String playerName = event.getEntity().getGameProfile().getName();
            
            // 创建并发送死亡消息包
            SyncDeathMessagePacket packet = new SyncDeathMessagePacket();
            PlayerInfoWithRaw playerInfo = new PlayerInfoWithRaw();
            playerInfo.setName(playerName);
            packet.setPlayer(playerInfo);
            packet.setRaw("玩家死亡了");
            bridgeClient.sendPacket(packet);
        }
    }
}