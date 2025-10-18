package top.foler.easybot_forge;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent.Loading;

import java.util.List;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = Easybot_forge.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    
    // 配置项（直接使用 ForgeConfigSpec.ConfigValue，无需额外字段）
    public static final ForgeConfigSpec.ConfigValue<String> SERVICE_URL = BUILDER
            .comment("EasyBot 服务端 URL")
            .define("service.url", "ws://127.0.0.1:26990/bridge");

    public static final ForgeConfigSpec.ConfigValue<String> SERVICE_TOKEN = BUILDER
            .comment("EasyBot 服务端令牌 (必填)")
            .define("service.token", "");

    public static final ForgeConfigSpec.BooleanValue DEBUG = BUILDER
            .comment("启用调试模式")
            .define("debug", false);

    public static final ForgeConfigSpec.ConfigValue<String> MESSAGE_BIND_SUCCESS = BUILDER
            .comment("绑定成功消息")
            .define("message.bind_success", "[!] 绑定 #account (#name) 成功!");

    public static final ForgeConfigSpec.BooleanValue EVENT_ENABLE_SUCCESS_EVENT = BUILDER
            .comment("启用绑定成功事件")
            .define("event.enable_success_event", false);

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> EVENT_BIND_SUCCESS = BUILDER
            .comment("绑定成功后执行的命令列表")
            .defineListAllowEmpty("event.bind_success", List.of(), obj -> obj instanceof String);

    public static final ForgeConfigSpec.BooleanValue BOT_FILTER_ENABLED = BUILDER
            .comment("是否启用假人过滤")
            .define("bot.filter_enabled", false);

    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> BOT_PREFIXES = BUILDER
            .comment("假人用户名前缀列表")
            .defineList("bot.prefixes", List.of(), obj -> obj instanceof String);
    
    // 配置规格（必须静态 final）- 必须在所有配置项定义之后构建
    public static final ForgeConfigSpec SPEC = BUILDER.build();

    // 保存 ModConfig 实例（Forge 自动管理）
    private static ModConfig modConfig;

    /**
     * Mod 配置加载事件（Forge 自动触发）
     */
    @SubscribeEvent
    static void onModConfigLoad(Loading event) {
        if (event.getConfig().getSpec() == SPEC) {
            // 获取 Forge 自动创建的 ModConfig 实例（使用默认文件名）
            modConfig = event.getConfig();
            // 初始化 ClientProfile 的配置
            syncConfigToProfile();
        }
    }

    /**
     * 同步配置到 ClientProfile（仅需调用一次）
     */
    private static void syncConfigToProfile() {
        top.foler.easybot_forge.bridge.ClientProfile.setDebugMode(DEBUG.get());
        top.foler.easybot_forge.bridge.ClientProfile.setBotFilterEnabled(BOT_FILTER_ENABLED.get());
        top.foler.easybot_forge.bridge.ClientProfile.setBotPrefixes(getBotPrefixes());
    }

    /**
     * 重新加载配置（Forge 自动触发，此处可扩展）
     */
    public static void reloadConfig() {
        // Forge 会自动重新加载配置到 ConfigValue，只需更新依赖模块
        syncConfigToProfile();
    }

    /**
     * 保存配置到文件（核心逻辑）
     */
    public static void saveConfig() {
        if (modConfig == null) {
            // 若未获取到 ModConfig，手动创建（使用简化构造函数）
            ModContainer container = ModList.get().getModContainerById(Easybot_forge.MODID).orElse(null);
            if (container == null) {
                Easybot_forge.getStaticLogger().error("无法找到 Mod 容器，保存配置失败");
                return;
            }
            // 使用简化构造函数（默认文件名：<modid>.toml）
            modConfig = new ModConfig(ModConfig.Type.COMMON, SPEC, container);
        }

        try {
            // 无需手动回写 ConfigValue！ModConfig 会自动同步
            modConfig.save();
            Easybot_forge.getStaticLogger().info("配置已保存到: " + modConfig.getFileName());
        } catch (Exception e) {
            Easybot_forge.getStaticLogger().error("保存配置失败: " + e.getMessage(), e);
        }
    }

    // ------------------------------
    // 配置项获取方法（封装 ConfigValue.get()）
    // ------------------------------
    public static String getServiceUrl() { return SERVICE_URL.get(); }
    public static String getServiceToken() { return SERVICE_TOKEN.get(); }
    public static boolean isDebug() { return DEBUG.get(); }
    public static String getMessageBindSuccess() { return MESSAGE_BIND_SUCCESS.get(); }
    public static boolean isEventEnableSuccessEvent() { return EVENT_ENABLE_SUCCESS_EVENT.get(); }
    public static List<String> getEventBindSuccess() { 
        return EVENT_BIND_SUCCESS.get().stream().map(Object::toString).collect(Collectors.toList()); 
    }
    public static boolean isBotFilterEnabled() { return BOT_FILTER_ENABLED.get(); }
    public static List<String> getBotPrefixes() { 
        return BOT_PREFIXES.get().stream().map(Object::toString).collect(Collectors.toList()); 
    }
}