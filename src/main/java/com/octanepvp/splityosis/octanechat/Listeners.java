package com.octanepvp.splityosis.octanechat;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.data.Rotatable;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Listeners implements Listener, CommandExecutor {

    private OctaneChat plugin;

    public Listeners(OctaneChat plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent e){
        e.setCancelled(true);

        List<Component> formatTemplate = plugin.getChatFormat();
        List<Component> processedFormat = new ArrayList<>();

        String rawMessage;
        if (e.getPlayer().hasPermission("octanechat.use-colors"))
            rawMessage = OctaneChat.translateAllColors(e.getMessage());
        else
            rawMessage = e.getMessage();

        List<BaseComponent> message = Arrays.asList(TextComponent.fromLegacyText(rawMessage));

        PlayerChatMessageEvent event = new PlayerChatMessageEvent(true, rawMessage, message);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        message = event.getMessage();

        for (Component component : formatTemplate) {
            processedFormat.add(component.clone().setPlayerPlaceholders(e.getPlayer()));
        }

        for (Player reader : Bukkit.getOnlinePlayers()){
            List<BaseComponent> msg = new ArrayList<>();
            for (Component component : processedFormat) {
                if (component.getDisplayText().equals("%message%"))
                    msg.addAll(message);
                else
                    msg.addAll(Arrays.asList(component.clone().setRelationalPlaceholders(e.getPlayer(), reader).fixColors().compile()));
            }
            reader.spigot().sendMessage(msg.toArray(new BaseComponent[0]));
        }

        List<BaseComponent> msg = new ArrayList<>();
        for (Component component : processedFormat) {
            System.out.println(component.getDisplayText());
            if (component.getDisplayText().equals("%message%")) {
                msg.addAll(message);
            }
            else
                msg.addAll(Arrays.asList(component.clone().setRelationalPlaceholders(e.getPlayer(), null).fixColors().compile()));
        }
        Bukkit.getConsoleSender().spigot().sendMessage(msg.toArray(new BaseComponent[0]));
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0 || !args[0].equalsIgnoreCase("reload")) {
            sender.sendMessage(ChatColor.RED + "Did you mean '/octanechat reload' ?");
            return false;
        }

        plugin.loadConfig();
        sender.sendMessage(ChatColor.GREEN + "Successfully reloaded the config!");

        return false;
    }
}