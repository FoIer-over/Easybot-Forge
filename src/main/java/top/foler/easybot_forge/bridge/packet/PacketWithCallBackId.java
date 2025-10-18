package top.foler.easybot_forge.bridge.packet;

import com.google.gson.annotations.SerializedName;

public class PacketWithCallBackId extends Packet {
    // 移除重复的opCode字段声明，继承自父类Packet
    
    @SerializedName("callback_id")
    private String CallBackId;

    @SerializedName("exec_op")
    private String Operation;

    // 使用父类的getter和setter方法，无需重写

    public String getCallBackId() {
        return CallBackId;
    }

    public void setCallBackId(String CallBackId) {
        this.CallBackId = CallBackId;
    }

    public String getOperation() {
        return Operation;
    }

    public void setOperation(String Operation) {
        this.Operation = Operation;
    }
}