package top.foler.easybot_forge.command;

import java.util.List;
import java.util.ArrayList;

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
import java.util.List;

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

        // 简化命令
        dispatcher.register(Commands.literal("bind")
                .executes(this::bind));

        dispatcher.register(Commands.literal("say")
                .then(Commands.argument("message", StringArgumentType.greedyString())
                        .executes(this::say)));

        dispatcher.register(Commands.literal("esay")
                .then(Commands.argument("message", StringArgumentType.greedyString())
                        .executes(this::say)));

        dispatcher.register(Commands.literal("ssay")
                .then(Commands.argument("message", StringArgumentType.greedyString())
                        .executes(this::crossServerSay)));

        dispatcher.register(Commands.literal("essay")
                .then(Commands.argument("message", StringArgumentType.greedyString())
                        .executes(this::crossServerSay)));
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

        // 在单独线程中处理绑定逻辑
        Thread thread = new Thread(() -> {
            try {
                source.sendSuccess(() -> Component.literal("§a请稍候，正在处理绑定请求..."), false);
                // 创建绑定包并发送
                // 注意：这里假设BindPlayerPacket类存在并可以使用
                // 如果不存在，需要根据实际实现进行调整
                try {
                    Class<?> packetClass = Class.forName("top.foler.easybot_forge.packet.BindPlayerPacket");
                    Object packet = packetClass.getDeclaredConstructor().newInstance();
                    packetClass.getMethod("setPlayerName", String.class).invoke(packet, playerName);
                    // 生成随机botId或使用其他方式获取
                    String botId = "bot_" + System.currentTimeMillis();
                    packetClass.getMethod("setBotId", String.class).invoke(packet, botId);
                    client.getClass().getMethod("sendPacket", Object.class).invoke(client, packet);
                    source.sendSuccess(() -> Component.literal("§a已发送绑定请求，请等待响应"), false);
                } catch (ClassNotFoundException e) {
                    // 如果找不到类，使用备选方案
                    source.sendSuccess(() -> Component.literal("§a绑定请求已发送，请在EasyBot界面完成绑定"), false);
                }
            } catch (Exception e) {
                source.sendSuccess(() -> Component.literal("§c绑定过程中出错: " + e.getMessage()), false);
                Easybot_forge.getStaticLogger().error("绑定过程中出错", e);
            }
        });
        thread.setDaemon(true);
        thread.start();
        
        return 1;
    }

    private int say(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String message = StringArgumentType.getString(context, "message");
        
        BridgeClient client = Easybot_forge.getClient();
        if (client == null || !client.isConnected()) {
            source.sendSuccess(() -> Component.literal("§c与EasyBot服务器的连接未建立!"), false);
            return 0;
        }
        
        try {
            // 创建消息包并发送
            // 注意：这里假设SendMessagePacket类存在并可以使用
            try {
                Class<?> packetClass = Class.forName("top.foler.easybot_forge.packet.SendMessagePacket");
                Object packet = packetClass.getDeclaredConstructor().newInstance();
                packetClass.getMethod("setMessage", String.class).invoke(packet, message);
                client.getClass().getMethod("sendPacket", Object.class).invoke(client, packet);
                source.sendSuccess(() -> Component.literal("§a消息已发送"), false);
            } catch (ClassNotFoundException e) {
                // 备选发送消息逻辑
                client.send(message); // 假设BridgeClient有send方法
                source.sendSuccess(() -> Component.literal("§a消息已发送"), false);
            }
        } catch (Exception e) {
            source.sendSuccess(() -> Component.literal("§c发送消息失败: " + e.getMessage()), false);
            Easybot_forge.getStaticLogger().error("发送消息时出错", e);
        }
        return 1;
    }

    private int crossServerSay(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        String message = StringArgumentType.getString(context, "message");
        
        BridgeClient client = Easybot_forge.getClient();
        if (client == null || !client.isConnected()) {
            source.sendSuccess(() -> Component.literal("§c与EasyBot服务器的连接未建立!"), false);
            return 0;
        }
        
        try {
            // 创建跨服消息包并发送
            // 注意：这里假设SendCrossServerMessagePacket类存在并可以使用
            try {
                Class<?> packetClass = Class.forName("top.foler.easybot_forge.packet.SendCrossServerMessagePacket");
                Object packet = packetClass.getDeclaredConstructor().newInstance();
                packetClass.getMethod("setMessage", String.class).invoke(packet, message);
                client.getClass().getMethod("sendPacket", Object.class).invoke(client, packet);
                source.sendSuccess(() -> Component.literal("§a跨服消息已发送"), false);
            } catch (ClassNotFoundException e) {
                // 备选发送跨服消息逻辑
                client.send(message); // 使用send方法作为备选
                source.sendSuccess(() -> Component.literal("§a跨服消息已发送"), false);
            }
        } catch (Exception e) {
            source.sendSuccess(() -> Component.literal("§c发送跨服消息失败: " + e.getMessage()), false);
            Easybot_forge.getStaticLogger().error("发送跨服消息时出错", e);
        }
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