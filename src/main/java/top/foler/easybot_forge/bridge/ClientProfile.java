package top.foler.easybot_forge.bridge;

import java.util.ArrayList;
import java.util.List;

public class ClientProfile {
    private static String pluginVersion;
    private static String serverDescription;
    private static boolean debugMode;
    private static boolean onlineMode;
    private static boolean commandSupported = true;
    private static boolean papiSupported = false;
    private static boolean hasGeyser = false;
    private static boolean hasSkinsRestorer = false;
    private static boolean hasPaperSkinApi = false;
    private static boolean syncMessageMoney = false;
    private static String syncMessageMode = "all";
    
    // 假人过滤相关配置
    private static boolean botFilterEnabled = false;
    private static List<String> botPrefixes = new ArrayList<>();
    private static boolean carpetBotFilterEnabled = false;
    private static List<String> carpetBotPrefixes = new ArrayList<>();

    public static String getPluginVersion() {
        return pluginVersion;
    }

    public static void setPluginVersion(String pluginVersion) {
        ClientProfile.pluginVersion = pluginVersion;
    }

    public static String getServerDescription() {
        return serverDescription;
    }

    public static void setServerDescription(String serverDescription) {
        ClientProfile.serverDescription = serverDescription;
    }

    public static boolean isDebugMode() {
        return debugMode;
    }

    public static void setDebugMode(boolean debugMode) {
        ClientProfile.debugMode = debugMode;
    }

    public static boolean isOnlineMode() {
        return onlineMode;
    }

    public static void setOnlineMode(boolean onlineMode) {
        ClientProfile.onlineMode = onlineMode;
    }

    public static boolean isCommandSupported() {
        return commandSupported;
    }

    public static void setCommandSupported(boolean commandSupported) {
        ClientProfile.commandSupported = commandSupported;
    }

    public static boolean isPapiSupported() {
        return papiSupported;
    }

    public static void setPapiSupported(boolean papiSupported) {
        ClientProfile.papiSupported = papiSupported;
    }

    public static boolean isHasGeyser() {
        return hasGeyser;
    }

    public static void setHasGeyser(boolean hasGeyser) {
        ClientProfile.hasGeyser = hasGeyser;
    }

    public static boolean isHasSkinsRestorer() {
        return hasSkinsRestorer;
    }

    public static void setHasSkinsRestorer(boolean hasSkinsRestorer) {
        ClientProfile.hasSkinsRestorer = hasSkinsRestorer;
    }

    public static boolean isHasPaperSkinApi() {
        return hasPaperSkinApi;
    }

    public static void setHasPaperSkinApi(boolean hasPaperSkinApi) {
        ClientProfile.hasPaperSkinApi = hasPaperSkinApi;
    }

    public static boolean isSyncMessageMoney() {
        return syncMessageMoney;
    }

    public static void setSyncMessageMoney(boolean syncMessageMoney) {
        ClientProfile.syncMessageMoney = syncMessageMoney;
    }

    public static String getSyncMessageMode() {
        return syncMessageMode;
    }

    public static void setSyncMessageMode(String syncMessageMode) {
        ClientProfile.syncMessageMode = syncMessageMode;
    }
    
    // 假人过滤相关方法
    public static boolean isBotFilterEnabled() {
        return botFilterEnabled;
    }

    public static void setBotFilterEnabled(boolean botFilterEnabled) {
        ClientProfile.botFilterEnabled = botFilterEnabled;
    }

    public static List<String> getBotPrefixes() {
        return botPrefixes;
    }

    public static void setBotPrefixes(List<String> botPrefixes) {
        ClientProfile.botPrefixes = botPrefixes;
    }

    public static boolean isCarpetBotFilterEnabled() {
        return carpetBotFilterEnabled;
    }

    public static void setCarpetBotFilterEnabled(boolean carpetBotFilterEnabled) {
        ClientProfile.carpetBotFilterEnabled = carpetBotFilterEnabled;
    }

    public static List<String> getCarpetBotPrefixes() {
        return carpetBotPrefixes;
    }

    public static void setCarpetBotPrefixes(List<String> carpetBotPrefixes) {
        ClientProfile.carpetBotPrefixes = carpetBotPrefixes;
    }
    
    // 检查玩家是否为假人
    public static boolean isBot(String playerName) {
        if (!botFilterEnabled) {
            return false;
        }
        
        for (String prefix : botPrefixes) {
            if (playerName.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
    
    // 检查玩家是否为Carpet假人
    public static boolean isCarpetBot(String playerName) {
        if (!carpetBotFilterEnabled) {
            return false;
        }
        
        for (String prefix : carpetBotPrefixes) {
            if (playerName.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}