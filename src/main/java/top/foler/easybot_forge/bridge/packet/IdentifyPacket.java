package top.foler.easybot_forge.bridge.packet;

import com.google.gson.annotations.SerializedName;
import top.foler.easybot_forge.bridge.OpCode;

public class IdentifyPacket {
    @SerializedName("op")
    private OpCode opCode = OpCode.Identify;
    @SerializedName("plugin_version")
    private String pluginVersion = "NONE";
    @SerializedName("server_description")
    private String serverDescription = "NONE";
    @SerializedName("token")
    private String token = "";
    @SerializedName("is_papi_supported")
    private boolean papiSupported = false;
    @SerializedName("is_command_supported")
    private boolean commandSupported = true;

    public IdentifyPacket(String token) {
        this.token = token;
    }

    public OpCode getOpCode() {
        return opCode;
    }

    public void setOpCode(OpCode opCode) {
        this.opCode = opCode;
    }

    public String getPluginVersion() {
        return pluginVersion;
    }

    public void setPluginVersion(String pluginVersion) {
        this.pluginVersion = pluginVersion;
    }

    public String getServerDescription() {
        return serverDescription;
    }

    public void setServerDescription(String serverDescription) {
        this.serverDescription = serverDescription;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public boolean isPapiSupported() {
        return papiSupported;
    }

    public void setPapiSupported(boolean papiSupported) {
        this.papiSupported = papiSupported;
    }

    public boolean isCommandSupported() {
        return commandSupported;
    }

    public void setCommandSupported(boolean commandSupported) {
        this.commandSupported = commandSupported;
    }
}