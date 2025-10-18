package top.foler.easybot_forge.bridge.model;

public class ServerInfo {
    private String serverName = "Unknown";
    private String pluginVersion = "1.0.0";
    private String serverVersion = "Unknown";
    private boolean commandSupported = true;
    private boolean papiSupported = false;
    private boolean hasGeyser = false;
    private boolean onlineMode = false;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getPluginVersion() {
        return pluginVersion;
    }

    public void setPluginVersion(String pluginVersion) {
        this.pluginVersion = pluginVersion;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public boolean isCommandSupported() {
        return commandSupported;
    }

    public void setCommandSupported(boolean commandSupported) {
        this.commandSupported = commandSupported;
    }

    public boolean isPapiSupported() {
        return papiSupported;
    }

    public void setPapiSupported(boolean papiSupported) {
        this.papiSupported = papiSupported;
    }

    public boolean isHasGeyser() {
        return hasGeyser;
    }

    public void setHasGeyser(boolean hasGeyser) {
        this.hasGeyser = hasGeyser;
    }

    public boolean isOnlineMode() {
        return onlineMode;
    }

    public void setOnlineMode(boolean onlineMode) {
        this.onlineMode = onlineMode;
    }
}