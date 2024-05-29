package me.unreference.core.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ChatDelayEvent extends Event {
  private static final HandlerList HANDLER_LIST = new HandlerList();
  private final int CHAT_DELAY_DURATION;

  public ChatDelayEvent(int duration) {
    this.CHAT_DELAY_DURATION = duration;
  }

  public static HandlerList getHandlerList() {
    return HANDLER_LIST;
  }

  @Override
  public @NotNull HandlerList getHandlers() {
    return HANDLER_LIST;
  }

  public int getDuration() {
    return CHAT_DELAY_DURATION;
  }
}
