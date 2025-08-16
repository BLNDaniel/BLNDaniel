package com.danny.treasurechests;

import org.bukkit.Location;
import org.bukkit.block.Barrel;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

public class BarrelCloseListener implements Listener {

    private final TreasureChestManager treasureChestManager;
    private final DisplayManager displayManager;

    public BarrelCloseListener(DisplayManager displayManager, TreasureChestManager treasureChestManager) {
        this.displayManager = displayManager;
        this.treasureChestManager = treasureChestManager;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        if (!(inventory.getHolder() instanceof Barrel)) {
            return;
        }

        Barrel barrel = (Barrel) inventory.getHolder();
        Location barrelLocation = barrel.getLocation();

        if (treasureChestManager.isTreasureChest(barrelLocation)) {
            if (inventory.isEmpty()) {
                // Find the associated ItemDisplay entity
                ItemDisplay display = findAssociatedDisplay(barrelLocation);
                if (display != null) {
                    displayManager.despawnTreasure(barrelLocation, display);
                }
            }
        }
    }

    private ItemDisplay findAssociatedDisplay(Location barrelLocation) {
        // Search for the ItemDisplay entity near the barrel
        for (Entity entity : barrelLocation.getChunk().getEntities()) {
            if (entity instanceof ItemDisplay) {
                // Check if it's at the same block location (center)
                if (entity.getLocation().toBlockLocation().equals(barrelLocation)) {
                    return (ItemDisplay) entity;
                }
            }
        }
        return null;
    }
}
