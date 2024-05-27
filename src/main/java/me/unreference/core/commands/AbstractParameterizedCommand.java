package me.unreference.core.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class AbstractParameterizedCommand extends AbstractCommand {
  private final Map<String, Command> SUBCOMMANDS = new HashMap<>();
  private final boolean IS_PLAYER_REQUIRED;

  public AbstractParameterizedCommand(String name, String prefix, String permission, boolean isPlayerRequired, String... aliases) {
    super(name, prefix, permission, aliases);
    this.IS_PLAYER_REQUIRED = isPlayerRequired;

    generatePermissions();
  }

  public AbstractParameterizedCommand(String name, String prefix, String permission, String... aliases) {
    super(name, prefix, permission, aliases);
    this.IS_PLAYER_REQUIRED = false;

    generatePermissions();
  }

  @Override
  public void trigger(CommandSender sender, String[] args) {
    if (IS_PLAYER_REQUIRED) {
      if (args.length > 1) {
        String action = args[1].toLowerCase();
        Command subcommand = SUBCOMMANDS.get(action);

        if (subcommand != null) {
          subcommand.setAliasUsed(action);
          subcommand.setMainAliasUsed(getAliasUsed());
          subcommand.trigger(sender, Arrays.copyOfRange(args, 0, args.length));
          return;
        }
      }
    } else {
      if (args.length > 0) {
        String action;
        action = args[0].toLowerCase();
        Command subcommand = SUBCOMMANDS.get(action);

        if (subcommand != null) {
          subcommand.setAliasUsed(action);
          subcommand.setMainAliasUsed(getAliasUsed());
          subcommand.trigger(sender, Arrays.copyOfRange(args, 0, args.length));
          return;
        }
      }
    }

    execute(sender, args);
  }

  @Override
  public List<String> tab(CommandSender sender, String alias, String[] args) {
    List<String> suggestions = new ArrayList<>();

    if (IS_PLAYER_REQUIRED) {
      if (args.length == 1) {
        String currentArg = args[0];
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
          suggestions.add(onlinePlayer.getName());
        }

        suggestions.removeIf(suggestion -> !suggestion.toLowerCase().startsWith(currentArg.toLowerCase()));
        return suggestions;

      } else if (args.length == 2) {
        suggestions.addAll(SUBCOMMANDS.keySet());
        String currentArg = args[1];
        suggestions.removeIf(suggestion -> !suggestion.toLowerCase().startsWith(currentArg.toLowerCase()));
        return suggestions;

      } else if (args.length > 2) {
        Command subcommand = SUBCOMMANDS.get(args[1].toLowerCase());
        if (subcommand != null) {
          return subcommand.tab(sender, alias, Arrays.copyOfRange(args, 2, args.length));
        }
      }
    } else {
      if (args.length == 1) {
        suggestions.addAll(SUBCOMMANDS.keySet());
        String currentArg = args[0];
        suggestions.removeIf(suggestion -> !suggestion.toLowerCase().startsWith(currentArg.toLowerCase()));
        return suggestions;

      } else if (args.length > 1) {
        Command subcommand = SUBCOMMANDS.get(args[0].toLowerCase());
        if (subcommand != null) {
          return subcommand.tab(sender, alias, Arrays.copyOfRange(args, 1, args.length));
        }
      }
    }

    return List.of();
  }

  @Override
  public boolean execute(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
    setAliasUsed(alias);
    trigger(sender, args);
    return true;
  }

  @Override
  public @NotNull List<String> tabComplete(@NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
    return tab(sender, alias, args);
  }

  protected void addSubcommand(Command subcommand) {
    SUBCOMMANDS.put(subcommand.getName().toLowerCase(), subcommand);
    for (String alias : subcommand.getAliases()) {
      SUBCOMMANDS.put(alias.toLowerCase(), subcommand);
    }
  }

  protected abstract void execute(CommandSender sender, String[] args);
}

