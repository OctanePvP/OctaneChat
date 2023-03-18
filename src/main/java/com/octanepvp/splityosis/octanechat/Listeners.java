package com.octanepvp.splityosis.octanechat;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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

        String playerMessage;
        if (e.getPlayer().hasPermission("octanechat.use-colors"))
            playerMessage = OctaneChat.translateAllColors(e.getMessage());
        else playerMessage = e.getMessage();

        formatTemplate.forEach(component -> {
            processedFormat.add(component.clone().setPlayerPlaceholders(e.getPlayer()));
        });


        for (Player reader : Bukkit.getOnlinePlayers()){
            TextComponent msg = new TextComponent();
            processedFormat.forEach(component -> {
                msg.addExtra(component.clone().setRelationalPlaceholders(e.getPlayer(), reader).fixColors().replace("%message%", playerMessage).compile());
            });
            reader.spigot().sendMessage(msg);
        }

        TextComponent msg = new TextComponent();
        processedFormat.forEach(component -> {
            msg.addExtra(component.clone().setRelationalPlaceholders(e.getPlayer(), null).fixColors().replace("%message%", playerMessage).compile());
        });
        Bukkit.getConsoleSender().spigot().sendMessage(msg);

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