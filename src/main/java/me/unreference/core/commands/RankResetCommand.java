package me.unreference.core.commands;

import me.unreference.core.managers.RankManager;
import me.unreference.core.models.Rank;
import me.unreference.core.utils.MessageUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class RankResetCommand extends AbstractCommand {

    public RankResetCommand() {
        super("reset", "Rank", "command.rank.reset", "clear");
    }

    @Override
    public void trigger(CommandSender sender, String[] args) {
        sender.sendMessage(args);
        sender.sendMessage("" + args.length);

        if (args.length != 2) {
            sendUsageMessage(sender);
            return;
        }


        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(
                    MessageUtil.getPrefixedMessage(
                            getPrefix(),
                            "Player not found: &e%s&7.", args[0]));
            return;
        }

        RankManager rankManager = RankManager.getInstance();
        rankManager.setPlayerRank(player, Rank.PLAYER);
        player.sendMessage(
                MessageUtil.getPrefixedMessage(
                        getPrefix(),
                        "Your rank has been reset."));
        sender.sendMessage(
                MessageUtil.getPrefixedMessage(
                        getPrefix(),
                        "Reset &e%s&7's rank.", player.getName()));
    }

    @Override
    public List<String> tab(CommandSender sender, String alias, String[] args) {
        return List.of();
    }

    @Override
    protected void sendUsageMessage(CommandSender sender) {
        sender.sendMessage(
                MessageUtil.getPrefixedMessage(
                        getPrefix(),
                        "/%s <player> %s", getMainAliasUsed(), getAliasUsed()));
    }

    @Override
    protected void generatePermissions() {
        Rank.ADMIN.grantPermission(getPermission(), true);
    }
}
