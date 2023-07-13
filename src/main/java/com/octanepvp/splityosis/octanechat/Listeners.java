package com.octanepvp.splityosis.octanechat;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
        if (e.getPlayer().hasPermission("octanechat.chat-colors"))
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


        //Chat features
        if (e.getMessage().contains(OctaneChat.chatItemSymbol))
            if (e.getPlayer().hasPermission("octanechat.chat-item"))
                message = applyItem(e.getPlayer(), message, event);


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

    private List<BaseComponent> applyItem(Player player, List<BaseComponent> msg, PlayerChatMessageEvent e){
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        TextComponent textComponent;

        if (itemStack == null || itemStack.getType() == Material.AIR){
            textComponent = new TextComponent(TextComponent.fromLegacyText(OctaneChat.translateAllColors(OctaneChat.chatItemHandItemFormat.replace("%player%", player.getName()))));
            ComponentBuilder componentBuilder = new ComponentBuilder();
            for (int i = 0; i < OctaneChat.chatItemHandItemHoverText.size(); i++) {
                if (i != 0)
                    componentBuilder.append("\n");
                componentBuilder.append(TextComponent.fromLegacyText(OctaneChat.translateAllColors(OctaneChat.chatItemHandItemHoverText.get(i))));
            }
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, componentBuilder.create()));
        }
        else{
            String itemName = getItemName(itemStack);
            BaseComponent[] display = TextComponent.fromLegacyText(OctaneChat.translateAllColors(OctaneChat.chatItemFormat.replace("%amount%", String.valueOf(itemStack.getAmount())).replace("%item%", itemName)));
            textComponent = new TextComponent(display);
            ComponentBuilder componentBuilder = new ComponentBuilder();
            componentBuilder.append(TextComponent.fromLegacyText(OctaneChat.translateAllColors(itemName)));
            componentBuilder.append("\n");
            componentBuilder.append(TextComponent.fromLegacyText(String.valueOf(ChatColor.RESET)));

            TreeMap<String, String> cursedEnchants = new TreeMap<>();
            TreeMap<String, String> normalEnchants = new TreeMap<>();

            itemStack.getEnchantments().forEach((enchantment, integer) -> {
                if (enchantment.isCursed())
                    cursedEnchants.put(getEnchantDisplayName(enchantment), " ");
                else normalEnchants.put(getEnchantDisplayName(enchantment), toRoman(integer));
            });

            for (Map.Entry<String, String> entry : normalEnchants.entrySet()) {
                componentBuilder.append(TextComponent.fromLegacyText(ChatColor.GRAY + entry.getKey() + " " + entry.getValue()));
                componentBuilder.append("\n");
                componentBuilder.append(TextComponent.fromLegacyText(String.valueOf(ChatColor.RESET)));
            }

            for (Map.Entry<String, String> entry : cursedEnchants.entrySet()) {
                componentBuilder.append(TextComponent.fromLegacyText(ChatColor.RED + entry.getKey()));
                componentBuilder.append("\n");
                componentBuilder.append(TextComponent.fromLegacyText(String.valueOf(ChatColor.RESET)));
            }


            if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasLore()){
                for (String s : itemStack.getItemMeta().getLore()) {
                    componentBuilder.append(TextComponent.fromLegacyText(String.valueOf(ChatColor.LIGHT_PURPLE) + ChatColor.ITALIC + s));
                    componentBuilder.append("\n");
                    componentBuilder.append(TextComponent.fromLegacyText(String.valueOf(ChatColor.RESET)));
                }
            }
            textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, componentBuilder.create()));
        }
        return e.replaceComponents(msg, OctaneChat.chatItemSymbol, textComponent);
    }

    private String getItemName(ItemStack item){
        String name;
        if (item.getItemMeta() != null && item.getItemMeta().hasDisplayName())
            name = item.getItemMeta().getDisplayName();
        else {
            if (item.getType() == Material.TNT){
                name = "TNT";
            }
            else {
                name = item.getType().name().toLowerCase().replace("_", " ");
                name = name.substring(0, 1).toUpperCase() + name.substring(1);
            }
        }
        return name;
    }

    private final static TreeMap<Integer, String> map = new TreeMap<>();

    static {

        map.put(1000, "M");
        map.put(900, "CM");
        map.put(500, "D");
        map.put(400, "CD");
        map.put(100, "C");
        map.put(90, "XC");
        map.put(50, "L");
        map.put(40, "XL");
        map.put(10, "X");
        map.put(9, "IX");
        map.put(5, "V");
        map.put(4, "IV");
        map.put(1, "I");

    }

    public final static String toRoman(int number) {
        int l =  map.floorKey(number);
        if ( number == l ) {
            return map.get(number);
        }
        return map.get(l) + toRoman(number-l);
    }

    private String getEnchantDisplayName(Enchantment enchantment){
        if (enchantment.equals(Enchantment.VANISHING_CURSE))
            return "Curse Of Vanishing";

        String name = enchantment.getKey().getKey();
        name.replace("_", " ");
        return capitalizeWords(name);
    }

    public static String capitalizeWords(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder result = new StringBuilder();
        String[] words = input.split("\\s+");

        for (String word : words) {
            if (!word.isEmpty()) {
                char firstChar = Character.toUpperCase(word.charAt(0));
                String capitalizedWord = firstChar + word.substring(1);
                result.append(capitalizedWord).append(" ");
            }
        }

        return result.toString().trim();
    }
}