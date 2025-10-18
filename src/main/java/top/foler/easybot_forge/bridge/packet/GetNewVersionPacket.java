package top.foler.easybot_forge.bridge.packet;

public class GetNewVersionPacket extends PacketWithCallBackId {
    public GetNewVersionPacket() {
        setOperation("GET_NEW_VERSION");
    }
}