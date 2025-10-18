package top.foler.easybot_forge.bridge.packet;

import com.google.gson.annotations.SerializedName;

public class PlayerUnBindNotifyPacket extends PacketWithCallBackId {
    @SerializedName("player_name")
    private String playerName;
    @SerializedName("kick_message")
    private String kickMessage;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getKickMessage() {
        return kickMessage;
    }

    public void setKickMessage(String kickMessage) {
        this.kickMessage = kickMessage;
    }
}