package com.octanepvp.splityosis.octanechat;

import com.octanepvp.splityosis.octanechat.files.ActionsConfig;
import com.octanepvp.splityosis.octanechat.files.AnnouncementsConfig;
import com.octanepvp.splityosis.octanechat.files.DataFile;
import com.octanepvp.splityosis.octanechat.listeners.ActionsListeners;
import com.octanepvp.splityosis.octanechat.listeners.Listeners;
import com.octanepvp.splityosis.octanechat.objects.AnnouncementScheduler;
import com.octanepvp.splityosis.octanechat.objects.Component;
import dev.splityosis.sysengine.configlib.ConfigLib;
import dev.splityosis.sysengine.configlib.manager.ConfigManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class OctaneChat extends JavaPlugin {

    public static boolean factionsEnabled;

    public static OctaneChat plugin;
    public static AnnouncementsConfig announcementsConfig;
    public static ConfigManager configManager;

    private List<Component> chatFormat;
    private Map<String, Component> componentMap;
    private String chatColorPlaceholder;

    //Features
    public static boolean chatItemEnabled = false;
    public static String chatItemSymbol;
    public static String chatItemFormat;
    public static String chatItemHandItemFormat;
    public static List<String> chatItemHandItemHoverText;

    public static boolean chatInvEnabled = false;
    public static String chatInvSymbol;
    public static String chatInvFormat;
    public static String chatInvTitle;
    public static List<String> chatInvHoverText;
    public static int chatInvExpireTime;
    public static List<String> chatInvExpiredMessage;

    private ActionsConfig actionsConfig;
    private DataFile dataFile;

    @Override
    public void onEnable() {
        plugin = this;
        configManager = ConfigLib.createConfigManager(this);
        saveDefaultConfig();
        actionsConfig = new ActionsConfig();//getDataFolder(), "actions");
        announcementsConfig = new AnnouncementsConfig();
        try {
            configManager.registerConfig(actionsConfig, new File(getDataFolder(), "actions.yml"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        try {
            configManager.registerConfig(announcementsConfig, new File(getDataFolder(), "announcements.yml"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        dataFile = new DataFile(new File(getDataFolder(), "data.yml"));
        dataFile.initialize(this);

        Listeners listeners = new Listeners(this);
        getServer().getPluginManager().registerEvents(listeners, this);
        getCommand("octanechat").setExecutor(listeners);
        getServer().getPluginManager().registerEvents(new ActionsListeners(this), this);
        loadConfig();
        factionsEnabled = hookFactions();
    }

    @Override
    public void onDisable() {

    }

    public void loadConfig(){
        reloadConfig();
        try {
            configManager.reload(actionsConfig);
            configManager.reload(announcementsConfig);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        AnnouncementScheduler.reloadSchedule();

        String rawFormat = getConfig().getString("format");
        componentMap = new HashMap<>();
        chatColorPlaceholder = getConfig().getString("chat-color");

        chatItemEnabled = getConfig().getBoolean("features.chat-item.enable");
        chatItemSymbol = getConfig().getString("features.chat-item.symbol");
        chatItemFormat = getConfig().getString("features.chat-item.format");
        chatItemHandItemFormat = getConfig().getString("features.chat-item.hand-item-format");
        chatItemHandItemHoverText = getConfig().getStringList("features.chat-item.hand-item-hover-text");

        chatInvEnabled = getConfig().getBoolean("features.chat-inv.enable");
        chatInvSymbol = getConfig().getString("features.chat-inv.symbol");
        chatInvFormat = getConfig().getString("features.chat-inv.format");
        chatInvTitle = getConfig().getString("features.chat-inv.inventory-title");
        chatInvHoverText = getConfig().getStringList("features.chat-inv.hover-text");
        chatInvExpireTime = getConfig().getInt("features.chat-inv.inventory-expire-time");
        chatInvExpiredMessage = getConfig().getStringList("features.chat-inv.inventory-expired-message");


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

    public ActionsConfig getActionsConfig() {
        return actionsConfig;
    }

    public DataFile getDataFile() {
        return dataFile;
    }

    public String getChatColorPlaceholder() {
        return chatColorPlaceholder;
    }

    public boolean hookFactions() {
        Plugin factionsPlugin = Bukkit.getPluginManager().getPlugin("Factions");
        if (factionsPlugin == null) return false;
        return factionsPlugin.isEnabled();
    }
}