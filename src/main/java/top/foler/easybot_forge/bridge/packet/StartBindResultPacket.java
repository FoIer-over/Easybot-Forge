package top.foler.easybot_forge.bridge.packet;

import com.google.gson.annotations.SerializedName;

public class StartBindResultPacket extends PacketWithCallBackId {
    @SerializedName("code")
    private String code;
    @SerializedName("time")
    private String time;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}