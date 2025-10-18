package top.foler.easybot_forge.bridge.packet;

import com.google.gson.annotations.SerializedName;

public class GetNewVersionResultPacket extends PacketWithCallBackId {
    @SerializedName("version_name")
    private String versionName;
    @SerializedName("download_url")
    private String downloadUrl;
    @SerializedName("publish_time")
    private String publishTime;
    @SerializedName("update_log")
    private String updateLog;

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getUpdateLog() {
        return updateLog;
    }

    public void setUpdateLog(String updateLog) {
        this.updateLog = updateLog;
    }
}