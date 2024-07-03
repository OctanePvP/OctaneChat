package com.octanepvp.splityosis.octanechat.objects;

import com.octanepvp.splityosis.octanechat.OctaneChat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PlayerInvSnapshot implements InventoryHolder {

    private static final ItemStack seperateItem = createItemStack(Material.GRAY_STAINED_GLASS_PANE, 1, "&7", new ArrayList<>());
    private Inventory inventory;

    public PlayerInvSnapshot(Player player) {
        inventory = Bukkit.createInventory(this, 54, OctaneChat.translateAllColors(OctaneChat.chatInvTitle.replace("%player%", player.getName())));

        for (int i = 9; i < 18; i++)
            inventory.setItem(i, seperateItem.clone());
        inventory.setItem(0, seperateItem.clone());
        inventory.setItem(5, seperateItem.clone());
        inventory.setItem(6, seperateItem.clone());
        inventory.setItem(8, seperateItem.clone());

        PlayerInventory playerInv = player.getInventory();

        inventory.setItem(1, clone(playerInv.getHelmet()));
        inventory.setItem(2, clone(playerInv.getChestplate()));
        inventory.setItem(3, clone(playerInv.getLeggings()));
        inventory.setItem(4, clone(playerInv.getBoots()));
        inventory.setItem(7, clone(playerInv.getItemInOffHand()));

        for (int i = 18; i < 45; i++)
            inventory.setItem(i, clone(playerInv.getItem(i-9)));

        for (int i = 45; i < 54; i++)
            inventory.setItem(i, clone(playerInv.getItem(i-45)));
    }

    @NotNull
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private static ItemStack createItemStack(Material material, int amount, String name, List<String> lore){
        ItemStack itemStack = new ItemStack(material, amount);
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null) return itemStack;
        if (name != null)
            meta.setDisplayName(OctaneChat.translateAllColors(name));
        if (lore != null)
            meta.setLore(OctaneChat.translateAllColors(lore));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    private static ItemStack clone(ItemStack itemStack){
        if (itemStack == null) return null;
        return itemStack.clone();
    }
}
