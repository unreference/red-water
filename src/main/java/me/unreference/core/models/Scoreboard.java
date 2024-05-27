package me.unreference.core.models;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Scoreboard {
  private final Player SCOREBOARD_OWNER;
  private final org.bukkit.scoreboard.Scoreboard SCOREBOARD;

  public Scoreboard(Player owner) {
    SCOREBOARD_OWNER = owner;
    SCOREBOARD = Bukkit.getScoreboardManager().getMainScoreboard();
  }

  public Player getOwner() {
    return SCOREBOARD_OWNER;
  }

  public org.bukkit.scoreboard.Scoreboard getHandle() {
    return SCOREBOARD;
  }
}
