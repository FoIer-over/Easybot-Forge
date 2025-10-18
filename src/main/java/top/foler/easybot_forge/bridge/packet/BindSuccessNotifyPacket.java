package top.foler.easybot_forge.bridge.packet;

import com.google.gson.annotations.SerializedName;

public class BindSuccessNotifyPacket extends PacketWithCallBackId {
    @SerializedName("player_name")
    private String playerName;
    @SerializedName("account_id")
    private String accountId;
    @SerializedName("account_name")
    private String accountName;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }
}