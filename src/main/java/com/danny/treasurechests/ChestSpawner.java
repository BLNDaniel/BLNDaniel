package com.danny.treasurechests;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public class ChestSpawner {

    private final TreasureChests plugin;
    private final TreasureChestManager treasureChestManager;

    public ChestSpawner(TreasureChests plugin, TreasureChestManager treasureChestManager) {
        this.plugin = plugin;
        this.treasureChestManager = treasureChestManager;
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

        // Prepare for random placement
        java.util.List<Integer> slots = new java.util.ArrayList<>();
        for (int i = 0; i < inventory.getSize(); i++) {
            slots.add(i);
        }
        java.util.Collections.shuffle(slots);

        // Place items in random slots
        for (int i = 0; i < lootResult.getItems().size(); i++) {
            if (i >= slots.size()) break; // Stop if there are more items than slots
            inventory.setItem(slots.get(i), lootResult.getItems().get(i));
        }

        // Register the new treasure chest
        treasureChestManager.addTreasureChest(location, lootResult.getTier());

        plugin.getLogger().info("Successfully spawned a treasure chest at " + location.toString());
    }
}
