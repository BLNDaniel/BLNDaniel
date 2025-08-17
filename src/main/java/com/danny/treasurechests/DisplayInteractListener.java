package com.danny.treasurechests;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.Inventory;

public class DisplayInteractListener implements Listener {

    private final TreasureChestManager treasureChestManager;

    public DisplayInteractListener(TreasureChestManager treasureChestManager) {
        this.treasureChestManager = treasureChestManager;
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        if (!(event.getRightClicked() instanceof ItemDisplay)) {
            return;
        }

        ItemDisplay itemDisplay = (ItemDisplay) event.getRightClicked();
        Location displayLocation = itemDisplay.getLocation().toBlockLocation();

        TreasureChestManager.TreasureChestData chestData = treasureChestManager.getChestDataAt(displayLocation);

        if (chestData != null) {
            event.setCancelled(true);
            Player player = event.getPlayer();

            // Create a virtual barrel inventory
            Inventory barrelInventory = Bukkit.createInventory(null, InventoryType.BARREL, chestData.tier().getDisplayName());
            barrelInventory.setContents(chestData.items().toArray(new org.bukkit.inventory.ItemStack[0]));

            player.openInventory(barrelInventory);
        }
    }
}
