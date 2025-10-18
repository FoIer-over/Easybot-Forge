package top.foler.easybot_forge.bridge.packet;

import com.google.gson.annotations.SerializedName;

public class UpdateSyncSettingsPacket extends PacketWithCallBackId {
    @SerializedName("sync_mode")
    private int syncMode;
    @SerializedName("sync_money")
    private int syncMoney;

    public int getSyncMode() {
        return syncMode;
    }

    public void setSyncMode(int syncMode) {
        this.syncMode = syncMode;
    }

    public int getSyncMoney() {
        return syncMoney;
    }

    public void setSyncMoney(int syncMoney) {
        this.syncMoney = syncMoney;
    }
}