package top.foler.easybot_forge.bridge.packet;

import com.google.gson.annotations.SerializedName;
import top.foler.easybot_forge.bridge.model.PlayerInfo;

public class OnPlayerJoinPacket extends PacketWithCallBackId {
    @SerializedName("player")
    private PlayerInfo playerInfo;
    
    public OnPlayerJoinPacket() {
        setOperation("PLAYER_JOIN");
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }

    public void setPlayerInfo(PlayerInfo playerInfo) {
        this.playerInfo = playerInfo;
    }
}