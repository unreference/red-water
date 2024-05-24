package me.unreference.core.managers;

import com.destroystokyo.paper.utils.PaperPluginLogger;
import me.unreference.core.models.Rank;
import me.unreference.core.models.Scoreboard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ScoreboardManager implements Listener {
    private static final Map<UUID, Scoreboard> SCOREBOARDS = new HashMap<>();

    @EventHandler
    private static void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        PaperPluginLogger.getAnonymousLogger().info("Player joined: " + player.getName());

        if (SCOREBOARDS.containsKey(playerId)) {
            return;
        }

        Scoreboard scoreboard = new Scoreboard(player);
        SCOREBOARDS.put(playerId, scoreboard);

        setup(scoreboard);
        addPlayerToScoreboard(player, scoreboard);
        player.setScoreboard(scoreboard.getHandle());
    }

    @EventHandler
    private static void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (SCOREBOARDS.containsKey(playerId)) {
            Scoreboard scoreboard = SCOREBOARDS.get(playerId);
            removePlayerFromScoreboard(player, scoreboard);
            SCOREBOARDS.remove(playerId);
        }
    }

    private static void setup(Scoreboard scoreboard) {
        for (Rank rank : Rank.values()) {
            if (scoreboard.getHandle().getTeam(rank.name()) == null) {
                Team team = scoreboard.getHandle().registerNewTeam(rank.name());
                team.prefix(rank.getDisplay());
            }
        }
    }

    private static void addPlayerToScoreboard(Player player, Scoreboard scoreboard) {
        RankManager rankManager = RankManager.getInstance();
        Rank rank = rankManager.getPlayerRank(player);

        Team team = scoreboard.getHandle().getTeam(rank.name());
        if (team != null) {
            team.addEntry(player.getName());
        }
    }

    private static void removePlayerFromScoreboard(Player player, Scoreboard scoreboard) {
        RankManager rankManager = RankManager.getInstance();
        Rank rank = rankManager.getPlayerRank(player);

        Team team = scoreboard.getHandle().getTeam(rank.name());
        if (team != null) {
            team.removeEntry(player.getName());
        }
    }
}
