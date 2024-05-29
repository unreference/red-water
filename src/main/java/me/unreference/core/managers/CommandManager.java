package me.unreference.core.managers;

import com.google.common.collect.Lists;
import me.unreference.core.commands.ChatCommand;
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

import java.util.*;

public class CommandManager implements Listener {
  private static final String PERMISSION_BYPASS_BLOCKED_COMMANDS = "command.bypass-blocked-commands";
  private static final List<String> BLOCKED_COMMANDS = Lists.newArrayList("plugins", "version");

  public CommandManager() {
    Rank.DEV.grantPermission(PERMISSION_BYPASS_BLOCKED_COMMANDS, true);

    addCommand(new RankCommand());
    addCommand(new ChatCommand());
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

  private static Collection<String> getAllowedCommands(Player player) {
    RankManager rankManager = RankManager.getInstance();
    Rank rank = rankManager.getPlayerRank(player);
    CommandMap commandMap = Bukkit.getCommandMap();

    Collection<String> allowedCommands = new HashSet<>();

    if (player.isOp()) {
      for (Command command : commandMap.getKnownCommands().values()) {
        allowedCommands.add(command.getName());
        allowedCommands.addAll(command.getAliases());
      }
    } else {
      for (Command command : commandMap.getKnownCommands().values()) {
        if (rank.isPermitted(command.getPermission())) {
          allowedCommands.add(command.getName());
          allowedCommands.addAll(command.getAliases());
        }

        if (rank.isPermitted(PERMISSION_BYPASS_BLOCKED_COMMANDS)) {
          allowedCommands.addAll(getBlockedCommands());
        }
      }
    }

    return allowedCommands;
  }

  private static List<String> getBlockedCommands() {
    Set<String> blockedCommands = new HashSet<>(BLOCKED_COMMANDS);
    CommandMap commandMap = Bukkit.getCommandMap();
    for (Command command : commandMap.getKnownCommands().values()) {
      if (blockedCommands.contains(command.getName())) {
        blockedCommands.addAll(command.getAliases());
      }
    }

    return new ArrayList<>(blockedCommands);
  }

  private static void addCommand(Command command) {
    Bukkit.getServer().getCommandMap().register(command.getName(), command);
  }
}


