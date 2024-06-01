package me.unreference.core;

import me.unreference.core.managers.*;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class Core extends JavaPlugin {

  public static Plugin getPlugin() {
    return Bukkit.getPluginManager().getPlugin("Core");
  }

  @Override
  public void onEnable() {

    addManager(new PlayerManager());
    addManager(new CommandManager());
    addManager(new ChatManager());
    addManager(new ScoreboardManager());
  }

  @Override
  public void onDisable() {
    // Plugin shutdown logic
  }

  private void addManager(Listener listener) {
    this.getServer().getPluginManager().registerEvents(listener, this);
  }
}
