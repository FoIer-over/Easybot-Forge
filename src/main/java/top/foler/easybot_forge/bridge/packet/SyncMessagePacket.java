package top.foler.easybot_forge.bridge.packet;

import com.google.gson.annotations.SerializedName;

public class SyncMessagePacket extends PacketWithCallBackId {
    @SerializedName("player")
    private PlayerInfoWithRaw player;
    @SerializedName("message")
    private String message;
    @SerializedName("use_command")
    private boolean useCommand;
    
    public SyncMessagePacket(){
        setOperation("SYNC_MESSAGE");
    }

    public PlayerInfoWithRaw getPlayer() {
        return player;
    }

    public void setPlayer(PlayerInfoWithRaw player) {
        this.player = player;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isUseCommand() {
        return useCommand;
    }

    public void setUseCommand(boolean useCommand) {
        this.useCommand = useCommand;
    }
}