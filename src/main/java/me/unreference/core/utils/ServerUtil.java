package me.unreference.core.utils;

import net.minecraft.core.HolderLookup;
import net.minecraft.server.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ServerUtil {

  public static HolderLookup.Provider getRegistryAccess() throws IllegalStateException {
    if (Bukkit.getOnlinePlayers().isEmpty()) {
      throw new IllegalStateException("No online players available to retrieve the server.");
    }

    Player player = Bukkit.getOnlinePlayers().iterator().next();
    if (player == null) {
      throw new IllegalStateException("Unable to retrieve an online player.");
    }

    MinecraftServer server = ((CraftPlayer) player).getHandle().getServer();
    if (server == null) {
      throw new IllegalStateException("Unable to retrieve the Minecraft server.");
    }

    return server.registryAccess();
  }
}
