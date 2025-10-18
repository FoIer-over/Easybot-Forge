package top.foler.easybot_forge.bridge.packet;

import com.google.gson.annotations.SerializedName;
public class IdentifySuccessPacket extends Packet {
    @SerializedName("server_name")
    private String serverName;

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
}