package me.unreference.core.scheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.unreference.core.utils.FormatUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ChatDelayTask {
  private final Player PLAYER;
  private final Plugin PLUGIN;

  private int countdown;
  private ScheduledTask scheduledTask;

  public ChatDelayTask(Plugin plugin, Player player, int countdown) {
    this.PLUGIN = plugin;
    this.PLAYER = player;
    this.countdown = countdown;
  }

  public void start() {
    scheduledTask =
        Bukkit.getGlobalRegionScheduler().runAtFixedRate(PLUGIN, task -> run(), 1L, 20L);
  }

  private void run() {
    if (countdown <= 0) {
      return;
    }

    countdown -= 1;

    String formattedTime = FormatUtil.getFormattedTimeFromSeconds(countdown);
    PLAYER.sendActionBar(
        FormatUtil.getFormattedComponent(
            "&7Shh... You can send a message in &e%s&7.", formattedTime));
  }

  public void cancel() {
    if (scheduledTask != null && !scheduledTask.isCancelled()) {
      scheduledTask.cancel();
    }
  }

  public int getCountdown() {
    return countdown;
  }
}
