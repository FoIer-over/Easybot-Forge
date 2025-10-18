package top.foler.easybot_forge.bridge.packet;

import com.google.gson.annotations.SerializedName;

public class SetSocialAccountResultPacket extends PacketWithCallBackId {
    @SerializedName("name")
    private String name;
    @SerializedName("time")
    private String time;
    @SerializedName("uuid")
    private String uuid;
    @SerializedName("platform")
    private String platform;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }
}