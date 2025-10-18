package top.foler.easybot_forge.bridge.packet;

import com.google.gson.annotations.SerializedName;

public class SyncDeathMessagePacket extends PacketWithCallBackId {
    @SerializedName("player")
    private PlayerInfoWithRaw player;
    @SerializedName("raw")
    private String raw;
    @SerializedName("killer")
    private String killer;
    
    public SyncDeathMessagePacket(){
        setOperation("SYNC_DEATH_MESSAGE");
    }

    public PlayerInfoWithRaw getPlayer() {
        return player;
    }

    public void setPlayer(PlayerInfoWithRaw player) {
        this.player = player;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public String getKiller() {
        return killer;
    }

    public void setKiller(String killer) {
        this.killer = killer;
    }
}