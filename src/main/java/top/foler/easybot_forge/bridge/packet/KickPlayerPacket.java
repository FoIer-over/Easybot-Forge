package top.foler.easybot_forge.bridge.packet;

import top.foler.easybot_forge.bridge.OpCode;

public class KickPlayerPacket extends Packet {
    private String player;
    private String kickMessage;

    public KickPlayerPacket(String player, String kickMessage) {
        setOpCode(OpCode.Packet);
        this.player = player;
        this.kickMessage = kickMessage;
    }

    public String getPlayer() {
        return player;
    }

    public String getKickMessage() {
        return kickMessage;
    }
}