package top.foler.easybot_forge.bridge.packet;

import com.google.gson.annotations.SerializedName;

/**
 * 跨服消息数据包
 */
public class SendCrossServerMessagePacket extends PacketWithCallBackId {
    @SerializedName("player")
    private PlayerInfoWithRaw player;
    @SerializedName("message")
    private String message;
    
    public SendCrossServerMessagePacket() {
        setOperation("CROSS_SERVER_MESSAGE");
    }

    public PlayerInfoWithRaw getPlayer() {
        return player;
    }

    public void setPlayer(PlayerInfoWithRaw player) {
        this.player = player;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}