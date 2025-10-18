package top.foler.easybot_forge.bridge.packet;

import com.google.gson.annotations.SerializedName;

public class StartBindPacket extends PacketWithCallBackId {
    @SerializedName("player_name")
    private String playerName;

    public StartBindPacket() {
        setOperation("START_BIND");
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}