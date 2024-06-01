package me.unreference.core.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MessageUtil {

  public static Component getPrefixedMessage(String prefix, String message, Object... args) {
    Component bodyPrefix = FormatUtil.getFormattedComponent(prefix, args);
    Component body = FormatUtil.getFormattedComponent(message, args);
    TextComponent.Builder builder = Component.text();

    builder.append(bodyPrefix.colorIfAbsent(NamedTextColor.BLUE));
    builder.appendSpace();
    builder.append(body.colorIfAbsent(NamedTextColor.GRAY));
    return builder.build();
  }

  public static void broadcastMessage(Component component) {
    for (Player player : Bukkit.getOnlinePlayers()) {
      player.sendMessage(component);
    }
  }

  public static Component getMessage(String message, Object... args) {
    Component body = FormatUtil.getFormattedComponent(message, args);
    TextComponent.Builder builder = Component.text();

    builder.append(body).colorIfAbsent(NamedTextColor.WHITE);
    return builder.build();
  }
}
