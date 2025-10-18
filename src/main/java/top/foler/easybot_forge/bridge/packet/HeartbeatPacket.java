package top.foler.easybot_forge.bridge.packet;

import top.foler.easybot_forge.bridge.OpCode;

public class HeartbeatPacket extends Packet {
    public HeartbeatPacket() {
        setOpCode(OpCode.HeartBeat);
    }
}