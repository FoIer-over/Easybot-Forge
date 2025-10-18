package top.foler.easybot_forge.bridge.packet;

import top.foler.easybot_forge.bridge.OpCode;

public class PlayerLeavePacket extends Packet {
    private String playerName;
    private String playerUUID;

    public PlayerLeavePacket(String playerName, String playerUUID) {
        setOpCode(OpCode.Packet);
        this.playerName = playerName;
        this.playerUUID = playerUUID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getPlayerUUID() {
        return playerUUID;
    }
}