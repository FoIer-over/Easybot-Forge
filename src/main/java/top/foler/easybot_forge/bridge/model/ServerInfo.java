package top.foler.easybot_forge.bridge.model;

import com.google.gson.annotations.SerializedName;

public class ServerInfo {
    @SerializedName("server_name")
    private String serverName = "Unknown";
    @SerializedName("plugin_version")
    private String pluginVersion = "1.0.0";
    @SerializedName("server_version")
    private String serverVersion = "Unknown";
    @SerializedName("is_command_supported")
    private boolean commandSupported = true;
    @SerializedName("is_papi_supported")
    private boolean papiSupported = false;
    @SerializedName("has_geyser")
    private boolean hasGeyser = false;
    @SerializedName("is_online_mode")
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