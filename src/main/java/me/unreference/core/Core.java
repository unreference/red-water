package me.unreference.core;

import me.unreference.core.managers.ChatManager;
import me.unreference.core.managers.PlayerManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class Core extends JavaPlugin {

    @Override
    public void onEnable() {

        registerManager(new PlayerManager());
        registerManager(new ChatManager());

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void registerManager(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }
}
