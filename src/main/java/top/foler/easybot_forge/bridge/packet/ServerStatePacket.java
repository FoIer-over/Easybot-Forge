package top.foler.easybot_forge.bridge.packet;

import com.google.gson.annotations.SerializedName;
import top.foler.easybot_forge.bridge.OpCode;

public class ServerStatePacket extends PacketWithCallBackId {
    @SerializedName("token")
    private String token;
    @SerializedName("players")
    private String players;
    
    public ServerStatePacket() {
        setOpCode(OpCode.Packet);
        setOperation("SERVER_STATE_CHANGED");
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPlayers() {
        return players;
    }

    public void setPlayers(String players) {
        this.players = players;
    }
}