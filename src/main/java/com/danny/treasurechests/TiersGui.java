package com.danny.treasurechests;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TiersGui implements InventoryHolder {

    private final TreasureChests plugin;

    public TiersGui(TreasureChests plugin) {
        this.plugin = plugin;
    }

    public void open(Player player) {
        Inventory gui = Bukkit.createInventory(this, 54, "TreasureChests: Tiers");

        Map<String, LootTier> tiers = plugin.getLootManager().getLootTiers();
        if (tiers.isEmpty()) {
            // Handle case with no tiers loaded
            ItemStack info = new ItemStack(Material.BARRIER);
            ItemMeta meta = info.getItemMeta();
            meta.setDisplayName(ChatColor.RED + "No Tiers Loaded");
            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Check your config.yml for errors.");
            meta.setLore(lore);
            info.setItemMeta(meta);
            gui.setItem(22, info); // Center of the GUI
        } else {
            for (LootTier tier : tiers.values()) {
                ItemStack item = new ItemStack(Material.CHEST);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.RESET + "" + ChatColor.GREEN + tier.getName());
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Chance: " + ChatColor.YELLOW + (tier.getChance() * 100) + "%");
                lore.add(ChatColor.GRAY + "Items: " + ChatColor.YELLOW + tier.getItems().size());
                lore.add(" ");
                lore.add(ChatColor.AQUA + "Click to edit this tier.");
                meta.setLore(lore);
                item.setItemMeta(meta);
                gui.addItem(item);
            }
        }
        // Add a save button
        ItemStack saveButton = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta saveMeta = saveButton.getItemMeta();
        saveMeta.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "Save & Reload");
        List<String> saveLore = new ArrayList<>();
        saveLore.add(ChatColor.GRAY + "Saves all changes to config.yml");
        saveLore.add(ChatColor.GRAY + "and reloads the loot tables.");
        saveMeta.setLore(saveLore);
        saveButton.setItemMeta(saveMeta);
        gui.setItem(53, saveButton); // Bottom right corner

        player.openInventory(gui);
    }

    @Override
    public Inventory getInventory() {
        // We must override this method from InventoryHolder.
        // It's not used directly by us in this simple implementation,
        // but it's good practice to return null or a meaningful inventory.
        return null;
    }
}
