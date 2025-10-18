package top.foler.easybot_forge.bridge.packet;

import top.foler.easybot_forge.bridge.OpCode;
import top.foler.easybot_forge.bridge.model.ServerInfo;

public class GetInfoResultPacket extends PacketWithCallBackId {
    private ServerInfo info;

    public GetInfoResultPacket(ServerInfo info) {
        setOpCode(OpCode.Packet);
        setOperation("GET_INFO_RESULT"); // 设置操作类型
        this.info = info;
    }

    public ServerInfo getInfo() {
        return info;
    }
}