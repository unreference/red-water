package me.unreference.core.managers;

import me.unreference.core.Core;
import me.unreference.core.commands.RankCommand;
import me.unreference.core.models.Rank;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;

import java.util.Collection;
import java.util.HashSet;

public class CommandManager implements Listener {
    private static final String BYPASS_BLOCKED_COMMANDS = "command.bypass-blocked-commands";

    public CommandManager() {
        Rank.ADMIN.grantPermission(BYPASS_BLOCKED_COMMANDS, true);

        addCommand(new RankCommand());
    }

    @EventHandler
    private static void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        String[] commandParts = message.split("\\s+");
        String commandName = commandParts[0].substring(1);
        Player player = event.getPlayer();

        if (!getAllowedCommands(player).contains(commandName)) {
            event.setCancelled(true);

            // StringBuilder fakeCommand = new StringBuilder(commandParts[0].charAt(0) + commandParts[0]);

            // for (int i = 1; i < commandParts.length; ++i) {
            //     fakeCommand.append(' ').append(commandParts[i]);
            // }

            // event.setMessage(fakeCommand.toString());
        }
    }

    @EventHandler
    private static void onPlayerCommandSend(PlayerCommandSendEvent event) {
        Player player = event.getPlayer();
        event.getCommands().clear();
        event.getCommands().addAll(getAllowedCommands(player));
    }

    private static Collection<String> getAllowedCommands(Player player) {
        RankManager rankManager = RankManager.getInstance();
        Rank rank = rankManager.getPlayerRank(player);
        CommandMap commandMap = Bukkit.getCommandMap();

        Collection<String> allowedCommands = new HashSet<>();
        boolean hasBypassPermission = rank.isPermitted(BYPASS_BLOCKED_COMMANDS);

        for (Command command : commandMap.getKnownCommands().values()) {
            String commandName = command.getName();
            String commandPermission = command.getPermission();

            // Allow command if player is an operator or if they have specific command permission
            if (player.isOp() || rank.isPermitted("command." + commandName)) {
                if (commandPermission != null) {
                    player.addAttachment(Core.getPlugin(), commandPermission, true);
                    allowedCommands.add(commandName);
                }
            }
            // Allow internal commands if player has bypass permission
            else if (hasBypassPermission && (commandPermission == null || !commandPermission.startsWith("command."))) {
                if (commandPermission != null) {
                    player.addAttachment(Core.getPlugin(), commandPermission, true);
                    allowedCommands.add(commandName);
                }
            }

            // Check aliases
            for (String alias : command.getAliases()) {
                if (player.isOp() || rank.isPermitted("command." + commandName)) {
                    allowedCommands.add(alias);
                } else if (hasBypassPermission && (commandPermission == null || !commandPermission.startsWith("command."))) {
                    if (commandPermission != null) {
                        player.addAttachment(Core.getPlugin(), commandPermission, true);
                        allowedCommands.add(alias);
                    }
                }
            }
        }

        return allowedCommands;
    }

    private static void addCommand(Command command) {
        Bukkit.getServer().getCommandMap().register(command.getName(), command);
    }
}
