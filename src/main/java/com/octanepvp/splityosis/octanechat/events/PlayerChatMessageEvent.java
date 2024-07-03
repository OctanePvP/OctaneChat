package com.octanepvp.splityosis.octanechat.events;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

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


    public List<BaseComponent> replaceComponents(List<BaseComponent> baseComponents, String str, BaseComponent[] replacement) {
        List<BaseComponent> newComponents = new ArrayList<>();

        for (int i = 0; i < baseComponents.size(); i++) {

            BaseComponent temp = baseComponents.get(i);

            if(!temp.toPlainText().contains(str)){
                newComponents.add(temp);
                continue;
            }
            // Does contain
            String[] string = temp.toPlainText().split(Pattern.quote(str), 2);
            BaseComponent before = mapComponent(string[0], temp);

            newComponents.add(before);
            newComponents.addAll(Arrays.asList(replacement));

            BaseComponent after;
            if(string.length == 2){
                after = mapComponent(string[1], temp);
                newComponents.add(after);
            }
        }
        return newComponents;
    }

    public BaseComponent mapComponent(String str, BaseComponent component){
        TextComponent comp =  new TextComponent(str);
        comp.setHoverEvent(component.getHoverEvent());
        comp.setClickEvent(component.getClickEvent());
        comp.setColor(component.getColor());
        return comp;
    }
}
