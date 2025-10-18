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
import top.foler.easybot_forge.command.CommandHandler;

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
        if (Config.getServiceToken().isEmpty()) {
            LOGGER.error("[EasyBot] 已禁用, 请先在配置文件中设置Token!");
            return;
        }
        
        // 设置客户端配置
        // 从mod信息中获取插件版本
        String modVersion = "1.0.0"; // 默认版本
        try {
            // 使用Forge的ModList API获取mod信息
            modVersion = net.minecraftforge.fml.ModList.get().getModContainerById(MODID)
                    .map(container -> container.getModInfo().getVersion().toString())
                    .orElse(modVersion);
        } catch (Exception e) {
            LOGGER.warn("无法获取mod版本信息，使用默认版本");
        }
        ClientProfile.setPluginVersion(modVersion);
        
        // 服务器描述将在服务器启动时从服务器实例获取
        ClientProfile.setDebugMode(Config.isDebug());
        
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
        
        // 从服务器实例获取服务器信息作为名称
        String serverName = "Minecraft Server"; // 默认名称
        try {
            // 尝试获取服务器的MOTD或其他标识信息
            // 在Forge中，我们可以使用服务器的MOTD
            serverName = minecraftServer.getMotd();
        } catch (Exception e) {
            LOGGER.debug("无法获取服务器MOTD，使用默认名称");
        }
        ClientProfile.setServerDescription(serverName);
        
        // 初始化Bridge客户端
        bridgeClient = new BridgeClient(Config.getServiceUrl(), bridgeBehavior);
        bridgeClient.setToken(Config.getServiceToken());
        
        // 注册事件监听器
        registerEvents();
        
        // 注册命令
        CommandHandler commandHandler = new CommandHandler(this);
        commandHandler.registerCommands(event.getServer().getCommands().getDispatcher());
        LOGGER.info("[EasyBot] 命令注册完成");
        
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
     * 获取日志记录器
     * @return 日志记录器实例
     */
    public Logger getLogger() {
        return LOGGER;
    }
    
    /**
     * 获取静态日志记录器
     * @return 静态日志记录器实例
     */
    public static Logger getStaticLogger() {
        return LOGGER;
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
