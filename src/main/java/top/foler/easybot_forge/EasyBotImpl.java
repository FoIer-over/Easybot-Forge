package top.foler.easybot_forge;

import com.mojang.brigadier.ParseResults;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;
import top.foler.easybot_forge.bridge.BridgeBehavior;
import top.foler.easybot_forge.bridge.ClientProfile;
import java.util.ArrayList;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import top.foler.easybot_forge.bridge.message.Segment;
import top.foler.easybot_forge.bridge.model.PlayerInfo;
import top.foler.easybot_forge.bridge.model.ServerInfo;

import java.util.ArrayList;
import java.util.List;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class EasyBotImpl implements BridgeBehavior {
    private final Logger logger = LogUtils.getLogger();
    private final Easybot_forge mod;
    private MinecraftServer server; // 改为非final，允许后续更新

    public EasyBotImpl(Easybot_forge mod) {
        this.mod = mod;
        this.server = ServerLifecycleHooks.getCurrentServer(); // 初始赋值，可能为null
    }
    
    /**
     * 更新服务器实例
     * 当服务器启动后调用此方法设置正确的服务器实例
     */
    public void updateServer(MinecraftServer server) {
        this.server = server;
        logger.info("服务器实例已更新");
    }

    @Override
    public String runCommand(String playerName, String command, boolean enablePapi) {
        if (!ClientProfile.isCommandSupported()) {
            logger.warn("无法执行命令: 此服务端不支持执行命令!");
            return "无法执行命令: 此服务端不支持执行命令!";
        }

        if (!ClientProfile.isPapiSupported() && enablePapi) {
            logger.warn("无法执行EasyBot主程序传来的命令,服务器未安装PlaceholderApi!");
            return "无法执行命令: 服务器未安装PlaceholderApi!";
        }

        // 在Forge中执行命令 - 捕获输出结果
        try {
            // 创建一个自定义的命令输出接收器
            List<String> messages = new ArrayList<>();
            
            // 使用反射尝试模拟Bukkit版本的行为
            try {
                // 尝试通过反射调用runCommand方法，如果服务器支持的话
                Method runCommandMethod = server.getClass().getMethod("runCommand", String.class);
                Object result = runCommandMethod.invoke(server, command);
                if (result instanceof String) {
                    return (String) result;
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
                // 忽略反射错误，继续尝试其他方法
            }
            
            // 获取命令管理器和命令执行源
            net.minecraft.commands.Commands commands = server.getCommands();
            net.minecraft.commands.CommandSourceStack source = server.createCommandSourceStack();
            
            // 解析命令
            ParseResults<net.minecraft.commands.CommandSourceStack> parseResults =
                commands.getDispatcher().parse(command, source);
            
            // 执行命令
            commands.performCommand(parseResults, command);
            
            // 由于无法直接捕获输出，我们尝试从日志中获取或返回通用信息
            // 这里我们可以添加更复杂的日志解析逻辑，但为了简单起见，先返回基本信息
            logger.info("命令已执行: {}", command);
            return "命令执行成功: " + command;
        } catch (Exception e) {
            logger.warn("执行命令时出错: " + e.getMessage());
            return "执行命令时出错: " + e.getMessage();
        }
    }

    @Override
    public String papiQuery(String playerName, String query) {
        // Forge版本可能需要适配PlaceholderAPI或提供类似功能
        return query;
    }

    @Override
    public ServerInfo getInfo() {
        ServerInfo info = new ServerInfo();
        
        // 添加空值检查，防止server为null时抛出异常
        if (server != null) {
            // 使用服务器配置中的真实名称
            info.setServerName(server.getMotd());
            info.setServerVersion(server.getServerVersion());
            info.setOnlineMode(server.usesAuthentication());
        } else {
            // server为null时使用默认值
            logger.warn("服务器实例为null，使用默认服务器信息");
            info.setServerName("Unknown");
            info.setServerVersion("Unknown");
            info.setOnlineMode(false);
        }
        
        // 从mod对象获取版本信息，而不是硬编码
        info.setPluginVersion(mod.getModVersion());
        info.setCommandSupported(ClientProfile.isCommandSupported());
        info.setPapiSupported(ClientProfile.isPapiSupported());
        info.setHasGeyser(ClientProfile.isHasGeyser());
        
        return info;
    }

    @Override
    public void SyncToChat(String message) {
        logger.info(message);
        server.execute(() -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                // 使用Forge推荐的sendMessage方法调用方式
                player.sendSystemMessage(net.minecraft.network.chat.Component.literal(message));
            }
        });
    }

    @Override
    public int getOnlinePlayerCount() {
        return server.getPlayerList().getPlayerCount();
    }

    @Override
    public int getMaxPlayers() {
        return server.getPlayerList().getMaxPlayers();
    }

    @Override
    public void BindSuccessBroadcast(String playerName, String accountId, String accountName) {
        server.execute(() -> {
            ServerPlayer onlinePlayer = server.getPlayerList().getPlayerByName(playerName);
            if (onlinePlayer != null) {
                String message = "[!] 绑定 #account (#name) 成功!"
                        .replace("#account", accountId)
                        .replace("#name", accountName);
                // 使用Forge推荐的sendSystemMessage方法
                onlinePlayer.sendSystemMessage(net.minecraft.network.chat.Component.literal(message));
            }

            // 执行绑定成功后的命令
            List<String> commands = new ArrayList<>(); // 从配置中获取
            for (String command : commands) {
                command = command
                        .replace("$player", playerName)
                        .replace("$account", accountId)
                        .replace("$name", accountName);
                // 使用正确的命令执行方式
                try {
                    // 获取命令管理器和命令执行源
                    net.minecraft.commands.Commands commandManager = server.getCommands();
                    net.minecraft.commands.CommandSourceStack source = server.createCommandSourceStack();

                    // 解析命令
                    ParseResults<net.minecraft.commands.CommandSourceStack> parseResults =
                            commandManager.getDispatcher().parse(command, source);

                    // 执行命令
                    commandManager.performCommand(parseResults,command);
                    logger.info("执行绑定命令成功: " + command);
                } catch (Exception e) {
                    logger.warn("执行绑定命令时出错: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void KickPlayer(String player, String kickMessage) {
        ServerPlayer onlinePlayer = server.getPlayerList().getPlayerByName(player);
        if (onlinePlayer != null) {
            // 使用连接对象的disconnect方法
            onlinePlayer.connection.disconnect(net.minecraft.network.chat.Component.literal(kickMessage));
        }
    }

    @Override
    public void SyncToChatExtra(List<Segment> segments, String text) {
        // 实现富文本消息发送
        SyncToChat(text);
    }

    @Override
    public List<PlayerInfo> getPlayerList() {
        List<PlayerInfo> players = new ArrayList<>();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            PlayerInfo info = new PlayerInfo();
            info.setPlayerName(player.getGameProfile().getName());
            info.setPlayerUuid(player.getUUID().toString());
            // 设置其他信息
            players.add(info);
        }
        return players;
    }
}