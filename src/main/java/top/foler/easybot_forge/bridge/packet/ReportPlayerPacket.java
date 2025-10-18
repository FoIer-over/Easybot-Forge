package top.foler.easybot_forge.bridge.packet;

import com.google.gson.annotations.SerializedName;

public class ReportPlayerPacket extends PacketWithCallBackId {
    @SerializedName("player_name")
    private String playerName;
    @SerializedName("player_uuid")
    private String playerUuid;
    @SerializedName("player_ip")
    private String playerIp;
    
    public ReportPlayerPacket(){
        setOperation("REPORT_PLAYER");
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerUuid() {
        return playerUuid;
    }

    public void setPlayerUuid(String playerUuid) {
        this.playerUuid = playerUuid;
    }

    public String getPlayerIp() {
        return playerIp;
    }

    public void setPlayerIp(String playerIp) {
        this.playerIp = playerIp;
    }
}