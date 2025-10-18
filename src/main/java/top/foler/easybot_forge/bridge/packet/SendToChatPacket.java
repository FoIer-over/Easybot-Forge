package top.foler.easybot_forge.bridge.packet;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

public class SendToChatPacket extends PacketWithCallBackId {
    @SerializedName("text")
    private String text;
    @SerializedName("extra")
    private JsonArray extra;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public JsonArray getExtra() {
        return extra;
    }

    public void setExtra(JsonArray extra) {
        this.extra = extra;
    }
}