package com.danny.treasurechests;

import org.bukkit.Location;
import org.bukkit.block.Barrel;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

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
        // The display is spawned at block center, so we get the block location
        Location displayLocation = itemDisplay.getLocation().toBlockLocation();

        if (treasureChestManager.isTreasureChest(displayLocation)) {
            event.setCancelled(true);
            Player player = event.getPlayer();

            if(displayLocation.getBlock().getState() instanceof Barrel) {
                Barrel barrel = (Barrel) displayLocation.getBlock().getState();
                player.openInventory(barrel.getInventory());
            }
        }
    }
}
