package me.unreference.core.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ServerTickEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private static Integer ticks = 0;

    public ServerTickEvent() {
        ticks += 1;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public Integer getTicks() {
        return ticks;
    }
}
