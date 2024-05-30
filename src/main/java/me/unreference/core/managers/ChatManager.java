package me.unreference.core.managers;

import io.papermc.paper.event.player.AsyncChatEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.unreference.core.events.ChatDelayEvent;
import me.unreference.core.models.Rank;
import me.unreference.core.scheduler.ChatDelayTask;
import me.unreference.core.utils.FormatUtil;
import me.unreference.core.utils.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatManager implements Listener {
  private static final String PERMISSION_CHAT_DELAY_BYPASS = "chat.delay-bypass";
  private static final String PERMISSION_CHAT_FORMAT_LEGACY = "chat.format-legacy";
  private static final String PERMISSION_CHAT_FORMAT_HEX = "chat.format-hex";

  private static final HashMap<UUID, Long> PLAYER_LAST_MESSAGE_TIMES = new HashMap<>();
  private static final Map<UUID, ChatDelayTask> PLAYER_COUNTDOWN_TASKS = new HashMap<>();

  private static int chatDelay = 0;

  public ChatManager() {
    Rank.TRAINEE.grantPermission(PERMISSION_CHAT_DELAY_BYPASS, true);
    Rank.ADMIN.grantPermission(PERMISSION_CHAT_FORMAT_LEGACY, true);
    Rank.ADMIN.grantPermission(PERMISSION_CHAT_FORMAT_HEX, true);
  }

  @EventHandler
  private static void onChatDelay(ChatDelayEvent event) {
    chatDelay = event.getDuration();
  }

  private static Component getFormattedMessage(Component message, Rank rank) {
    boolean isLegacy = rank.isPermitted(PERMISSION_CHAT_FORMAT_LEGACY);
    boolean isHex = rank.isPermitted(PERMISSION_CHAT_FORMAT_HEX);

    if (!isLegacy && !isHex) {
      return message;
    }

    if (isLegacy && isHex) {
      return FormatUtil.getFormattedComponent(message);
    } else if (isLegacy) {
      return FormatUtil.getLegacyFormattedComponent(message);
    } else {
      return FormatUtil.getHexFormattedComponent(message);
    }
  }

  private static boolean isChatAllowed(Player player) {
    RankManager rankManager = RankManager.getInstance();

    Rank rank = rankManager.getPlayerRank(player);
    if (rank.isPermitted(PERMISSION_CHAT_DELAY_BYPASS)) {
      return true;
    }

    UUID playerUuid = player.getUniqueId();
    long currentTime = System.currentTimeMillis();

    if (!PLAYER_LAST_MESSAGE_TIMES.containsKey(playerUuid)) {
      return true;
    }

    long lastTime = PLAYER_LAST_MESSAGE_TIMES.get(playerUuid);
    return (currentTime - lastTime) >= chatDelay * 1000L;
  }

  private static long getTimeLeft(Player player) {
    UUID playerUUID = player.getUniqueId();
    long currentTime = System.currentTimeMillis();

    if (!PLAYER_LAST_MESSAGE_TIMES.containsKey(playerUUID)) {
      return 0;
    }

    long lastTime = PLAYER_LAST_MESSAGE_TIMES.get(playerUUID);
    long timeElapsed = currentTime - lastTime;
    long timeLeft = chatDelay * 1000L - timeElapsed;

    return timeLeft > 0 ? timeLeft : 0;
  }

  public static void removeChatDelay() {
    PLAYER_LAST_MESSAGE_TIMES.clear();
    PLAYER_COUNTDOWN_TASKS.values().forEach(ChatDelayTask::cancel);
    PLAYER_COUNTDOWN_TASKS.clear();
    chatDelay = 0;
  }

  @EventHandler
  private void onAsyncChat(AsyncChatEvent event) {
    event.setCancelled(true);

    Player player = event.getPlayer();
    if (!isChatAllowed(player)) {
      long timeLeft = getTimeLeft(player);
      if (timeLeft > 0) {
        startOrUpdateDelayCountdown(player, (int) (timeLeft / 1000));
        player.sendMessage(
            MessageUtil.getPrefixedMessage(
                "Chat>",
                "Delay mode is &eenabled&7. You can send one message every &e%d %s&7.",
                chatDelay,
                (chatDelay > 1 ? "seconds" : "second")));
      }

      return;
    }

    RankManager rankManager = RankManager.getInstance();
    Rank rank = rankManager.getPlayerRank(player);
    Component formattedMessage = getFormattedMessage(event.message(), rank);

    Component finalMessage =
        Component.text()
            .append(rank.getPrefixFormatting())
            .append(player.displayName().colorIfAbsent(rank.getPlayerNameColor()))
            .appendSpace()
            .append(formattedMessage)
            .build();

    Bukkit.broadcast(finalMessage);
    PLAYER_LAST_MESSAGE_TIMES.put(player.getUniqueId(), System.currentTimeMillis());
  }

  private void startOrUpdateDelayCountdown(Player player, int delay) {
    UUID playerUuid = player.getUniqueId();

    ChatDelayTask existingTask = PLAYER_COUNTDOWN_TASKS.get(playerUuid);
    if (existingTask != null) {
      existingTask.update(delay);
    } else {
      ChatDelayTask newTask = new ChatDelayTask(player, delay);
      newTask.start();
      PLAYER_COUNTDOWN_TASKS.put(playerUuid, newTask);
    }
  }
}
