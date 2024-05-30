package me.unreference.core.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
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
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Team;

public class ScoreboardManager implements Listener {
  private static final Map<UUID, Scoreboard> SCOREBOARD_PLAYERS = new HashMap<>();
  private static final Map<UUID, Rank> SCOREBOARD_RANKS = new HashMap<>();

  @EventHandler
  private static void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();
    UUID playerId = player.getUniqueId();

    if (SCOREBOARD_PLAYERS.containsKey(playerId)) {
      return;
    }

    Scoreboard scoreboard = new Scoreboard(player);
    SCOREBOARD_PLAYERS.put(playerId, scoreboard);

    setup(scoreboard);
    addPlayerToScoreboard(player, scoreboard);
    player.setScoreboard(scoreboard.getHandle());
    updatePlayerScoreboard(player, scoreboard);

    RankManager rankManager = RankManager.getInstance();
    Rank rank = rankManager.getPlayerRank(player);
    SCOREBOARD_RANKS.put(playerId, rank);
  }

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
  private static void onPlayerQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    UUID playerId = player.getUniqueId();

    if (SCOREBOARD_PLAYERS.containsKey(playerId)) {
      Scoreboard scoreboard = SCOREBOARD_PLAYERS.get(playerId);
      removePlayerFromScoreboard(player, scoreboard);
      SCOREBOARD_PLAYERS.remove(playerId);
      SCOREBOARD_RANKS.remove(playerId);
    }
  }

  @EventHandler
  private static void onPluginDisable(PluginDisableEvent event) {
    reset();
  }

  private static void setup(Scoreboard scoreboard) {
    Rank[] invertedRanks = getSortedRanks();
    for (Rank rank : invertedRanks) {
      if (scoreboard.getHandle().getTeam(getSortedRankName(rank)) == null) {
        Team team = scoreboard.getHandle().registerNewTeam(getSortedRankName(rank));
        team.prefix(rank.getPrefixFormatting());
      }
    }
  }

  private static void update() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      UUID playerId = player.getUniqueId();
      Scoreboard scoreboard = SCOREBOARD_PLAYERS.get(playerId);

      if (scoreboard != null) {
        updatePlayerScoreboard(player, scoreboard);
      }
    }
  }

  private static void reset() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      UUID playerId = player.getUniqueId();
      Scoreboard scoreboard = SCOREBOARD_PLAYERS.get(playerId);

      scoreboard.getHandle().getObjectives().forEach(Objective::unregister);
      scoreboard.getHandle().getTeams().forEach(Team::unregister);
    }
  }

  private static void addPlayerToScoreboard(Player player, Scoreboard scoreboard) {
    RankManager rankManager = RankManager.getInstance();
    Rank rank = rankManager.getPlayerRank(player);

    Team team = scoreboard.getHandle().getTeam(getSortedRankName(rank));
    if (team != null) {
      team.addEntry(player.getName());
    }
  }

  private static void removePlayerFromScoreboard(Player player, Scoreboard scoreboard) {
    RankManager rankManager = RankManager.getInstance();
    Rank rank = rankManager.getPlayerRank(player);

    Team team = scoreboard.getHandle().getTeam(getSortedRankName(rank));
    if (team != null) {
      team.removeEntry(player.getName());
    }
  }

  private static void updatePlayerScoreboard(Player player, Scoreboard scoreboard) {
    RankManager rankManager = RankManager.getInstance();
    Rank currentRank = rankManager.getPlayerRank(player);
    UUID playerId = player.getUniqueId();

    Rank previousRank = SCOREBOARD_RANKS.get(playerId);
    if (previousRank != currentRank) {
      if (previousRank != null) {
        String previousTeamName = getSortedRankName(previousRank);
        Team previousTeam = scoreboard.getHandle().getTeam(previousTeamName);
        if (previousTeam != null) {
          previousTeam.removeEntry(player.getName());
        }
      }
    }

    String currentTeamName = getSortedRankName(currentRank);
    Team currentTeam = scoreboard.getHandle().getTeam(currentTeamName);
    if (currentTeam != null) {
      currentTeam.addEntry(player.getName());
    }

    SCOREBOARD_RANKS.put(playerId, currentRank);
  }

  private static String getSortedRankName(Rank rank) {
    Rank[] invertedRanks = getSortedRanks();
    int index = getSortedRankIndex(rank);
    int maxDigits = String.valueOf(invertedRanks.length - 1).length();
    String paddedIndex = String.format("%0" + maxDigits + "d", index);

    return paddedIndex + rank.name();
  }

  private static Rank[] getSortedRanks() {
    Rank[] ranks = Rank.values();
    Rank[] invertedRanks = new Rank[ranks.length];
    for (int i = 0; i < ranks.length; ++i) {
      invertedRanks[i] = ranks[ranks.length - 1 - i];
    }

    return invertedRanks;
  }

  private static int getSortedRankIndex(Rank rank) {
    Rank[] invertedRanks = getSortedRanks();
    for (int i = 0; i < invertedRanks.length; ++i) {
      if (invertedRanks[i] == rank) {
        return i;
      }
    }

    return -1;
  }
}
