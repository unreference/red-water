package me.unreference.core.managers;

import me.unreference.core.events.RankChangeEvent;
import me.unreference.core.events.ServerTickEvent;
import me.unreference.core.models.Rank;
import me.unreference.core.models.Scoreboard;
import org.bukkit.Bukkit;
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
    private static final Map<UUID, Scoreboard> PLAYER_SCOREBOARDS = new HashMap<>();
    private static final Map<UUID, Rank> PLAYER_RANKS = new HashMap<>();

    @EventHandler
    private static void onServerTick(ServerTickEvent event) {
        // Every minute
        if (event.getTicks() % 1200 == 0) {
            update();
        }
    }

    @EventHandler
    private static void onRankChange(RankChangeEvent event) {
        update();
    }

    @EventHandler
    private static void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (PLAYER_SCOREBOARDS.containsKey(playerId)) {
            return;
        }

        Scoreboard scoreboard = new Scoreboard(player);
        PLAYER_SCOREBOARDS.put(playerId, scoreboard);

        setup(scoreboard);
        addPlayerToScoreboard(player, scoreboard);
        player.setScoreboard(scoreboard.getHandle());

        RankManager rankManager = RankManager.getInstance();
        Rank rank = rankManager.getPlayerRank(player);
        PLAYER_RANKS.put(playerId, rank);
    }

    @EventHandler
    private static void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (PLAYER_SCOREBOARDS.containsKey(playerId)) {
            Scoreboard scoreboard = PLAYER_SCOREBOARDS.get(playerId);
            removePlayerFromScoreboard(player, scoreboard);
            PLAYER_SCOREBOARDS.remove(playerId);
            PLAYER_RANKS.remove(playerId);
        }
    }

    private static void setup(Scoreboard scoreboard) {
        Rank[] invertedRanks = getInvertedRankValues();
        for (Rank rank : invertedRanks) {
            int index = getInvertedRankIndex(rank);
            String teamName = index + rank.name();
            if (scoreboard.getHandle().getTeam(teamName) == null) {
                Team team = scoreboard.getHandle().registerNewTeam(teamName);
                team.prefix(rank.getPrefixFormatting());
                team.color(rank.getPlayerNameColor());
            }
        }
    }

    private static Rank[] getInvertedRankValues() {
        Rank[] ranks = Rank.values();
        Rank[] invertedRanks = new Rank[ranks.length];
        for (int i = 0; i < ranks.length; ++i) {
            invertedRanks[i] = ranks[ranks.length - 1 - i];
        }

        return invertedRanks;
    }

    private static int getInvertedRankIndex(Rank rank) {
        Rank[] invertedRanks = getInvertedRankValues();
        for (int i = 0; i < invertedRanks.length; i++) {
            if (invertedRanks[i] == rank) {
                return i;
            }
        }

        return -1;
    }

    private static void update() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            UUID playerId = player.getUniqueId();
            Scoreboard scoreboard = PLAYER_SCOREBOARDS.get(playerId);

            if (scoreboard != null) {
                updatePlayerScoreboard(player, scoreboard);
            }
        }
    }

    private static void updatePlayerScoreboard(Player player, Scoreboard scoreboard) {
        RankManager rankManager = RankManager.getInstance();
        Rank currentRank = rankManager.getPlayerRank(player);
        UUID playerId = player.getUniqueId();

        Rank previousRank = PLAYER_RANKS.get(playerId);
        if (previousRank != currentRank) {
            if (previousRank != null) {
                String previousTeamName = getInvertedRankValues()[getInvertedRankIndex(previousRank)].name();
                Team previousTeam = scoreboard.getHandle().getTeam(previousTeamName);
                if (previousTeam != null) {
                    previousTeam.removeEntry(player.getName());
                }
            }
        }

        String currentTeamName = getInvertedRankIndex(currentRank) + currentRank.name();
        Team currentTeam = scoreboard.getHandle().getTeam(currentTeamName);
        if (currentTeam != null) {
            currentTeam.addEntry(player.getName());
        }

        PLAYER_RANKS.put(playerId, currentRank);
    }

    private static void addPlayerToScoreboard(Player player, Scoreboard scoreboard) {
        RankManager rankManager = RankManager.getInstance();
        Rank rank = rankManager.getPlayerRank(player);
        String teamName = getInvertedRankIndex(rank) + rank.name();

        Team team = scoreboard.getHandle().getTeam(teamName);
        if (team != null) {
            team.addEntry(player.getName());
        }
    }

    private static void removePlayerFromScoreboard(Player player, Scoreboard scoreboard) {
        RankManager rankManager = RankManager.getInstance();
        Rank rank = rankManager.getPlayerRank(player);
        Rank[] invertedRanks = getInvertedRankValues();
        String teamName = invertedRanks[getInvertedRankIndex(rank)].name();

        Team team = scoreboard.getHandle().getTeam(teamName);
        if (team != null) {
            team.removeEntry(player.getName());
        }
    }
}
