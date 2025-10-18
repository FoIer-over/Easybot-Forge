package top.foler.easybot_forge.bridge.packet;

import com.google.gson.annotations.SerializedName;

public class SyncEnterExitMessagePacket extends PacketWithCallBackId {
    @SerializedName("player")
    private PlayerInfoWithRaw player;
    @SerializedName("is_enter")
    private boolean isEnter;
    
    public SyncEnterExitMessagePacket(){
        setOperation("SYNC_ENTER_EXIT_MESSAGE");
    }

    public PlayerInfoWithRaw getPlayer() {
        return player;
    }

    public void setPlayer(PlayerInfoWithRaw player) {
        this.player = player;
    }

    public boolean isEnter() {
        return isEnter;
    }

    public void setEnter(boolean isEnter) {
        this.isEnter = isEnter;
    }
}