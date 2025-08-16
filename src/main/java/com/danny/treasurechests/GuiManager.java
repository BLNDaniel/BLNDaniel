package com.danny.treasurechests;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class GuiManager implements Listener {

    private final TreasureChests plugin;

    public GuiManager(TreasureChests plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // We will add logic here to identify our custom GUIs.
        // For now, as a placeholder, we can check for a specific title.
        // A more robust system might involve a map of open GUIs.

        String title = event.getView().getTitle();

        // This is a simple way to protect all our GUIs at once if we use a common prefix.
        if (title.startsWith("TreasureChests:")) {
            // Prevent players from taking items out of the GUI
            event.setCancelled(true);

            // Route the click to the specific GUI's handler.
            if (event.getInventory().getHolder() instanceof TiersGui) {
                handleTiersGuiClick(event);
            } else if (event.getInventory().getHolder() instanceof TierEditorGui) {
                handleTierEditorGuiClick(event);
            }
        }
    }

    private void handleTiersGuiClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || !clickedItem.hasItemMeta()) {
            return;
        }
        Player player = (Player) event.getWhoClicked();

        // Handle Save Button
        if (clickedItem.getType() == org.bukkit.Material.EMERALD_BLOCK) {
            plugin.getLootManager().saveLootTables();
            player.closeInventory();
            player.sendMessage(org.bukkit.ChatColor.GREEN + "TreasureChests configuration saved and reloaded!");
            return;
        }

        // Handle Tier Click
        String tierName = org.bukkit.ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
        LootTier tier = plugin.getLootManager().getLootTiers().get(tierName.toLowerCase());

        if (tier != null) {
            plugin.getTierEditorGui().open(player, tier);
        }
    }

    private void handleTierEditorGuiClick(org.bukkit.event.inventory.InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null) return;

        // Get tier from title
        String title = event.getView().getTitle();
        String tierName = title.replace("TreasureChests: Edit ", "").toLowerCase();
        LootTier tier = plugin.getLootManager().getLootTiers().get(tierName);
        if (tier == null) return;

        // Find the LootItem corresponding to the clicked ItemStack
        LootItem itemToRemove = null;
        for (LootItem lootItem : tier.getItems()) {
            if (lootItem.getMaterial() == clickedItem.getType()) {
                // This is a simplified check. A real implementation would need to be more robust,
                // perhaps by storing a unique ID in the item's NBT tag. For this scope,
                // we assume one item type only appears once per tier.
                itemToRemove = lootItem;
                break;
            }
        }

        if (itemToRemove != null) {
            tier.getItems().remove(itemToRemove);
            player.sendMessage(ChatColor.YELLOW + "Removed " + itemToRemove.getMaterial().name() + " from tier " + tier.getName());
            // Re-open the GUI to show the change
            plugin.getTierEditorGui().open(player, tier);
        }
    }
}
