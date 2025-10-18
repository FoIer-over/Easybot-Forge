package top.foler.easybot_forge.bridge.packet;

import com.google.gson.annotations.SerializedName;
import top.foler.easybot_forge.bridge.model.PlayerInfo;

import java.util.List;

public class PlayerListPacket {
    @SerializedName("list")
    private List<PlayerInfo> list;

    public List<PlayerInfo> getList() {
        return list;
    }

    public void setList(List<PlayerInfo> list) {
        this.list = list;
    }
}