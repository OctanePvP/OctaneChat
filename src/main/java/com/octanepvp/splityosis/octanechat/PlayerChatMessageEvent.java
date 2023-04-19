package com.octanepvp.splityosis.octanechat;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class PlayerChatMessageEvent extends Event implements Cancellable {

    private String rawMessage;
    private List<BaseComponent> message;

    private boolean isCancelled = false;

    public PlayerChatMessageEvent(boolean isAsync, String rawMessage, List<BaseComponent> message) {
        super(isAsync);
        this.rawMessage = rawMessage;
        this.message = message;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public String getRawMessage() {
        return rawMessage;
    }

    public List<BaseComponent> getMessage() {
        return message;
    }

    public void setMessage(List<BaseComponent> message) {
        this.message = message;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
}
