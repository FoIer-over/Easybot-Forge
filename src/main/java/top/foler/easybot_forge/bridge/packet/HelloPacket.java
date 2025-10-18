package top.foler.easybot_forge.bridge.packet;

import com.google.gson.annotations.SerializedName;

public class HelloPacket extends Packet {
    @SerializedName("version")
    private String version;

    @SerializedName("system")
    private String systemName;

    @SerializedName("dotnet")
    private String dotnetVersion;

    @SerializedName("session_id")
    private String sessionId;

    @SerializedName("token")
    private String token;

    @SerializedName("interval")
    private int interval;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getDotnetVersion() {
        return dotnetVersion;
    }

    public void setDotnetVersion(String dotnetVersion) {
        this.dotnetVersion = dotnetVersion;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
}