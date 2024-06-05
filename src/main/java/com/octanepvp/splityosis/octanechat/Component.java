package com.octanepvp.splityosis.octanechat;

import me.clip.placeholderapi.PlaceholderAPI;
import net.md_5.bungee.api.chat.*;
import org.bukkit.entity.Player;

import java.util.List;

public class Component {

    private String displayText;
    private List<String> hoverText;
    private ClickEvent.Action clickAction;
    private String clickValue;

    public Component(String displayText, List<String> hoverText, ClickEvent.Action clickAction, String clickValue) {
        this.displayText = displayText;
        this.hoverText = hoverText;
        this.clickAction = clickAction;
        this.clickValue = clickValue;
    }

    public Component setPlayerPlaceholders(Player player){
        displayText = PlaceholderAPI.setPlaceholders(player, displayText);
        if (hoverText != null)
            hoverText = PlaceholderAPI.setPlaceholders(player, hoverText);
        if (clickValue != null)
            clickValue = PlaceholderAPI.setPlaceholders(player, clickValue);
        return this;
    }

    public Component setRelationalPlaceholders(Player player, Player player2){
        displayText = PlaceholderAPI.setRelationalPlaceholders(player, player2, displayText);
        if (hoverText != null)
            hoverText = PlaceholderAPI.setRelationalPlaceholders(player, player2, hoverText);
        if (clickValue != null)
            clickValue = PlaceholderAPI.setRelationalPlaceholders(player, player2, clickValue);
        return this;
    }

    public Component replace(String replace, String with){
        displayText = displayText.replace(replace, with);
        return this;
    }

    public Component fixColors(){
        displayText = OctaneChat.translateAllColors(displayText);
        if (hoverText != null)
            hoverText = OctaneChat.translateAllColors(hoverText);
        if (clickValue != null)
            clickValue = OctaneChat.translateAllColors(clickValue);
        return this;
    }

    public BaseComponent[] compile(){
        BaseComponent[] components = TextComponent.fromLegacyText(displayText);

        for (int i = 0; i < components.length; i++) {
            if (hoverText != null) {
                ComponentBuilder builder = new ComponentBuilder();
                for (int j = 0; j < hoverText.size(); j++) {
                    if (j != 0) {
                        builder.append("\n", ComponentBuilder.FormatRetention.NONE);
                    }
                    builder.append(TextComponent.fromLegacyText(hoverText.get(j)), ComponentBuilder.FormatRetention.NONE);
                }
                components[i].setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, builder.create()));
            }

            if (clickAction != null && clickValue != null)
                components[i].setClickEvent(new ClickEvent(clickAction, clickValue));
        }
        return components;
    }

    public Component clone(){
        return new Component(displayText, hoverText, clickAction, clickValue);
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public List<String> getHoverText() {
        return hoverText;
    }

    public void setHoverText(List<String> hoverText) {
        this.hoverText = hoverText;
    }

    public ClickEvent.Action getClickAction() {
        return clickAction;
    }

    public void setClickAction(ClickEvent.Action clickAction) {
        this.clickAction = clickAction;
    }

    public String getClickValue() {
        return clickValue;
    }

    public void setClickValue(String clickValue) {
        this.clickValue = clickValue;
    }
}