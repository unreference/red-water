package me.unreference.core.commands;

import me.unreference.core.managers.RankManager;
import me.unreference.core.models.Rank;
import me.unreference.core.utils.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RankSetCommand extends AbstractCommand {

    public RankSetCommand() {
        super("set", "Rank>", null, "update");
    }

    @Override
    public void trigger(CommandSender sender, String[] args) {
        if (args.length != 3) {
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
        Rank rank = rankManager.getRankFromId(args[2]);

        if (rank == null) {
            sender.sendMessage(
                    MessageUtil.getPrefixedMessage(
                            getPrefix(),
                            "Rank not found: &e%s", args[2]));
            return;
        }

        rankManager.setPlayerRank(player, rank);
        player.sendMessage(
                MessageUtil.getPrefixedMessage(
                        getPrefix(),
                        "Your rank has been updated to &e%s&7.", rank.name()));
        sender.sendMessage(
                MessageUtil.getPrefixedMessage(
                        getPrefix(),
                        "Updated &e%s&7's rank to &e%s&7.", player.getName(), rank.name()));
    }

    @Override
    public List<String> tab(CommandSender sender, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            for (Rank rank : Rank.values()) {
                suggestions.add(rank.getId().toUpperCase());
            }
        }

        String currentArg = args[args.length - 1];
        suggestions.removeIf(suggestion -> !suggestion.toLowerCase().startsWith(currentArg.toLowerCase()));
        return suggestions;
    }

    @Override
    protected Component getUsageMessage() {
        return MessageUtil.getPrefixedMessage(
                getPrefix(),
                "/%s <player> %s <rank>", getMainAliasUsed(), getAliasUsed());
    }

    @Override
    protected void generatePermissions() {
    }
}
