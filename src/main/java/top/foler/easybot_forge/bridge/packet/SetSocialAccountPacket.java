package top.foler.easybot_forge.bridge.packet;

import com.google.gson.annotations.SerializedName;

public class SetSocialAccountPacket extends PacketWithCallBackId {
    @SerializedName("player_name")
    private String playerName;

    public SetSocialAccountPacket() {
        setOperation("GET_SOCIAL_ACCOUNT");
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}