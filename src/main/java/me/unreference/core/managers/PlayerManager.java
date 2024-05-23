package me.unreference.core.managers;

import me.unreference.core.models.Rank;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerManager implements Listener {

    @EventHandler
    private static void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        RankManager rankManager = RankManager.getInstance();

        Rank rank = rankManager.getPlayerRank(player);
        if (rank == null) {
            rankManager.setPlayerRank(player, Rank.PLAYER);
        }
    }

}
