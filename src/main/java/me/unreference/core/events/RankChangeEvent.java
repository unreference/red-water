package me.unreference.core.events;

import me.unreference.core.models.Rank;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class RankChangeEvent extends Event {
  private static final HandlerList HANDLER_LIST = new HandlerList();
  private final Player RANK_CHANGE_PLAYER;
  private final Rank RANK_CHANGE_NEW_RANK;

  public RankChangeEvent(Player player, Rank newRank) {
    this.RANK_CHANGE_PLAYER = player;
    this.RANK_CHANGE_NEW_RANK = newRank;
  }

  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return HANDLER_LIST;
  }

  public Player getPlayer() {
    return RANK_CHANGE_PLAYER;
  }

  public Rank getNewRank() {
    return RANK_CHANGE_NEW_RANK;
  }
}
