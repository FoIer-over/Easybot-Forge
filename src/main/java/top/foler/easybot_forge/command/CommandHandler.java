package top.foler.easybot_forge.command;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import top.foler.easybot_forge.Config;
import top.foler.easybot_forge.Easybot_forge;
import top.foler.easybot_forge.bridge.BridgeClient;

public class CommandHandler {
    private final Easybot_forge mod;

    public CommandHandler(Easybot_forge mod) {
        this.mod = mod;
    }

    public void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher) {
        // 主命令 /ez
        dispatcher.register(Commands.literal("ez")
                .then(Commands.literal("help")
                        .executes(this::showHelp))
                .then(Commands.literal("reload")
                        .requires(source -> source.hasPermission(3))
                        .executes(this::reload))
                .then(Commands.literal("bind")
                        .executes(this::bind))
                .then(Commands.literal("say")
                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                .executes(this::say)))
                .then(Commands.literal("ssay")
                        .then(Commands.argument("message", StringArgumentType.greedyString())
                                .executes(this::crossServerSay)))
                .then(Commands.literal("bot")
                        .requires(source -> source.hasPermission(3))
                        .then(Commands.literal("toggle")
                                .executes(this::toggleBotFilter))
                        .then(Commands.literal("add")
                                .then(Commands.argument("prefix", StringArgumentType.string())
                                        .executes(this::addBotPrefix)))
                        .then(Commands.literal("remove")
                                .then(Commands.argument("prefix", StringArgumentType.string())
                                        .executes(this::removeBotPrefix)))
                        .then(Commands.literal("list")
                                .executes(this::listBotPrefixes)))
                .executes(this::showHelp));
    }

    private int showHelp(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        
        String[] helpMessages = {
            "§6--------§a EasyBot Forge V1.0.0§r--------",
            "§b/ez help §f- §c显示帮助菜单",
            "§b/ez reload §f- §c重载配置文件 (需要OP权限)",
            "",
            "§c绑定类",
            "§b/ez bind §f- §c触发绑定",
            "§b/bind §f- §c同上",
            "",
            "§c消息发送类",
            "§b/ez say <message> §f- §c发送消息",
            "§b/esay <message> §f- §c同上",
            "§b/say <message> §f- §c同上",
            "",
            "§c跨服聊天",
            "§b/ez ssay <message> §f- §c发送跨服消息",
            "§b/essay <message> §f- §c同上",
            "§b/ssay <message> §f- §c同上",
            "",
            "§c假人过滤设置(需要OP权限)",
            "§b/ez bot toggle §f- §c开启/关闭假人过滤",
            "§b/ez bot add <prefix> §f- §c添加假人过滤前缀",
            "§b/ez bot remove <prefix> §f- §c移除假人过滤前缀",
            "§b/ez bot list §f- §c显示假人过滤前缀列表",
            "§6---------------------------------------------"
        };

        for (String line : helpMessages) {
            if (!line.trim().isEmpty()) {
                source.sendSuccess(() -> Component.literal(line), false);
            }
        }
        return 1;
    }

    private int reload(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        
        try {
            // 重载配置
            Config.reloadConfig();
            source.sendSuccess(() -> Component.literal("§a插件重载成功!"), false);
        } catch (Exception e) {
            source.sendSuccess(() -> Component.literal("§c插件重载失败: " + e.getMessage()), false);
            Easybot_forge.getStaticLogger().error("重载配置时出错", e);
        }
        return 1;
    }

    private int bind(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        
        if (!source.isPlayer()) {
            source.sendSuccess(() -> Component.literal("§c这个命令不能在控制台使用!"), false);
            return 0;
        }

        ServerPlayer player = source.getPlayer();
        String playerName = player.getGameProfile().getName();
        BridgeClient client = Easybot_forge.getClient();
        
        if (client == null || !client.isConnected()) {
            source.sendSuccess(() -> Component.literal("§c与EasyBot服务器的连接未建立!"), false);
            return 0;
        }

        // 异步处理绑定逻辑
        CompletableFuture.runAsync(() -> {
            try {
                // 先检查玩家是否已经绑定
                getSocialAccount(client, playerName).thenAccept(bindData -> {
                    String uuid = (String) bindData.get("uuid");
                    
                    if (uuid != null && !uuid.isEmpty()) {
                        // 已绑定
                        String name = (String) bindData.get("name");
                        String platform = (String) bindData.get("platform");
                        String time = (String) bindData.get("time");
                        
                        // 在主线程上发送消息
                        player.getServer().execute(() -> {
                            player.sendSystemMessage(Component.literal(
                                String.format("§c你已经绑定了账号, (%s/%s/时间:%s/%s)",
                                    name, uuid, time, platform)
                            ));
                        });
                    } else {
                        // 未绑定，开始绑定流程
                        startBind(client, playerName).thenAccept(codeData -> {
                            String code = (String) codeData.get("code");
                            String time = (String) codeData.get("time");
                            
                            String message = Config.BIND_START_MESSAGE.get()
                                    .replace("#code", code)
                                    .replace("#time", time);
                            
                            // 在主线程上发送消息
                            player.getServer().execute(() -> {
                                player.sendSystemMessage(Component.literal(message));
                            });
                            Easybot_forge.getStaticLogger().info("玩家 {} 开始绑定，绑定码: {}", playerName, code);
                        }).exceptionally(e -> {
                            // 在主线程上发送消息
                            player.getServer().execute(() -> {
                                player.sendSystemMessage(Component.literal("§c获取绑定码失败: " + e.getMessage()));
                            });
                            Easybot_forge.getStaticLogger().error("获取绑定码时出错", e);
                            return null;
                        });
                    }
                }).exceptionally(e -> {
                    // 在主线程上发送消息
                    player.getServer().execute(() -> {
                        player.sendSystemMessage(Component.literal("§c检查绑定状态失败: " + e.getMessage()));
                    });
                    Easybot_forge.getStaticLogger().error("检查绑定状态时出错", e);
                    return null;
                });
            } catch (Exception e) {
                // 在主线程上发送消息
                player.getServer().execute(() -> {
                    player.sendSystemMessage(Component.literal("§c绑定请求失败: " + e.getMessage()));
                });
                Easybot_forge.getStaticLogger().error("处理绑定请求时出错", e);
            }
        });

        return 1;
    }
    
    /**
     * 获取玩家的社交账号信息
     */
    private CompletableFuture<java.util.Map<String, Object>> getSocialAccount(BridgeClient client, String playerName) {
        CompletableFuture<java.util.Map<String, Object>> result = new CompletableFuture<>();
        
        try {
            // 使用现有的GetBindInfoPacket类
            top.foler.easybot_forge.bridge.packet.GetBindInfoPacket packet = new top.foler.easybot_forge.bridge.packet.GetBindInfoPacket();
            packet.setPlayerName(playerName);
            
            // 发送数据包并等待响应
            client.sendAndWaitForCallbackAsync(packet, com.google.gson.JsonObject.class)
                .thenAccept(response -> {
                    try {
                        // 解析响应数据
                        com.google.gson.JsonObject dataObj = response.getAsJsonObject();
                        java.util.Map<String, Object> map = new java.util.HashMap<>();
                        
                        // 根据GetBindInfoResultPacket的结构解析
                        map.put("uuid", dataObj.has("id") ? dataObj.get("id").getAsString() : "");
                        map.put("name", dataObj.has("name") ? dataObj.get("name").getAsString() : "");
                        map.put("platform", dataObj.has("platform") ? dataObj.get("platform").getAsString() : "");
                        map.put("time", ""); // 时间可能不在响应中，使用空字符串
                        
                        result.complete(map);
                    } catch (Exception e) {
                        result.completeExceptionally(e);
                    }
                })
                .exceptionally(e -> {
                    result.completeExceptionally(e);
                    return null;
                });
        } catch (Exception e) {
            result.completeExceptionally(e);
        }
        
        return result;
    }
    
    /**
     * 开始绑定流程
     */
    private CompletableFuture<java.util.Map<String, Object>> startBind(BridgeClient client, String playerName) {
        CompletableFuture<java.util.Map<String, Object>> result = new CompletableFuture<>();
        
        try {
            // 使用现有的StartBindPacket类
            top.foler.easybot_forge.bridge.packet.StartBindPacket packet = new top.foler.easybot_forge.bridge.packet.StartBindPacket();
            packet.setPlayerName(playerName);
            
            // 发送数据包并等待响应
            client.sendAndWaitForCallbackAsync(packet, com.google.gson.JsonObject.class)
                .thenAccept(response -> {
                    try {
                        // 解析响应数据
                        com.google.gson.JsonObject dataObj = response.getAsJsonObject();
                        java.util.Map<String, Object> map = new java.util.HashMap<>();
                        
                        // 根据StartBindResultPacket的结构解析
                        map.put("code", dataObj.has("code") ? dataObj.get("code").getAsString() : "");
                        map.put("time", dataObj.has("time") ? dataObj.get("time").getAsString() : "10"); // 默认10分钟
                        
                        result.complete(map);
                    } catch (Exception e) {
                        result.completeExceptionally(e);
                    }
                })
                .exceptionally(e -> {
                    result.completeExceptionally(e);
                    return null;
                });
        } catch (Exception e) {
            result.completeExceptionally(e);
        }
        
        return result;
    }

    private int say(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String message = StringArgumentType.getString(context, "message");
        
        String playerName = "CONSOLE";
        ServerPlayer player = null;
        if (source.isPlayer()) {
            player = source.getPlayer();
            playerName = player.getGameProfile().getName();
        }

        BridgeClient client = Easybot_forge.getClient();
        if (client == null || !client.isConnected()) {
            source.sendSuccess(() -> Component.literal("§c与EasyBot服务器的连接未建立!"), false);
            return 0;
        }

        // 异步发送消息
        final String finalPlayerName = playerName;
        final ServerPlayer finalPlayer = player;
        CompletableFuture.runAsync(() -> {
            try {
                if (finalPlayer != null) {
                    // 创建PlayerInfoWithRaw对象
                    top.foler.easybot_forge.bridge.packet.PlayerInfoWithRaw playerInfoWithRaw = 
                        new top.foler.easybot_forge.bridge.packet.PlayerInfoWithRaw();
                    
                    // 设置基本信息
                    playerInfoWithRaw.setName(finalPlayerName);
                    playerInfoWithRaw.setUuid(finalPlayer.getUUID().toString());
                    
                    // 获取玩家IP地址
                    String ipAddress = "unknown";
                    try {
                        if (finalPlayer.connection != null) {
                            ipAddress = finalPlayer.connection.getConnection().getRemoteAddress().toString();
                            if (ipAddress.contains(":")) {
                                ipAddress = ipAddress.substring(0, ipAddress.lastIndexOf(":"));
                            }
                            if (ipAddress.startsWith("/")) {
                                ipAddress = ipAddress.substring(1);
                            }
                        }
                    } catch (Exception e) {
                        Easybot_forge.getStaticLogger().warn("获取玩家IP地址时出错: " + e.getMessage());
                    }
                    playerInfoWithRaw.setIp(ipAddress);
                    playerInfoWithRaw.setNameRaw(finalPlayerName);
                    
                    // 使用SyncMessagePacket发送消息
                    top.foler.easybot_forge.bridge.packet.SyncMessagePacket packet = new top.foler.easybot_forge.bridge.packet.SyncMessagePacket();
                    packet.setPlayer(playerInfoWithRaw);
                    packet.setMessage(message);
                    packet.setUseCommand(true);
                    
                    // 发送数据包
                    client.sendAndWaitForCallbackAsync(packet, com.google.gson.JsonObject.class);
                } else {
                    // 控制台发送消息的逻辑
                    Easybot_forge.getStaticLogger().info("控制台发送消息: {}", message);
                    
                    // 对于控制台消息，创建特殊的PlayerInfoWithRaw
                    top.foler.easybot_forge.bridge.packet.PlayerInfoWithRaw playerInfoWithRaw = 
                        new top.foler.easybot_forge.bridge.packet.PlayerInfoWithRaw();
                    playerInfoWithRaw.setName("CONSOLE");
                    playerInfoWithRaw.setUuid("console");
                    playerInfoWithRaw.setIp("localhost");
                    playerInfoWithRaw.setNameRaw("CONSOLE");
                    
                    top.foler.easybot_forge.bridge.packet.SyncMessagePacket packet = new top.foler.easybot_forge.bridge.packet.SyncMessagePacket();
                    packet.setPlayer(playerInfoWithRaw);
                    packet.setMessage(message);
                    packet.setUseCommand(true);
                    
                    client.sendAndWaitForCallbackAsync(packet, com.google.gson.JsonObject.class);
                }
                
                // 在主线程上发送反馈消息
                source.getServer().execute(() -> {
                    source.sendSuccess(() -> Component.literal("§a消息已发送: §f" + message), false);
                });
            } catch (Exception e) {
                // 在主线程上发送错误消息
                source.getServer().execute(() -> {
                    source.sendSuccess(() -> Component.literal("§c消息发送失败: " + e.getMessage()), false);
                });
                Easybot_forge.getStaticLogger().error("发送消息时出错", e);
            }
        });

        return 1;
    }

    private int crossServerSay(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String message = StringArgumentType.getString(context, "message");
        
        if (!source.isPlayer()) {
            source.sendSuccess(() -> Component.literal("§c这个命令只能由玩家使用!"), false);
            return 0;
        }

        ServerPlayer player = source.getPlayer();
        String playerName = player.getGameProfile().getName();
        BridgeClient client = Easybot_forge.getClient();
        
        if (client == null || !client.isConnected()) {
            source.sendSuccess(() -> Component.literal("§c与EasyBot服务器的连接未建立!"), false);
            return 0;
        }

        // 异步发送跨服消息
        CompletableFuture.runAsync(() -> {
            try {
                // 创建PlayerInfoWithRaw对象
                top.foler.easybot_forge.bridge.packet.PlayerInfoWithRaw playerInfoWithRaw = 
                    new top.foler.easybot_forge.bridge.packet.PlayerInfoWithRaw();
                
                // 设置基本信息
                playerInfoWithRaw.setName(playerName);
                playerInfoWithRaw.setUuid(player.getUUID().toString());
                
                // 获取玩家IP地址
                String ipAddress = "unknown";
                try {
                    if (player.connection != null) {
                        ipAddress = player.connection.getConnection().getRemoteAddress().toString();
                        if (ipAddress.contains(":")) {
                            ipAddress = ipAddress.substring(0, ipAddress.lastIndexOf(":"));
                        }
                        if (ipAddress.startsWith("/")) {
                            ipAddress = ipAddress.substring(1);
                        }
                    }
                } catch (Exception e) {
                    Easybot_forge.getStaticLogger().warn("获取玩家IP地址时出错: " + e.getMessage());
                }
                playerInfoWithRaw.setIp(ipAddress);
                playerInfoWithRaw.setNameRaw(playerName);
                
                // 使用SendCrossServerMessagePacket发送跨服消息
                top.foler.easybot_forge.bridge.packet.SendCrossServerMessagePacket packet = new top.foler.easybot_forge.bridge.packet.SendCrossServerMessagePacket();
                packet.setPlayer(playerInfoWithRaw);
                packet.setMessage(message);
                
                // 发送数据包
                client.sendAndWaitForCallbackAsync(packet, com.google.gson.JsonObject.class);
                
                Easybot_forge.getStaticLogger().info("玩家 {} 发送跨服消息: {}", playerName, message);
                // 在主线程上发送反馈消息
                player.getServer().execute(() -> {
                    player.sendSystemMessage(Component.literal("§a你的消息已发送到其他服务器."));
                });
            } catch (Exception e) {
                // 在主线程上发送错误消息
                player.getServer().execute(() -> {
                    player.sendSystemMessage(Component.literal("§c跨服消息发送失败: " + e.getMessage()));
                });
                Easybot_forge.getStaticLogger().error("发送跨服消息时出错", e);
            }
        });

        return 1;
    }

    private int toggleBotFilter(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        try {
            boolean enabled = !Config.isBotFilterEnabled();
            Config.BOT_FILTER_ENABLED.set(enabled);
            Config.saveConfig();
            // 同步更新ClientProfile
            top.foler.easybot_forge.bridge.ClientProfile.setBotFilterEnabled(enabled);
            source.sendSuccess(() -> Component.literal("§a假人过滤已" + (enabled ? "启用" : "禁用")), false);
        } catch (Exception e) {
            source.sendSuccess(() -> Component.literal("§c切换假人过滤状态失败: " + e.getMessage()), false);
            Easybot_forge.getStaticLogger().error("切换假人过滤状态时出错", e);
        }
        return 1;
    }

    private int addBotPrefix(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String prefix = StringArgumentType.getString(context, "prefix");
        
        try {
            if (Config.getBotPrefixes().contains(prefix)) {
                source.sendSuccess(() -> Component.literal("§c前缀已存在"), false);
            } else {
                // 创建新的列表并添加前缀
                List<String> newPrefixes = new ArrayList<>(Config.getBotPrefixes());
                newPrefixes.add(prefix);
                Config.BOT_PREFIXES.set(newPrefixes);
                Config.saveConfig();
                // 同步更新ClientProfile
                top.foler.easybot_forge.bridge.ClientProfile.setBotPrefixes(newPrefixes);
                source.sendSuccess(() -> Component.literal("§a已添加假人前缀: " + prefix), false);
            }
        } catch (Exception e) {
            source.sendSuccess(() -> Component.literal("§c添加假人前缀失败: " + e.getMessage()), false);
            Easybot_forge.getStaticLogger().error("添加假人前缀时出错", e);
        }
        return 1;
    }

    private int removeBotPrefix(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String prefix = StringArgumentType.getString(context, "prefix");
        
        try {
            if (Config.getBotPrefixes().contains(prefix)) {
                // 创建新的列表并移除前缀
                List<String> newPrefixes = new ArrayList<>(Config.getBotPrefixes());
                newPrefixes.remove(prefix);
                Config.BOT_PREFIXES.set(newPrefixes);
                Config.saveConfig();
                // 同步更新ClientProfile
                top.foler.easybot_forge.bridge.ClientProfile.setBotPrefixes(newPrefixes);
                source.sendSuccess(() -> Component.literal("§a已移除假人前缀: " + prefix), false);
            } else {
                source.sendSuccess(() -> Component.literal("§c前缀不存在"), false);
            }
        } catch (Exception e) {
            source.sendSuccess(() -> Component.literal("§c移除假人前缀失败: " + e.getMessage()), false);
            Easybot_forge.getStaticLogger().error("移除假人前缀时出错", e);
        }
        return 1;
    }

    private int listBotPrefixes(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        
        try {
            List<String> prefixes = Config.getBotPrefixes();
            
            if (prefixes.isEmpty()) {
                source.sendSuccess(() -> Component.literal("§a当前没有设置假人前缀"), false);
            } else {
                StringBuilder builder = new StringBuilder("§a假人前缀列表: ");
                for (int i = 0; i < prefixes.size(); i++) {
                    builder.append(prefixes.get(i));
                    if (i < prefixes.size() - 1) {
                        builder.append(", ");
                    }
                }
                source.sendSuccess(() -> Component.literal(builder.toString()), false);
            }
        } catch (Exception e) {
            source.sendSuccess(() -> Component.literal("§c获取假人前缀列表失败: " + e.getMessage()), false);
            Easybot_forge.getStaticLogger().error("获取假人前缀列表时出错", e);
        }
        return 1;
    }
}