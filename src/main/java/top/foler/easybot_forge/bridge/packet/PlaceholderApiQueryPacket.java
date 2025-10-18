package top.foler.easybot_forge.bridge.packet;

import com.google.gson.annotations.SerializedName;

public class PlaceholderApiQueryPacket extends PacketWithCallBackId {
    @SerializedName("player_name")
    private String playerName;
    @SerializedName("query_text")
    private String text;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}