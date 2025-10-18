package top.foler.easybot_forge.bridge.packet;

import top.foler.easybot_forge.bridge.OpCode;

public class NeedSyncSettingsPacket extends PacketWithCallBackId {
    public NeedSyncSettingsPacket() {
        setOperation("NEED_SYNC_SETTING");
        setOpCode(OpCode.Packet);
    }
}