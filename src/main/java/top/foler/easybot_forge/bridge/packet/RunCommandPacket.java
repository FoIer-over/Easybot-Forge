package top.foler.easybot_forge.bridge.packet;

import com.google.gson.annotations.SerializedName;

public class RunCommandPacket extends PacketWithCallBackId {
    @SerializedName("player_name")
    private String playerName;
    @SerializedName("command")
    private String command;
    @SerializedName("enable_papi")
    private boolean enablePapi;

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public boolean isEnablePapi() {
        return enablePapi;
    }

    public void setEnablePapi(boolean enablePapi) {
        this.enablePapi = enablePapi;
    }
}