package top.foler.easybot_forge;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.List;

// EasyBot 配置类
@Mod.EventBusSubscriber(modid = Easybot_forge.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // 服务端配置
    private static final ForgeConfigSpec.ConfigValue<String> SERVICE_URL = BUILDER
            .comment("EasyBot 服务端 URL")
            .define("service.url", "ws://127.0.0.1:26990/bridge");

    private static final ForgeConfigSpec.ConfigValue<String> SERVICE_TOKEN = BUILDER
            .comment("EasyBot 服务端令牌 (必填)")
            .define("service.token", "");

    // 调试模式
    private static final ForgeConfigSpec.BooleanValue DEBUG = BUILDER
            .comment("启用调试模式")
            .define("debug", false);

    // 消息配置
    private static final ForgeConfigSpec.ConfigValue<String> MESSAGE_BIND_SUCCESS = BUILDER
            .comment("绑定成功消息")
            .define("message.bind_success", "[!] 绑定 #account (#name) 成功!");

    // 事件配置
    private static final ForgeConfigSpec.BooleanValue EVENT_ENABLE_SUCCESS_EVENT = BUILDER
            .comment("启用绑定成功事件")
            .define("event.enable_success_event", false);

    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> EVENT_BIND_SUCCESS = BUILDER
            .comment("绑定成功后执行的命令列表")
            .defineListAllowEmpty("event.bind_success", List.of(), obj -> obj instanceof String);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    // 公共配置字段
    public static String serviceUrl;
    public static String serviceToken;
    public static boolean debug;
    public static String messageBindSuccess;
    public static boolean eventEnableSuccessEvent;
    public static List<String> eventBindSuccess;

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event) {
        serviceUrl = SERVICE_URL.get();
        serviceToken = SERVICE_TOKEN.get();
        debug = DEBUG.get();
        messageBindSuccess = MESSAGE_BIND_SUCCESS.get();
        eventEnableSuccessEvent = EVENT_ENABLE_SUCCESS_EVENT.get();
        // 类型转换：List<? extends String> 到 List<String>
        eventBindSuccess = List.copyOf(EVENT_BIND_SUCCESS.get());
    }
}
