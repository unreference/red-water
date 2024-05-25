package me.unreference.core.commands;

import me.unreference.core.models.Rank;
import me.unreference.core.utils.MessageUtil;
import org.bukkit.command.CommandSender;

public class RankCommand extends AbstractParameterCommand {

    public RankCommand() {
        super("rank", "Rank>", "command.rank", true);

        addSubcommand(new RankSetCommand());
        addSubcommand(new RankResetCommand());
    }

    @Override
    protected void execute(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sendUsageMessage(sender);
        }
    }

    @Override
    protected void sendUsageMessage(CommandSender sender) {
        sender.sendMessage(MessageUtil.getPrefixedMessage(
                getPrefix(), "/%s <player> (set|reset) [<rank>]", getAliasUsed()));
    }

    @Override
    protected void generatePermissions() {
        Rank.ADMIN.grantPermission(getPermission(), true);
    }
}
