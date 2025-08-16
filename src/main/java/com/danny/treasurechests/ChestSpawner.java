package com.danny.treasurechests;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class ChestSpawner {

    private final TreasureChests plugin;

    public ChestSpawner(TreasureChests plugin) {
        this.plugin = plugin;
    }

    public void spawnTreasure(Location location, LootManager.LootResult lootResult) {
        // This method must be run on the main server thread.
        if (lootResult == null || lootResult.getItems().isEmpty()) {
            plugin.getLogger().warning("Loot result was null or empty, cannot spawn treasure.");
            return;
        }

        // Place the actual chest and fill it
        location.getBlock().setType(org.bukkit.Material.CHEST);
        org.bukkit.block.Chest chest = (org.bukkit.block.Chest) location.getBlock().getState();
        org.bukkit.inventory.Inventory inventory = chest.getInventory();
        for (ItemStack item : lootResult.getItems()) {
            inventory.addItem(item);
        }

        plugin.getLogger().info("Successfully spawned a treasure chest at " + location.toString());
    }
}
