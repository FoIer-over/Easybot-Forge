package top.foler.easybot_forge.bridge.packet;

import com.google.gson.annotations.SerializedName;

public class PlayerInfoWithRaw {
    @SerializedName("ip")
    public String ip;
    @SerializedName("player_name")
    public String name;
    @SerializedName("player_uuid")
    public String uuid;
    @SerializedName("player_name_raw")
    public String nameRaw;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getNameRaw() {
        return nameRaw;
    }

    public void setNameRaw(String nameRaw) {
        this.nameRaw = nameRaw;
    }
}