package me.unreference.core.commands;

import me.unreference.core.managers.RankManager;
import me.unreference.core.models.Rank;
import me.unreference.core.utils.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatCommand extends AbstractParameterizedCommand {

  public ChatCommand() {
    super("chat", "Chat>", "command.chat", false, "ch");

    addSubcommand(new ChatDelayCommand());
  }

  @Override
  protected void execute(CommandSender sender, String[] args) {
    if (args.length < 1 || args.length > 2) {
      Player player = (Player) sender;
      RankManager rankManager = RankManager.getInstance();
      Rank rank = rankManager.getPlayerRank(player);
      sender.sendMessage(getPermissionUsageMessage(rank));
    }
  }

  @Override
  protected Component getUsageMessage() {
    return Component.empty();
  }

  @Override
  protected void generatePermissions() {
    Rank.SM.grantPermission(getPermission(), true);
  }

  private Component getPermissionUsageMessage(Rank rank) {
    Component message = Component.empty();

    if (rank == Rank.SM) {
      return message.append(MessageUtil.getPrefixedMessage(getPrefix(),
        "/%s delay [<duration>]", getAliasUsed()));
    } else {
      return message.append(MessageUtil.getPrefixedMessage(getPrefix(),
        "/%s (delay|lock) [<duration>]", getAliasUsed()));
    }
  }
}
