package com.danny.treasurechests;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class TierEditorGui implements InventoryHolder {

    private final TreasureChests plugin;

    public TierEditorGui(TreasureChests plugin) {
        this.plugin = plugin;
    }

    public void open(Player player, LootTier tier) {
        Inventory gui = Bukkit.createInventory(this, 54, "TreasureChests: Edit " + tier.getName());

        for (LootItem item : tier.getItems()) {
            ItemStack itemStack = new ItemStack(item.getMaterial());
            org.bukkit.inventory.meta.ItemMeta meta = itemStack.getItemMeta();

            List<String> lore = new ArrayList<>();
            lore.add(org.bukkit.ChatColor.GRAY + "Amount: " + org.bukkit.ChatColor.YELLOW + item.getAmount());
            lore.add(org.bukkit.ChatColor.GRAY + "Chance: " + org.bukkit.ChatColor.YELLOW + (item.getChance() * 100) + "%");
            lore.add(" ");
            lore.add(org.bukkit.ChatColor.RED + "Click to remove this item.");
            meta.setLore(lore);

            itemStack.setItemMeta(meta);
            gui.addItem(itemStack);
        }

        player.openInventory(gui);
    }

    @Override
    public Inventory getInventory() {
        return null;
    }
}
