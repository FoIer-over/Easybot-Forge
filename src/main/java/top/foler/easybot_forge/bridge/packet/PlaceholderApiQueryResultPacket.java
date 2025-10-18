package top.foler.easybot_forge.bridge.packet;

import com.google.gson.annotations.SerializedName;

public class PlaceholderApiQueryResultPacket {
    @SerializedName("text")
    private String text;
    @SerializedName("success")
    private boolean success;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}