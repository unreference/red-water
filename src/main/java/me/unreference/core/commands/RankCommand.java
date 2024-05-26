package me.unreference.core.commands;

import me.unreference.core.models.Rank;
import me.unreference.core.utils.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;

public class RankCommand extends AbstractParameterizedCommand {

    public RankCommand() {
        super("rank", "Rank>", "command.rank", true);

        addSubcommand(new RankSetCommand());
        addSubcommand(new RankResetCommand());
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(getUsageMessage());
        }
    }

    @Override
    protected Component getUsageMessage() {
        return MessageUtil.getPrefixedMessage(
                getPrefix(),
                "/%s <player> (set|reset) [<rank>]", getAliasUsed());
    }

    @Override
    protected void generatePermissions() {
        Rank.ADMIN.grantPermission(getPermission(), true);
    }
}
