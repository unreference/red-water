package me.unreference.core.scheduler;

import me.unreference.core.Core;
import me.unreference.core.utils.FormatUtil;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ChatDelayTask extends BukkitRunnable {
  private final Player PLAYER;

  private int countdown;

  public ChatDelayTask(Player player, int countdown) {
    this.PLAYER = player;
    this.countdown = countdown;
  }

  @Override
  public void run() {
    if (countdown <= 0) {
      return;
    }

    countdown -= 1;

    String formattedTime = FormatUtil.getFormattedTimeFromSeconds(countdown);
    PLAYER.sendActionBar(
        FormatUtil.getFormattedComponent("&7You can send a message in &e%s&7.", formattedTime));
  }

  public void start() {
    runTaskTimerAsynchronously(Core.getPlugin(), 0, 20);
  }

  public void update(int countdown) {
    this.countdown = countdown;
  }
}
