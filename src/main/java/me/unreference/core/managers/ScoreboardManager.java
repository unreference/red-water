package me.unreference.core.managers;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import me.unreference.core.Core;
import me.unreference.core.events.PlayerRankChangeEvent;
import me.unreference.core.models.Rank;
import me.unreference.core.utils.PacketUtil;
import me.unreference.core.utils.ServerUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.world.scores.PlayerTeam;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.scoreboard.CraftScoreboard;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

public class ScoreboardManager implements Listener {

  public ScoreboardManager() {
    Bukkit.getGlobalRegionScheduler()
        .runAtFixedRate(Core.getPlugin(), task -> update(), 20L, 1200L);
  }

  private static MutableComponent getMinecraftComponent(Component component) {
    String json = GsonComponentSerializer.gson().serialize(component);
    JsonElement jsonElement = JsonParser.parseString(json);
    return net.minecraft.network.chat.Component.Serializer.fromJson(
        jsonElement, ServerUtil.getRegistryAccess());
  }

  @EventHandler
  private static void onPlayerJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();

    RankManager rankManager = RankManager.getInstance();
    Rank rank = rankManager.getPlayerRank(player);

    setupPlayerTeam(player, rank);
    update();
  }

  @EventHandler
  private static void onRankChange(PlayerRankChangeEvent event) {
    Player player = event.getPlayer();
    Rank newRank = event.getNewRank();

    setupPlayerTeam(player, newRank);
    update();
  }

  @EventHandler
  private static void onPlayerQuit(PlayerQuitEvent event) {
    Player player = event.getPlayer();
    removePlayerTeam(player);
  }

  @EventHandler
  private static void onPluginDisable(PluginDisableEvent event) {
    reset();
  }

  private static void setupPlayerTeam(Player player, Rank rank) {
    String teamName = getSortedRankName(rank);
    Component prefix = rank.getPrefixFormatting();

    PlayerTeam team =
        new PlayerTeam(
            ((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle(),
            teamName);
    team.setPlayerPrefix(getMinecraftComponent(prefix));

    ClientboundSetPlayerTeamPacket createTeamPacket =
        ClientboundSetPlayerTeamPacket.createAddOrModifyPacket(team, true);
    ClientboundSetPlayerTeamPacket addPlayerPacket =
        ClientboundSetPlayerTeamPacket.createPlayerPacket(
            team, player.getName(), ClientboundSetPlayerTeamPacket.Action.ADD);

    PacketUtil.broadcastPacket(createTeamPacket);
    PacketUtil.broadcastPacket(addPlayerPacket);
  }

  private static void removePlayerTeam(Player player) {
    RankManager rankManager = RankManager.getInstance();
    Rank rank = rankManager.getPlayerRank(player);
    String teamName = getSortedRankName(rank);

    PlayerTeam team =
        new PlayerTeam(
            ((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle(),
            teamName);
    ClientboundSetPlayerTeamPacket removePlayerPacket =
        ClientboundSetPlayerTeamPacket.createPlayerPacket(
            team, player.getName(), ClientboundSetPlayerTeamPacket.Action.REMOVE);

    PacketUtil.broadcastPacket(removePlayerPacket);
  }

  private static void update() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      RankManager rankManager = RankManager.getInstance();
      Rank rank = rankManager.getPlayerRank(player);

      setupPlayerTeam(player, rank);
    }
  }

  private static void reset() {
    for (Player player : Bukkit.getOnlinePlayers()) {
      removePlayerTeam(player);
    }
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
