package me.unreference.core.commands.scheduler;

import me.unreference.core.Core;
import me.unreference.core.utils.FormatUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ChatDelayTask extends BukkitRunnable {
  private final Player PLAYER;
  private final int INITIAL_COUNTDOWN;

  private int countdown;

  public ChatDelayTask(Player player, int countdown) {
    this.PLAYER = player;
    this.INITIAL_COUNTDOWN = countdown;
    this.countdown = countdown;
  }

  @Override
  public void run() {
    if (countdown <= 0) {
      PLAYER.sendActionBar(Component.empty());
      return;
    }

    String formattedTime = FormatUtil.getFormattedTimeFromSeconds(countdown);
    PLAYER.sendActionBar(FormatUtil.getFormattedComponent(
      "&7You can chat in &e%s&7.", formattedTime));
    countdown -= 1;
  }

  public void start() {
    runTaskTimer(Core.getPlugin(), 0, 20);
  }

  public void update(int countdown) {
    this.countdown = countdown;
  }
}
