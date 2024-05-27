package me.unreference.core.managers;

import io.papermc.paper.event.player.AsyncChatEvent;
import me.unreference.core.models.Rank;
import me.unreference.core.utils.FormatUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatManager implements Listener {
  private static final String PERMISSION_CHAT_FORMAT_LEGACY = "chat.format-legacy";
  private static final String PERMISSION_CHAT_FORMAT_HEX = "chat.format-hex";

  public ChatManager() {
    Rank.ADMIN.grantPermission(PERMISSION_CHAT_FORMAT_LEGACY, true);
    Rank.ADMIN.grantPermission(PERMISSION_CHAT_FORMAT_HEX, true);
  }

  @EventHandler
  private static void onAsyncChat(AsyncChatEvent event) {
    event.setCancelled(true);

    Player player = event.getPlayer();
    RankManager rankManager = RankManager.getInstance();
    Rank rank = rankManager.getPlayerRank(player);

    Component formattedMessage = getFormattedMessage(event.message(), rank);

    Component finalMessage = Component.text()
      .append(rank.getPrefixFormatting())
      .append(player.displayName().colorIfAbsent(rank.getPlayerNameColor()))
      .append(Component.text(" "))
      .append(formattedMessage)
      .build();

    Bukkit.broadcast(finalMessage);
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
}
