package me.unreference.core.commands;

import java.util.List;
import me.unreference.core.events.ChatDelayEvent;
import me.unreference.core.managers.ChatManager;
import me.unreference.core.models.Rank;
import me.unreference.core.utils.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class ChatDelayCommand extends AbstractCommand {
  private static boolean IS_CHAT_DELAYED = false;

  public ChatDelayCommand() {
    super("delay", "Chat>", "command.chat.delay", "slow");
  }

  @Override
  public void trigger(CommandSender sender, String[] args) {
    if (args.length > 1) {
      sender.sendMessage(getUsageMessage());
      return;
    }

    if (args.length == 0) {
      if (!IS_CHAT_DELAYED) {
        sender.sendMessage(
            MessageUtil.getPrefixedMessage(getPrefix(), "Delay mode is not currently enabled."));
      } else {
        disableChatDelay(sender);
      }
      return;
    }

    try {
      int duration = Math.abs(Integer.parseInt(args[0]));

      if (duration == 0) {
        if (IS_CHAT_DELAYED) {
          disableChatDelay(sender);
        } else {
          sender.sendMessage(
              MessageUtil.getPrefixedMessage(getPrefix(), "Delay mode is not currently enabled."));
        }
        return;
      }

      if (IS_CHAT_DELAYED) {
        ChatManager.removeChatDelay();
      }

      IS_CHAT_DELAYED = true;
      sender.sendMessage(
          MessageUtil.getPrefixedMessage(
              getPrefix(),
              "Delay mode is now &eenabled&7. Players can send one message every &e%d %s&7.",
              duration,
              (duration > 1 ? "seconds" : "second")));

      Bukkit.getServer().getPluginManager().callEvent(new ChatDelayEvent(duration));

    } catch (NumberFormatException exception) {
      sender.sendMessage(getUsageMessage());
    }
  }

  private void disableChatDelay(CommandSender sender) {
    IS_CHAT_DELAYED = false;
    ChatManager.removeChatDelay();
    sender.sendMessage(
        MessageUtil.getPrefixedMessage(getPrefix(), "Delay mode is now &edisabled&7."));
  }

  @Override
  public List<String> tab(CommandSender sender, String alias, String[] args) {
    return List.of();
  }

  @Override
  protected Component getUsageMessage() {
    return MessageUtil.getPrefixedMessage(
        getPrefix(), "/%s %s [<seconds>]", getMainAliasUsed(), getAliasUsed());
  }

  @Override
  protected void generatePermissions() {
    Rank.SM.grantPermission(getPermission(), true);
  }
}
