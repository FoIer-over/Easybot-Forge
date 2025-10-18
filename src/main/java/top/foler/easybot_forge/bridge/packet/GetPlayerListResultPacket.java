package top.foler.easybot_forge.bridge.packet;

import top.foler.easybot_forge.bridge.OpCode;
import top.foler.easybot_forge.bridge.model.PlayerInfo;

import java.util.List;

public class GetPlayerListResultPacket extends Packet {
    private List<PlayerInfo> players;

    public GetPlayerListResultPacket(List<PlayerInfo> players) {
        setOpCode(OpCode.Packet);
        this.players = players;
    }

    public List<PlayerInfo> getPlayers() {
        return players;
    }
}