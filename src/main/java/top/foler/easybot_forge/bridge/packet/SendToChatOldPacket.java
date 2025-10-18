package top.foler.easybot_forge.bridge.packet;

import com.google.gson.annotations.SerializedName;

public class SendToChatOldPacket extends PacketWithCallBackId {
    @SerializedName("text")
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}