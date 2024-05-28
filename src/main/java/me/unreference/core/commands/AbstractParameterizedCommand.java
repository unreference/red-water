package me.unreference.core.commands;

import me.unreference.core.managers.RankManager;
import me.unreference.core.models.Rank;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

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
      if (args.length == 0) {
        execute(sender, args);
        return;
      } else if (args.length == 1) {
        execute(sender, args);
        return;
      } else {
        String action = args[1];
        Command subcommand = SUBCOMMANDS.get(action);
        if (subcommand != null) {
          subcommand.setAliasUsed(action);
          subcommand.setMainAliasUsed(getAliasUsed());
          if (isPermitted(sender, subcommand.getPermission())) {
            subcommand.trigger(sender, Arrays.copyOfRange(args, 0, args.length));
            return;
          }
        }
      }
    } else {
      if (args.length == 0) {
        execute(sender, args);
        return;
      } else {
        String action = args[0];
        Command subcommand = SUBCOMMANDS.get(action);
        if (subcommand != null) {
          subcommand.setAliasUsed(action);
          subcommand.setMainAliasUsed(getAliasUsed());
          if (isPermitted(sender, subcommand.getPermission())) {
            subcommand.trigger(sender, Arrays.copyOfRange(args, 1, args.length));
            return;
          }
        }
      }
    }

    execute(sender, args);
  }

  @Override
  public List<String> tab(CommandSender sender, String alias, String[] args) {
    if (IS_PLAYER_REQUIRED) {
      switch (args.length) {
        case 1 -> {
          List<String> suggestions = new ArrayList<>(getOnlinePlayers());
          String currentArg = args[0];
          filterSuggestions(suggestions, currentArg);
          return suggestions;
        }
        case 2 -> {
          List<String> suggestions = new ArrayList<>(getPermittedSubcommands(sender));
          String currentArg = args[1];
          filterSuggestions(suggestions, currentArg);
          return suggestions;
        }
        default -> {
          Command subcommand = SUBCOMMANDS.get(args[1]);
          if (subcommand != null) {
            return subcommand.tab(sender, alias, Arrays.copyOfRange(args, 2, args.length));
          }
        }
      }
    } else {
      if (args.length == 1) {
        List<String> suggestions = new ArrayList<>(getPermittedSubcommands(sender));
        String currentArg = args[0];
        filterSuggestions(suggestions, currentArg);
        return suggestions;
      } else {
        Command subcommand = SUBCOMMANDS.get(args[0]);
        if (subcommand != null) {
          return subcommand.tab(sender, alias, Arrays.copyOfRange(args, 0, args.length));
        }
      }
    }

    return Collections.emptyList();
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

  private boolean isPermitted(CommandSender sender, String permission) {
    if (!(sender instanceof Player player)) {
      return true;
    }

    RankManager rankManager = RankManager.getInstance();
    Rank rank = rankManager.getPlayerRank(player);
    return rank.isPermitted(permission);
  }

  private List<String> getOnlinePlayers() {
    return Bukkit.getOnlinePlayers().stream()
      .map(Player::getName)
      .collect(Collectors.toList());
  }

  private void filterSuggestions(List<String> suggestions, String arg) {
    suggestions.removeIf(suggestion -> !suggestion.toLowerCase().startsWith(arg.toLowerCase()));
  }

  private List<String> getPermittedSubcommands(CommandSender sender) {
    return SUBCOMMANDS.entrySet().stream()
      .filter(entry -> isPermitted(sender, entry.getValue().getPermission()))
      .map(Map.Entry::getKey)
      .collect(Collectors.toList());
  }

  protected void addSubcommand(Command subcommand) {
    SUBCOMMANDS.put(subcommand.getName().toLowerCase(), subcommand);
    for (String alias : subcommand.getAliases()) {
      SUBCOMMANDS.put(alias.toLowerCase(), subcommand);
    }
  }

  protected abstract void execute(CommandSender sender, String[] args);
}

