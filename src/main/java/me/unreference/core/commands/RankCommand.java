package me.unreference.core.commands;

import me.unreference.core.managers.RankManager;
import me.unreference.core.models.Rank;
import me.unreference.core.utils.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankCommand extends AbstractParameterizedCommand {

  public RankCommand() {
    super("rank", "Rank>", "command.rank", true);

    addSubcommand(new RankSetCommand());
    addSubcommand(new RankResetCommand());
  }

  @Override
  protected void execute(CommandSender sender, String[] args) {
    if (args.length < 1 || args.length > 2) {
      sender.sendMessage(getUsageMessage());
      return;
    }

    Player player = Bukkit.getPlayer(args[0]);
    if (player == null) {
      sender.sendMessage(
        MessageUtil.getPrefixedMessage(
          getPrefix(),
          "Player not found: &e%s", args[0]));
      return;
    }

    RankManager rankManager = RankManager.getInstance();
    Rank rank = rankManager.getPlayerRank(player);
    sender.sendMessage(
      MessageUtil.getPrefixedMessage(
        getPrefix(),
        "&e%s&7's rank: &e%s", player.getName(), rank.getId().toUpperCase()));
  }

  @Override
  protected Component getUsageMessage() {
    return MessageUtil.getPrefixedMessage(
      getPrefix(),
      "/%s <player> [set|reset] [<rank>]", getAliasUsed());
  }

  @Override
  protected void generatePermissions() {
    Rank.ADMIN.grantPermission(getPermission(), true);
  }
}
