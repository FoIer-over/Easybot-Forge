package top.foler.easybot_forge.bridge.packet;

import com.google.gson.annotations.SerializedName;
import top.foler.easybot_forge.bridge.OpCode;

public class DataRecordPacket extends PacketWithCallBackId {
    @SerializedName("data")
    private String data;
    @SerializedName("name")
    private String name;
    @SerializedName("token")
    private String token;
    @SerializedName("type")
    private RecordTypeEnum type;

    public DataRecordPacket() {
        setOpCode(OpCode.Packet);
        setOperation("DATA_RECORD");
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public RecordTypeEnum getType() {
        return type;
    }

    public void setType(RecordTypeEnum type) {
        this.type = type;
    }
}