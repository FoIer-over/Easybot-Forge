package top.foler.easybot_forge.bridge.packet;

import com.google.gson.annotations.SerializedName;

public class GetBindInfoResultPacket extends PacketWithCallBackId {
    @SerializedName("name")
    private String name;
    @SerializedName("platform")
    private String platform;
    @SerializedName("bind_names")
    private String bindName;
    @SerializedName("id")
    private String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getBindName() {
        return bindName;
    }

    public void setBindName(String bindName) {
        this.bindName = bindName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}