package top.foler.easybot_forge.bridge;

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
}