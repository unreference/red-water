package me.unreference.core.managers;

import me.unreference.core.events.PlayerRankChangeEvent;
import me.unreference.core.models.Rank;
import me.unreference.core.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerManager implements Listener {
  private static final String PERMISSION_JOIN_FULL = "server.join-full";
  private static final String PERMISSION_AUTO_OP = "server.auto-op";

  public PlayerManager() {
    Rank.ULTRA.grantPermission(PERMISSION_JOIN_FULL, true);
    Rank.LT.grantPermission(PERMISSION_AUTO_OP, true);
  }

  @EventHandler
  private static void onRankChange(PlayerRankChangeEvent event) {
    RankManager rankManager = RankManager.getInstance();
    Player player = event.getPlayer();
    Rank rank = rankManager.getPlayerRank(player);
    player.setOp(rank.isPermitted(PERMISSION_AUTO_OP));
  }

  @EventHandler
  private static void onPlayerQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    event.quitMessage(MessageUtil.getPrefixedMessage("&8Quit>", "&8%s", player.getName()));
  }

  @EventHandler
  private static void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    RankManager rankManager = RankManager.getInstance();

    Rank rank = rankManager.getPlayerRank(player);
    if (rank == null) {
      rankManager.setPlayerRank(player, Rank.PLAYER);
    } else {
      player.setOp(rank.isPermitted(PERMISSION_AUTO_OP));
    }

    event.joinMessage(MessageUtil.getPrefixedMessage("&8Join>", "&8%s", player.getName()));
  }

  @EventHandler
  public void onLogin(PlayerLoginEvent event) {
    if (Bukkit.getOnlinePlayers().size() >= Bukkit.getServer().getMaxPlayers()) {
      Player player = event.getPlayer();
      Rank rank = RankManager.getInstance().getPlayerRank(player);

      if (rank.isPermitted(PERMISSION_JOIN_FULL)) {
        event.allow();
        return;
      }

      event.disallow(PlayerLoginEvent.Result.KICK_FULL, MessageUtil.getMessage("The server is full!"));
    }
  }
}
