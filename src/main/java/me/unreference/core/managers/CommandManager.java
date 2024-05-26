package me.unreference.core.managers;

import me.unreference.core.Core;
import me.unreference.core.commands.RankCommand;
import me.unreference.core.events.RankChangeEvent;
import me.unreference.core.models.Rank;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerCommandSendEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.permissions.PermissionAttachment;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CommandManager implements Listener {
    private static final String PERMISSION_BYPASS_BLOCKED_COMMANDS = "command.bypass-blocked-commands";
    private static final Map<Player, PermissionAttachment> PERMISSION_ATTACHMENTS = new HashMap<>();

    public CommandManager() {
        Rank.ADMIN.grantPermission(PERMISSION_BYPASS_BLOCKED_COMMANDS, true);
        addCommand(new RankCommand());
    }

    @EventHandler
    private static void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        updatePermissions(player);
    }

    @EventHandler
    private static void onRankChange(RankChangeEvent event) {
        Player player = event.getPlayer();
        updatePermissions(player);
    }

    @EventHandler
    private static void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        String[] commandParts = message.split("\\s+");
        String commandName = commandParts[0].substring(1);
        Player player = event.getPlayer();

        if (!getAllowedCommands(player).contains(commandName)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private static void onPlayerCommandSend(PlayerCommandSendEvent event) {
        Player player = event.getPlayer();
        Collection<String> allowedCommands = getAllowedCommands(player);

        event.getCommands().clear();
        event.getCommands().addAll(allowedCommands);

        player.updateCommands();
    }

    private static void updatePermissions(Player player) {
        clearPermissions(player);

        PermissionAttachment attachment = player.addAttachment(Core.getPlugin());
        PERMISSION_ATTACHMENTS.put(player, attachment);

        if (RankManager.getInstance().getPlayerRank(player).isPermitted(PERMISSION_BYPASS_BLOCKED_COMMANDS)) {
            for (String command : getBlockedCommands()) {
                String permission = determinePermissionNode(command);
                attachment.setPermission(permission, true);
            }
        }
    }

    private static void clearPermissions(Player player) {
        PermissionAttachment attachment = PERMISSION_ATTACHMENTS.get(player);
        if (attachment != null) {
            attachment.remove();
        }
    }

    private static Collection<String> getAllowedCommands(Player player) {
        RankManager rankManager = RankManager.getInstance();
        Rank rank = rankManager.getPlayerRank(player);
        CommandMap commandMap = Bukkit.getCommandMap();

        Collection<String> allowedCommands = new HashSet<>();
        boolean hasBypassPermission = rank.isPermitted(PERMISSION_BYPASS_BLOCKED_COMMANDS);

        for (Command command : commandMap.getKnownCommands().values()) {
            String commandName = command.getName();
            String commandPermission = command.getPermission();

            // Allow command if player is an operator or if they have specific command permission
            if (player.isOp() || rank.isPermitted(determinePermissionNode(commandName))) {
                allowedCommands.add(commandName);
            }
            // Allow Bukkit and Minecraft commands if player has bypass permission
            else if (hasBypassPermission && isBukkitOrMinecraftCommand(commandPermission)) {
                allowedCommands.add(commandName);
            }

            // Check aliases
            for (String alias : command.getAliases()) {
                if (player.isOp() || rank.isPermitted(determinePermissionNode(alias))) {
                    allowedCommands.add(alias);
                } else if (hasBypassPermission && isBukkitOrMinecraftCommand(commandPermission)) {
                    allowedCommands.add(alias);
                }
            }
        }

        return allowedCommands;
    }

    private static boolean isBukkitOrMinecraftCommand(String commandPermission) {
        return commandPermission != null && (commandPermission.startsWith("bukkit.command.") || commandPermission.startsWith("minecraft.command."));
    }

    private static Collection<String> getBlockedCommands() {
        CommandMap commandMap = Bukkit.getCommandMap();
        Collection<String> bypassableCommands = new HashSet<>();

        for (Command command : commandMap.getKnownCommands().values()) {
            String commandPermission = command.getPermission();
            if (isBukkitOrMinecraftCommand(commandPermission)) {
                bypassableCommands.add(command.getName());
            }
        }
        return bypassableCommands;
    }

    private static String determinePermissionNode(String commandName) {
        CommandMap commandMap = Bukkit.getCommandMap();
        Command command = commandMap.getCommand(commandName);
        if (command != null && command.getPermission() != null) {
            return command.getPermission();
        }

        return "command." + commandName;
    }

    private static void addCommand(Command command) {
        Bukkit.getServer().getCommandMap().register(command.getName(), command);
    }
}
