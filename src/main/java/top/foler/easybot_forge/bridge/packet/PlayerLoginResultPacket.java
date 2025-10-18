package top.foler.easybot_forge.bridge.packet;

import com.google.gson.annotations.SerializedName;

public class PlayerLoginResultPacket extends PacketWithCallBackId {
    @SerializedName("kick")
    private Boolean kick;
    @SerializedName("kick_message")
    private String kickMessage;

    public Boolean getKick() {
        return kick;
    }

    public void setKick(Boolean kick) {
        this.kick = kick;
    }

    public String getKickMessage() {
        return kickMessage;
    }

    public void setKickMessage(String kickMessage) {
        this.kickMessage = kickMessage;
    }
}