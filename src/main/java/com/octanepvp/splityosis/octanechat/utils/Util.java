package com.octanepvp.splityosis.octanechat.utils;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    public static final String LOG_PREFIX = "&8[&eOctaneFishing&8]"; //Only affects messages sent to console

    public static void sendMessage(CommandSender to, String message){
        to.sendMessage(colorize(message));
    }

    public static void sendMessage(CommandSender to, List<String> message){
        message.forEach(s -> {
            sendMessage(to, s);
        });
    }

    public static void broadcast(String message){
        for (Player onlinePlayer : Bukkit.getOnlinePlayers())
            sendMessage(onlinePlayer, message);
        log(message);
    }

    public static void broadcast(List<String> message){
        for (Player onlinePlayer : Bukkit.getOnlinePlayers())
            sendMessage(onlinePlayer, message);
        log(message);
    }

    public static void log(String message){
        sendMessage(Bukkit.getServer().getConsoleSender(), LOG_PREFIX+" "+message);
    }

    public static void log(List<String> message){
        List<String> msg = new ArrayList<>(message);
        if (!msg.isEmpty())
            msg.set(0, LOG_PREFIX+" "+msg.get(0));
        sendMessage(Bukkit.getServer().getConsoleSender(), msg);
    }

    private static final Pattern HEX_PATTERN = Pattern.compile("&(#\\w{6})");
    public static String colorize(String str) {
        Matcher matcher = HEX_PATTERN.matcher(net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', str));
        StringBuffer buffer = new StringBuffer();

        while (matcher.find())
            matcher.appendReplacement(buffer, net.md_5.bungee.api.ChatColor.of(matcher.group(1)).toString());

        return ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
    }

    public static List<String> colorize(List<String> lst){
        if (lst == null) return null;
        List<String> newList = new ArrayList<>();
        lst.forEach(s -> {
            newList.add(colorize(s));
        });
        return newList;
    }

    public static ItemStack createItemStack(Material material, int amount, String name, List<String> lore){
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta meta = itemStack.getItemMeta();
        if (name != null)
            meta.setDisplayName(colorize(name));
        meta.setLore(colorize(lore));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    public static ItemStack createItemStack(Material material, int amount, String name, String... lore){
        return createItemStack(material, amount, name, Arrays.asList(lore));
    }

    public static ItemStack createItemStack(Material material, int amount){
        return createItemStack(material, amount, null);
    }

    public static List<String> replaceList(List<String> lst, String from, String to){
        if (lst == null) return null;
        List<String> newList = new ArrayList<>();
        lst.forEach(s -> {
            newList.add(s.replace(from, to));
        });
        return newList;
    }

    public static ItemStack getItemFromSection(ConfigurationSection itemSection){
        if (itemSection == null) return null;
        String material = itemSection.getString("material");
        int amount = itemSection.getInt("amount");
        if (amount == 0) amount = 1;
        String customName = itemSection.getString("custom-name");
        List<String> lore = itemSection.getStringList("custom-lore");

        Map<Enchantment, Integer> enchants = new HashMap<>();
        ConfigurationSection enchantsSection = itemSection.getConfigurationSection("enchants");
        if (enchantsSection != null)
            for (String key : enchantsSection.getKeys(false))
                enchants.put(Enchantment.getByName(key), enchantsSection.getInt(key));

        ItemStack item = createItemStack(Material.getMaterial(Objects.requireNonNull(material)), amount, customName, lore);
        item.addUnsafeEnchantments(enchants);
        return item;
    }


    public static String locationToString(Location location){
        String world = location.getWorld().getName();
        String x = String.valueOf(location.getBlockX());
        String y = String.valueOf(location.getBlockY());
        String z = String.valueOf(location.getBlockZ());
        return world + "_" + x + "_" + y + "_" + z;
    }

    public static Location locationFromString(String str){
        String[] arr = str.split("_");
        World world = Bukkit.getWorld(arr[0]);
        double x = Double.parseDouble(arr[1]);
        double y = Double.parseDouble(arr[2]);
        double z = Double.parseDouble(arr[3]);
        return new Location(world, x, y, z);
    }

    public static String getFormattedEntityName(EntityType entityType){
        StringBuilder builder = new StringBuilder();
        for (String s : entityType.name().toLowerCase().split("_"))
            builder.append(s.substring(0, 1).toUpperCase()).append(s.substring(1)).append(" ");
        return builder.substring(0, builder.length()-1);
    }


    public static String createBar(int completed, int total, String completedChar, String emptyChar, String maxedOutChar){
        StringBuilder builder = new StringBuilder();
        if (completed == total){
            for (int i = 0; i < total; i++)
                builder.append(maxedOutChar);
            return colorize(builder.toString());
        }

        for (int i = 0; i < completed; i++)
            builder.append(completedChar);

        for (int i = 0; i < total - completed; i++)
            builder.append(emptyChar);
        return colorize(builder.toString());
    }

    public static ItemStack replaceTextInItem(ItemStack itemStack, String from, String to){
        ItemStack item = itemStack.clone();
        ItemMeta meta = item.getItemMeta();
        if (meta == null)
            return item;
        if (meta.hasDisplayName())
            meta.setDisplayName(meta.getDisplayName().replace(from, to));
        if (meta.hasLore())
            meta.setLore(replaceList(meta.getLore(), from, to));
        item.setItemMeta(meta);
        return item;
    }

    public static String getItemName(ItemStack item){
        String name;
        if (item.getItemMeta() != null && item.getItemMeta().hasDisplayName())
            name = item.getItemMeta().getDisplayName();
        else {
            name = item.getType().name().toLowerCase().replace("_", " ");
            if (!name.equals("TNT")) {
                name = name.toLowerCase();
                StringBuilder stringBuilder = new StringBuilder();
                for (String s : name.split(Pattern.quote(" "))) {
                    stringBuilder.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(" ");
                }
                name = stringBuilder.substring(0, stringBuilder.length()-1);
            }
        }
        return name;
    }

    public static List<String> getLore(ItemStack itemStack){
        if (itemStack.getItemMeta().hasLore()){
            return itemStack.getItemMeta().getLore();
        }
        return new ArrayList<>();
    }

    public static String formatDouble(double num) {
        if (num == Math.floor(num))
            return new DecimalFormat("###,###").format(num);

        String s = new DecimalFormat("###,###.##").format(num);
        String[] parts = s.split(Pattern.quote("."));

        if (parts.length == 1)  // Check if there's no fractional part
            return s + ".00";
        else if (parts[1].length() == 1)  // Check if fractional part has only one digit
            return s + "0";
        return s;
    }

    public static String formatInt(int num) {
        return new DecimalFormat("###,###").format(num);
    }

    public static void giveItemsToPlayer(Player player, ItemStack itemStack, int amount) {
        int maxStackSize = itemStack.getMaxStackSize();
        int remainingAmount = amount;

        while (remainingAmount > 0) {
            int giveAmount = Math.min(remainingAmount, maxStackSize);
            ItemStack giveItem = new ItemStack(itemStack);
            giveItem.setAmount(giveAmount);

            HashMap<Integer, ItemStack> leftovers = player.getInventory().addItem(giveItem);
            if (!leftovers.isEmpty()) {
                // If there are leftovers, drop them on the floor
                World world = player.getWorld();
                Location playerLocation = player.getLocation();
                for (ItemStack leftover : leftovers.values()) {
                    world.dropItemNaturally(playerLocation, leftover);
                }
            }

            remainingAmount -= giveAmount;
        }
    }

    public static  String formatTime(long milliseconds) {
        int totalSeconds = (int) (milliseconds / 1000);
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;

        String m;
        if (minutes == 0) m = "00";
        else if (minutes < 10) m = "0" + minutes;
        else m = String.valueOf(minutes);

        String s;
        if (seconds == 0) s = "00";
        else if (seconds < 10) s = "0" + seconds;
        else s = String.valueOf(seconds);

        return m + ":" + s;
    }

    public static boolean isMaterial(ItemStack itemStack, Material material){
        if (itemStack != null && itemStack.getType().equals(material)){
            return true;
        }
        return false;
    }

    public static boolean isNotNullOrAir(ItemStack itemStack){
        if (itemStack != null && !itemStack.getType().equals(Material.AIR)){
            return true;
        }
        return false;
    }

    public static boolean isNullOrAir(ItemStack itemStack){
        if (itemStack != null && itemStack.getType().equals(Material.AIR)){
            return true;
        }
        if (itemStack == null){
            return true;
        }
        return false;
    }

    public static int getRandomIntBetween(double min, double max) {
        if (min >= max) {
            throw new IllegalArgumentException("Min value must be less than max value.");
        }

        Random random = new Random();
        int range = (int) (max - min) + 1;
        return random.nextInt(range) + (int) min;
    }

    public static double getRandomNumber(double min, double max, int decimalPlaces) {
        if (min >= max || decimalPlaces < 0) {
            throw new IllegalArgumentException("Min value must be less than max value.");
        }

        // Calculate the range and adjust according to decimal places
        double range = max - min;
        double factor = Math.pow(10, decimalPlaces);

        // Generate a random number within the adjusted range
        Random random = new Random();
        double randomValue = min + (random.nextDouble() * range);

        // Round the random value to the specified decimal places
        randomValue = Math.round(randomValue * factor) / factor;

        return randomValue;
    }

    public static List<String> reverseColorize(List<String> lst) {
        if (lst == null) return null;
        List<String> newLst = new ArrayList<>();
        for (String s : lst)
            newLst.add(reverseColorize(s));
        return newLst;
    }

    private static final Pattern patternAll = Pattern.compile("§x(§[0-9a-fA-F]){6}");
    public static String reverseColorize(String input) {
        Matcher matcher = patternAll.matcher(input);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String colorCode = matcher.group().replaceAll("§", "");
            matcher.appendReplacement(sb, "&#" + colorCode.substring(1));
        }
        matcher.appendTail(sb);

        return sb.toString().replaceAll("§([0-9a-fklmnorx])", "&$1");
    }

    public FileConfiguration getConfigFromPath(String pluginName, String path){
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin(pluginName);
        File file = new File(plugin.getDataFolder(), path.replace("/", System.lineSeparator()));
        return YamlConfiguration.loadConfiguration(file);
    }

    public static boolean hasEmptySlot(Player player) {
        // Get the player's inventory
        ItemStack[] contents = player.getInventory().getStorageContents();

        // Loop through the inventory slots
        for (ItemStack item : contents) {
            // If the slot is empty, return true
            if (item == null) {
                return true;
            }
        }

        // If no empty slots were found, return false
        return false;
    }

    public static int countEmptySlots(Player player) {
        ItemStack[] contents = player.getInventory().getStorageContents();
        int emptySlots = 0;

        for (ItemStack item : contents) {
            if (item == null) {
                emptySlots++;
            }
        }

        return emptySlots;
    }
}