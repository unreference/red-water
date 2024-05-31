package me.unreference.core.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.unreference.core.models.Rank;
import me.unreference.core.utils.MessageUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GiveCommand extends AbstractCommand {

  public GiveCommand() {
    super("give", "Give>", "command.give", "i");
  }

  @Override
  public void trigger(CommandSender sender, String[] args) {
    if (args.length < 2 || args.length > 3) {
      sender.sendMessage(getUsageMessage());
      return;
    }

    Player player = Bukkit.getPlayer(args[0]);
    if (player == null) {
      sender.sendMessage(
          MessageUtil.getPrefixedMessage(getPrefix(), "Player not found: &e%s", args[0]));
      return;
    }

    String itemName = args[1];
    Material material = Material.matchMaterial(itemName);
    if (material == null) {
      sender.sendMessage(
          MessageUtil.getPrefixedMessage(getPrefix(), "Item not found: &e%s", args[1]));
      return;
    }

    if (args.length == 3) {
      try {
        int amount = Integer.parseInt(args[2]);
        amount = Math.max(amount, 1);
        give(sender, player, material, amount);
      } catch (NumberFormatException exception) {
        sender.sendMessage(getUsageMessage());
      }
    } else {
      give(sender, player, material, 1);
    }
  }

  private void give(CommandSender sender, Player player, Material material, int amount) {
    ItemStack itemStack = new ItemStack(material, amount);
    player.getInventory().addItem(itemStack);

    player.sendMessage(
        MessageUtil.getPrefixedMessage(
            getPrefix(), "&e%d %s &7was added to your inventory.", amount, material));
    sender.sendMessage(
        MessageUtil.getPrefixedMessage(
            getPrefix(),
            "Added &e%d %s &7to &e%s&7's inventory.",
            amount,
            material,
            player.getName()));
  }

  @Override
  public List<String> tab(CommandSender sender, String alias, String[] args) {
    List<String> suggestions = new ArrayList<>();

    if (args.length == 1) {
      suggestions.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList());
    }

    if (args.length == 2) {
      suggestions.addAll(Arrays.stream(Material.values()).map(Material::name).toList());
    }

    String currentArg = args[args.length - 1];
    suggestions.removeIf(
        suggestion -> !suggestion.toLowerCase().startsWith(currentArg.toLowerCase()));
    return suggestions;
  }

  @Override
  protected Component getUsageMessage() {
    return MessageUtil.getPrefixedMessage(
        getPrefix(), "/%s <player> <item> [<amount>]", getAliasUsed());
  }

  @Override
  protected void generatePermissions() {
    Rank.ADMIN.grantPermission(getPermission(), true);
  }
}
