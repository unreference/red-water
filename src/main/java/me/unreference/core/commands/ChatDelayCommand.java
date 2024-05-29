package me.unreference.core.commands;

import me.unreference.core.events.ChatDelayEvent;
import me.unreference.core.managers.ChatManager;
import me.unreference.core.models.Rank;
import me.unreference.core.utils.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.List;

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
        sender.sendMessage(MessageUtil.getPrefixedMessage(getPrefix(),
          "The chat does not currently have a delay."));
      } else {
        IS_CHAT_DELAYED = false;
        ChatManager.removeChatDelay();
        sender.sendMessage(MessageUtil.getPrefixedMessage(getPrefix(),
          "Removed the chat delay."));
      }
    } else {
      try {
        if (IS_CHAT_DELAYED) {
          IS_CHAT_DELAYED = false;
          ChatManager.removeChatDelay();
        }

        int duration = Integer.parseInt(args[0]);

        duration = duration < 0 ? duration * -1 : duration;
        if (duration == 0) {
          IS_CHAT_DELAYED = false;
          ChatManager.removeChatDelay();
          sender.sendMessage(MessageUtil.getPrefixedMessage(getPrefix(),
            "Removed the chat delay."));
          return;
        }

        sender.sendMessage(MessageUtil.getPrefixedMessage(getPrefix(),
          "Added a chat delay of &e%d &7%s.", duration, (duration > 1 ? "seconds" : "second")));
        Bukkit.getServer().getPluginManager().callEvent(new ChatDelayEvent(duration));
        IS_CHAT_DELAYED = true;
      } catch (NumberFormatException exception) {
        sender.sendMessage(getUsageMessage());
      }
    }
  }

  @Override
  public List<String> tab(CommandSender sender, String alias, String[] args) {
    return List.of();
  }

  @Override
  protected Component getUsageMessage() {
    return MessageUtil.getPrefixedMessage(getPrefix(),
      "/%s %s [<duration>]", getMainAliasUsed(), getAliasUsed());
  }

  @Override
  protected void generatePermissions() {
    Rank.SM.grantPermission(getPermission(), true);
  }
}
