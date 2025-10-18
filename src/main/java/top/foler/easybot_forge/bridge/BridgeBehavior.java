package top.foler.easybot_forge.bridge;

import top.foler.easybot_forge.bridge.message.Segment;
import top.foler.easybot_forge.bridge.model.PlayerInfo;
import top.foler.easybot_forge.bridge.model.ServerInfo;
import java.util.List;

public interface BridgeBehavior {
    String runCommand(String playerName, String command, boolean enablePapi);
    String papiQuery(String playerName, String query);
    ServerInfo getInfo();
    void SyncToChat(String message);
    void BindSuccessBroadcast(String playerName,String accountId, String accountName);
    void KickPlayer(String player, String kickMessage);
    void SyncToChatExtra(List<Segment> segments, String text);
    List<PlayerInfo> getPlayerList();
    int getOnlinePlayerCount();
    int getMaxPlayers();
}