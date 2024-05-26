package me.unreference.core.managers;

import me.unreference.core.models.Rank;
import me.unreference.core.utils.MessageUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerManager implements Listener {

    @EventHandler
    private static void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        event.quitMessage(
                MessageUtil.getPrefixedMessage("&8Quit>",
                        "&8%s", player.getName()));
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        RankManager rankManager = RankManager.getInstance();

        Rank rank = rankManager.getPlayerRank(player);
        if (rank == null) {
            rankManager.setPlayerRank(player, Rank.PLAYER);
        }

        event.joinMessage(
                MessageUtil.getPrefixedMessage("&8Join>",
                        "&8%s", player.getName()));
    }
}
