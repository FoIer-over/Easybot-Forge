package top.foler.easybot_forge;

import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import net.minecraft.client.Minecraft;
import top.foler.easybot_forge.bridge.BridgeClient;
import top.foler.easybot_forge.bridge.ClientProfile;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Easybot_forge.MODID)
public class Easybot_forge {

    // Define mod id in a common place for everything to reference
    public static final String MODID = "easybot_forge";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    
    public static Easybot_forge instance;
    private static BridgeClient bridgeClient;
    private static EasyBotImpl bridgeBehavior;

    public Easybot_forge(FMLJavaModLoadingContext context) {
        instance = this;
        IEventBus modEventBus = context.getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("[EasyBot] 正在初始化...");
        
        // 检查配置
        if (Config.serviceToken.isEmpty()) {
            LOGGER.error("[EasyBot] 已禁用, 请先在配置文件中设置Token!");
            return;
        }
        
        // 设置客户端配置
        ClientProfile.setPluginVersion("1.0.0"); // 应该从mod信息中获取
        ClientProfile.setServerDescription("Minecraft Forge Server");
        ClientProfile.setDebugMode(Config.debug);
        
        // 初始化Bridge行为实现
        bridgeBehavior = new EasyBotImpl(this);
    }

    // 在服务器启动时初始化Bridge客户端
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("[EasyBot] 服务器启动中...");
        
        // 获取服务器实例并更新到bridgeBehavior
        MinecraftServer minecraftServer = event.getServer();
        if (bridgeBehavior != null) {
            bridgeBehavior.updateServer(minecraftServer);
        }
        
        // 初始化Bridge客户端
        bridgeClient = new BridgeClient(Config.serviceUrl, bridgeBehavior);
        bridgeClient.setToken(Config.serviceToken);
        
        // 注册事件监听器
        registerEvents();
        
        LOGGER.info("[EasyBot] 初始化完成!");
    }
    
    private void registerEvents() {
        // 注册事件监听器
        MinecraftForge.EVENT_BUS.register(new EasyBotEventListener());
        // 设置Bridge客户端到事件监听器
        EasyBotEventListener.setBridgeClient(bridgeClient);
    }
    
    public static BridgeClient getClient() {
        return bridgeClient;
    }
    
    public static Easybot_forge getInstance() {
        return instance;
    }
    
    /**
     * 获取模组版本
     * @return 模组版本字符串
     */
    public String getModVersion() {
        // 目前返回硬编码版本，后续可以从mod信息中动态获取
        return "1.0.0";
    }
    
    public void runTask(Runnable task) {
        // 在主线程中执行任务 - 使用Forge推荐的方式
        if (bridgeClient != null && bridgeClient.isConnected()) {
            // 使用线程安全的方式执行任务
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
