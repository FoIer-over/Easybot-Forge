package top.foler.easybot_forge.bridge.packet;

import com.google.gson.annotations.SerializedName;

public class GetBindInfoPacket extends PacketWithCallBackId {
    @SerializedName("player_name")
    private String playerName;

    public GetBindInfoPacket() {
        setOperation("GET_BIND_INFO");
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}