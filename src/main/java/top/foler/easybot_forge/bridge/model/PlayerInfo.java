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
    @SerializedName("is_bedrock_player")
    private boolean bedrock;
    
    public PlayerInfo(String playerName, String playerUuid, String ip, boolean bedrock) {
        this.playerName = playerName;
        this.playerUuid = playerUuid;
        this.ip = ip;
        this.skinUrl = "https://crafatar.com/skins/" + playerUuid;
        this.bedrock = bedrock;
    }
    
    // 默认构造函数，用于反序列化或动态设置属性
    public PlayerInfo() {
        this.skinUrl = "";
        this.bedrock = false;
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