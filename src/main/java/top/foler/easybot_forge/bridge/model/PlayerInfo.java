package top.foler.easybot_forge.bridge.model;

import com.google.gson.annotations.SerializedName;

public class PlayerInfo {
    @SerializedName("player_name")
    private String playerName;

    @SerializedName("player_uuid")
    private String playerUuid;

    @SerializedName("ip")
    private String ip;

    @SerializedName("skin_url")
    private String skinUrl;

    @SerializedName("bedrock")
    private boolean bedrock;

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

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSkinUrl() {
        return skinUrl;
    }

    public void setSkinUrl(String skinUrl) {
        this.skinUrl = skinUrl;
    }

    public boolean isBedrock() {
        return bedrock;
    }

    public void setBedrock(boolean bedrock) {
        this.bedrock = bedrock;
    }
}