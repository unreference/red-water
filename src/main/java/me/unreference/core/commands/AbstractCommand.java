package me.unreference.core.commands;

import java.util.Arrays;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCommand extends Command
    implements me.unreference.core.commands.Command {
  private final String COMMAND_NAME;
  private final String COMMAND_PREFIX;
  private final String COMMAND_PERMISSION;
  private final List<String> COMMAND_ALIASES;
  protected String aliasUsed;
  protected String mainAliasUsed;

  public AbstractCommand(String name, String prefix, String permission, String... aliases) {
    super(name);

    this.COMMAND_NAME = name;
    this.COMMAND_PREFIX = prefix;
    this.COMMAND_PERMISSION = permission;
    this.COMMAND_ALIASES = Arrays.asList(aliases);

    generatePermissions();
  }

  @Override
  public abstract void trigger(CommandSender sender, String[] args);

  @Override
  public @NotNull List<String> tabComplete(
      @NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
    return tab(sender, alias, args);
  }

  @Override
  public abstract List<String> tab(CommandSender sender, String alias, String[] args);

  @Override
  public boolean execute(
      @NotNull CommandSender sender, @NotNull String alias, @NotNull String[] args) {
    setAliasUsed(alias);
    trigger(sender, args);
    return true;
  }

  public String getName() {
    return COMMAND_NAME;
  }

  public String getPrefix() {
    return COMMAND_PREFIX;
  }

  public String getPermission() {
    return COMMAND_PERMISSION;
  }

  public List<String> getAliases() {
    return COMMAND_ALIASES;
  }

  protected String getAliasUsed() {
    return aliasUsed;
  }

  @Override
  public void setAliasUsed(String alias) {
    aliasUsed = alias;
  }

  protected String getMainAliasUsed() {
    return mainAliasUsed;
  }

  @Override
  public void setMainAliasUsed(String alias) {
    mainAliasUsed = alias;
  }

  protected abstract Component getUsageMessage();

  protected abstract void generatePermissions();
}
