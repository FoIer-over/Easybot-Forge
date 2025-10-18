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
import top.foler.easybot_forge.Config;

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
        // 检查是否支持PlaceholderAPI功能
        if (!ClientProfile.isPapiSupported()) {
            logger.warn("PlaceholderAPI功能未启用，返回原始查询字符串");
            return query;
        }
        
        try {
            // 尝试查找PlaceholderAPI或类似的Forge插件
            // 这里提供基础实现，可以根据实际安装的PlaceholderAPI插件进行扩展
            logger.info("处理占位符查询: player={}, query={}", playerName, query);
            
            // 基础变量替换
            if (playerName != null && !playerName.isEmpty()) {
                // 替换一些简单的玩家相关变量
                query = query.replace("%player_name%", playerName);
                ServerPlayer player = server.getPlayerList().getPlayerByName(playerName);
                if (player != null) {
                    query = query.replace("%player_uuid%", player.getUUID().toString());
                    query = query.replace("%player_x%", String.valueOf(player.getX()));
                    query = query.replace("%player_y%", String.valueOf(player.getY()));
                    query = query.replace("%player_z%", String.valueOf(player.getZ()));
                }
            }
            
            // 替换服务器相关变量
            query = query.replace("%online%", String.valueOf(getOnlinePlayerCount()));
            query = query.replace("%max_players%", String.valueOf(getMaxPlayers()));
            query = query.replace("%server_name%", server.getMotd());
            
            return query;
        } catch (Exception e) {
            logger.warn("处理占位符查询时出错: " + e.getMessage());
            return query;
        }
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
                
                // 向所有玩家广播绑定成功消息（如果需要）
                String broadcastMessage = "玩家 " + playerName + " 绑定了账号 " + accountName + " 成功!";
                for (ServerPlayer p : server.getPlayerList().getPlayers()) {
                    p.sendSystemMessage(net.minecraft.network.chat.Component.literal(broadcastMessage));
                }
            }

            // 执行绑定成功后的命令
            // 从Config中获取绑定成功后要执行的命令
            List<String> commands = Config.eventBindSuccess;
            if (commands == null || commands.isEmpty()) {
                // 如果配置中没有命令，使用默认的命令列表
                commands = new ArrayList<>();
                commands.add("tell $player 欢迎使用绑定系统!");
            }
            
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
                    commandManager.performCommand(parseResults, command);
                    logger.info("执行绑定命令成功: {}", command);
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
        if (segments == null || segments.isEmpty()) {
            SyncToChat(text);
            return;
        }
        
        try {
            // 在Forge中构建可交互的聊天组件
            net.minecraft.network.chat.MutableComponent messageComponent = net.minecraft.network.chat.Component.empty();
            
            for (Segment segment : segments) {
                switch (segment.getType()) {
                    case TEXT:
                        // 处理文本段
                        String textContent = segment.get("text");
                        if (textContent != null) {
                            net.minecraft.network.chat.MutableComponent textComponent = net.minecraft.network.chat.Component.literal(textContent);
                            
                            // 处理样式
                            if (segment.get("color") != null) {
                                String color = segment.get("color");
                                try {
                                    // 尝试解析颜色
                                    net.minecraft.ChatFormatting formatting = net.minecraft.ChatFormatting.valueOf(color.toUpperCase());
                                    textComponent.withStyle(formatting);
                                } catch (IllegalArgumentException ignored) {
                                    // 忽略未知颜色
                                }
                            }
                            
                            // 处理粗体
                            if ("true".equalsIgnoreCase(segment.get("bold"))) {
                                textComponent.withStyle(net.minecraft.ChatFormatting.BOLD);
                            }
                            
                            // 处理斜体
                            if ("true".equalsIgnoreCase(segment.get("italic"))) {
                                textComponent.withStyle(net.minecraft.ChatFormatting.ITALIC);
                            }
                            
                            // 处理下划线
                            if ("true".equalsIgnoreCase(segment.get("underline"))) {
                                textComponent.withStyle(net.minecraft.ChatFormatting.UNDERLINE);
                            }
                            
                            // 添加悬停事件
                            if (segment.get("hover") != null) {
                                textComponent.withStyle(style -> style.withHoverEvent(
                                        new net.minecraft.network.chat.HoverEvent(
                                                net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT,
                                                net.minecraft.network.chat.Component.literal(segment.get("hover"))
                                        )
                                ));
                            }
                            
                            // 添加点击事件
                            if (segment.get("click") != null && segment.get("clickType") != null) {
                                String clickType = segment.get("clickType");
                                String clickValue = segment.get("click");
                                
                                net.minecraft.network.chat.ClickEvent.Action action;
                                switch (clickType.toLowerCase()) {
                                    case "open_url":
                                        action = net.minecraft.network.chat.ClickEvent.Action.OPEN_URL;
                                        break;
                                    case "run_command":
                                        action = net.minecraft.network.chat.ClickEvent.Action.RUN_COMMAND;
                                        break;
                                    case "suggest_command":
                                        action = net.minecraft.network.chat.ClickEvent.Action.SUGGEST_COMMAND;
                                        break;
                                    default:
                                        action = net.minecraft.network.chat.ClickEvent.Action.SUGGEST_COMMAND; // 默认为建议命令
                                }
                                
                                textComponent.withStyle(style -> style.withClickEvent(
                                        new net.minecraft.network.chat.ClickEvent(action, clickValue)
                                ));
                            }
                            
                            messageComponent.append(textComponent);
                        }
                        break;
                        
                    case AT:
                        // 处理@段
                        String qq = segment.get("qq");
                        if ("all".equals(qq)) {
                            net.minecraft.network.chat.MutableComponent atAllComponent = net.minecraft.network.chat.Component.literal("@所有人");
                            atAllComponent.withStyle(net.minecraft.ChatFormatting.RED);
                            atAllComponent.withStyle(style -> style.withHoverEvent(
                                    new net.minecraft.network.chat.HoverEvent(
                                            net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT,
                                            net.minecraft.network.chat.Component.literal("点击@所有人")
                                    )
                            ));
                            messageComponent.append(atAllComponent);
                        } else {
                            // 构建普通@消息
                            net.minecraft.network.chat.MutableComponent atComponent = net.minecraft.network.chat.Component.literal("@" + qq);
                            atComponent.withStyle(net.minecraft.ChatFormatting.BLUE);
                            messageComponent.append(atComponent);
                        }
                        break;
                        
                    case IMAGE:
                        // 处理图片段，在Minecraft中我们只能显示图片URL或占位符
                        String imageUrl = segment.get("url");
                        if (imageUrl != null) {
                            net.minecraft.network.chat.MutableComponent imageComponent = net.minecraft.network.chat.Component.literal("[图片]");
                            imageComponent.withStyle(net.minecraft.ChatFormatting.GREEN);
                            imageComponent.withStyle(style -> style.withHoverEvent(
                                    new net.minecraft.network.chat.HoverEvent(
                                            net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT,
                                            net.minecraft.network.chat.Component.literal(imageUrl)
                                    )
                            ));
                            messageComponent.append(imageComponent);
                        }
                        break;
                        
                    case FILE:
                        // 处理文件段
                        String fileName = segment.get("name");
                        String fileUrl = segment.get("url");
                        if (fileName != null) {
                            net.minecraft.network.chat.MutableComponent fileComponent = net.minecraft.network.chat.Component.literal("[文件: " + fileName + "]");
                            fileComponent.withStyle(net.minecraft.ChatFormatting.AQUA);
                            
                            if (fileUrl != null) {
                                fileComponent.withStyle(style -> style.withClickEvent(
                                        new net.minecraft.network.chat.ClickEvent(
                                                net.minecraft.network.chat.ClickEvent.Action.OPEN_URL,
                                                fileUrl
                                        )
                                ));
                                fileComponent.withStyle(style -> style.withHoverEvent(
                                        new net.minecraft.network.chat.HoverEvent(
                                                net.minecraft.network.chat.HoverEvent.Action.SHOW_TEXT,
                                                net.minecraft.network.chat.Component.literal("点击下载文件")
                                        )
                                ));
                            }
                            
                            messageComponent.append(fileComponent);
                        }
                        break;
                        
                    case FACE:
                        // 处理表情段
                        String faceId = segment.get("id");
                        if (faceId != null) {
                            net.minecraft.network.chat.MutableComponent faceComponent = net.minecraft.network.chat.Component.literal("[表情:" + faceId + "]");
                            faceComponent.withStyle(net.minecraft.ChatFormatting.YELLOW);
                            messageComponent.append(faceComponent);
                        }
                        break;
                        
                    default:
                        // 处理未知类型
                        logger.warn("未知的消息段类型: {}", segment.getType());
                }
            }
            
            // 发送构建好的消息组件
            server.execute(() -> {
                for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                    player.sendSystemMessage(messageComponent);
                }
            });
        } catch (Exception e) {
            logger.warn("处理富文本消息时出错: " + e.getMessage());
            // 如果构建富文本失败，回退到普通文本消息
            SyncToChat(text);
        }
    }

    @Override
    public List<PlayerInfo> getPlayerList() {
        List<PlayerInfo> players = new ArrayList<>();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            PlayerInfo info = new PlayerInfo();
            info.setPlayerName(player.getGameProfile().getName());
            info.setPlayerUuid(player.getUUID().toString());
            
            // 获取玩家IP地址
            try {
                // 在Forge中通过Player.connection获取IP地址
                if (player.connection != null) {
                    String ipAddress = player.connection.getConnection().getRemoteAddress().toString();
                    // 移除端口号部分
                    if (ipAddress.contains(":")) {
                        ipAddress = ipAddress.substring(0, ipAddress.lastIndexOf(":"));
                    }
                    // 移除可能的斜杠前缀
                    if (ipAddress.startsWith("/")) {
                        ipAddress = ipAddress.substring(1);
                    }
                    info.setIp(ipAddress);
                }
            } catch (Exception e) {
                logger.warn("获取玩家IP地址时出错: " + e.getMessage());
                info.setIp("unknown");
            }
            
            // 尝试设置皮肤URL
            try {
                // 使用Mojang的皮肤API
                String uuid = player.getUUID().toString().replace("-", "");
                String skinUrl = "https://crafatar.com/skins/" + uuid;
                info.setSkinUrl(skinUrl);
            } catch (Exception e) {
                logger.warn("设置玩家皮肤URL时出错: " + e.getMessage());
            }
            
            // 检测是否为基岩版玩家（通过Geyser）
            try {
                // 这里是一个简单的检测方法，实际项目中可能需要根据Geyser的API进行调整
                // 检查是否存在Geyser相关的标签或元数据
                boolean isBedrock = false;
                // 尝试通过NBT数据或其他方式检测
                // 例如: isBedrock = player.getTags().contains("geyser_bedrock_player");
                // 或者检查是否有特殊的GameProfile属性
                info.setBedrock(isBedrock);
            } catch (Exception e) {
                logger.warn("检测基岩版玩家时出错: " + e.getMessage());
                info.setBedrock(false);
            }
            
            players.add(info);
        }
        return players;
    }
}