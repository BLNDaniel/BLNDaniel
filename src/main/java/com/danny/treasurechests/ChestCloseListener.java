package com.danny.treasurechests;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

public class ChestCloseListener implements Listener {

    private final TreasureChests plugin;
    private final TreasureChestManager treasureChestManager;

    public ChestCloseListener(TreasureChests plugin, TreasureChestManager treasureChestManager) {
        this.plugin = plugin;
        this.treasureChestManager = treasureChestManager;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();

        if (inventory.getHolder() instanceof Chest) {
            Chest chest = (Chest) inventory.getHolder();
            Location chestLocation = chest.getLocation();
            Location headLocation = chestLocation.clone().add(0, 1, 0);

            if (treasureChestManager.isTreasureChest(headLocation)) {
                if (inventory.isEmpty()) {
                    LootTier tier = treasureChestManager.getTierAt(headLocation);
                    if (tier != null) {
                        // Play the despawn animation
                        AnimationManager.playAnimation(plugin, headLocation, tier.getDespawnAnimation());
                    }

                    // Schedule the removal of the chest after the animation
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            // Remove the head block
                            Block headBlock = headLocation.getBlock();
                            headBlock.setType(Material.AIR);

                            // Remove the chest block
                            Block chestBlock = chestLocation.getBlock();
                            chestBlock.setType(Material.AIR);

                            // Unregister the treasure chest
                            treasureChestManager.removeTreasureChest(headLocation);
                        }
                    }.runTaskLater(plugin, 20L); // 20 ticks = 1 second delay
                }
            }
        }
    }
}
