package me.unreference.core;

import me.unreference.core.events.ServerTickEvent;
import me.unreference.core.managers.ChatManager;
import me.unreference.core.managers.CommandManager;
import me.unreference.core.managers.PlayerManager;
import me.unreference.core.managers.ScoreboardManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Core extends JavaPlugin {

    @Override
    public void onEnable() {

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            Bukkit.getServer().getPluginManager().callEvent(new ServerTickEvent());
        }, 0L, 1L);

        registerManager(new CommandManager());
        registerManager(new PlayerManager());
        registerManager(new ChatManager());
        registerManager(new ScoreboardManager());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerManager(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }
}
