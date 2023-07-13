package com.octanepvp.splityosis.octanechat;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class OctaneChat extends JavaPlugin {

    private List<Component> chatFormat;
    private Map<String, Component> componentMap;

    //Features
    public static boolean chatItemEnabled = false;
    public static String chatItemSymbol;
    public static String chatItemFormat;
    public static String chatItemHandItemFormat;
    public static List<String> chatItemHandItemHoverText;

    public static boolean chatInvEnabled = false;
    public static String chatInvSymbol;
    public static String chatInvFormat;
    public static List<String> chatInvHoverText;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Listeners listeners = new Listeners(this);
        getServer().getPluginManager().registerEvents(listeners, this);
        getCommand("octanechat").setExecutor(listeners);
        loadConfig();
    }

    @Override
    public void onDisable() {

    }

    public void loadConfig(){
        reloadConfig();
        String rawFormat = getConfig().getString("format");
        componentMap = new HashMap<>();

        chatItemEnabled = getConfig().getBoolean("features.chat-item.enable");
        chatItemSymbol = getConfig().getString("features.chat-item.symbol");
        chatItemFormat = getConfig().getString("features.chat-item.format");
        chatItemHandItemFormat = getConfig().getString("features.chat-item.hand-item-format");
        chatItemHandItemHoverText = getConfig().getStringList("features.chat-item.hand-item-hover-text");

        chatInvEnabled = getConfig().getBoolean("features.chat-item.chat-inv.enable");
        chatInvSymbol = getConfig().getString("features.chat-item.chat-inv.symbol");
        chatInvFormat = getConfig().getString("features.chat-item.chat-inv.format");
        chatInvHoverText = getConfig().getStringList("features.chat-item.chat-inv.hover-text");


        ConfigurationSection componentsSection = getConfig().getConfigurationSection("components");
        if (componentsSection != null)
            for (String key : componentsSection.getKeys(false)){
                String displayText = translateAllColors(componentsSection.getString(key+".display-text"));
                List<String> hoverText = translateAllColors(componentsSection.getStringList(key+".hover-text"));
                ClickEvent.Action clickAction = ClickEvent.Action.valueOf(componentsSection.getString(key+".click-event.action"));
                String clickValue = translateAllColors(componentsSection.getString(key+".click-event.value"));
                componentMap.put(key, new Component(displayText, hoverText, clickAction, clickValue));
            }

        chatFormat = new ArrayList<>();
        List<TextComponent> seperatedFormat = separateString(rawFormat);
        seperatedFormat.forEach(textComponent -> {
            String text = textComponent.getText();
            if (text.startsWith("%{") && text.endsWith("}%")){
                String substring = text.substring(2, text.length() - 2);
                Component component = componentMap.get(substring);
                if (component != null){
                    chatFormat.add(component);
                    return;
                }
                Bukkit.broadcastMessage("Unknown Component '"+ substring+"'");
            }
            chatFormat.add(new Component(translateAllColors(text), null, null, null));
        });
    }

    public List<Component> getChatFormat() {
        return chatFormat;
    }

    public static List<TextComponent> separateString(String message) {
        List<TextComponent> components = new ArrayList<>();
        Pattern pattern = Pattern.compile("%\\{([^}]+)}%|%message%");
        Matcher matcher = pattern.matcher(message);
        int lastEnd = 0;
        while (matcher.find()) {
            if (matcher.start() > lastEnd) {
                components.add(new TextComponent(message.substring(lastEnd, matcher.start())));
            }
            if (matcher.group().equals("%message%")){
                components.add(new TextComponent("%message%"));
                lastEnd = matcher.end();
                continue;
            }
            String placeholder = matcher.group(1);
            components.add(new TextComponent("%{" + placeholder + "}%"));
            lastEnd = matcher.end();
        }
        if (lastEnd < message.length()) {
            components.add(new TextComponent(message.substring(lastEnd)));
        }
        return components;
    }

    public static final Pattern HEX_PATTERN = Pattern.compile("&(#[A-Fa-f0-9]{6})");
    public static String translateHexColorCodes(String string) {
        Matcher matcher = HEX_PATTERN.matcher(string);
        while (matcher.find()) {
            string = string.replace(matcher.group(), "" + net.md_5.bungee.api.ChatColor.of(matcher.group().substring(1)));
        }
        return string;
    }

    public static String translateOldColorCodes(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static String translateAllColors(String str){
        return translateOldColorCodes(translateHexColorCodes(str));
    }

    public static List<String> translateAllColors(List<String> lst){
        List<String> nlst = new ArrayList<>();
        lst.forEach(s -> {
            nlst.add(translateAllColors(s));
        });
        return nlst;
    }
}