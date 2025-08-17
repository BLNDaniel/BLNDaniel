package com.danny.treasurechests;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;

import java.util.UUID;

public class DisplayInteractListener implements Listener {

    private final TreasureChestManager treasureChestManager;

    public DisplayInteractListener(TreasureChestManager treasureChestManager) {
        this.treasureChestManager = treasureChestManager;
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Interaction)) {
            return;
        }

        UUID interactionId = event.getRightClicked().getUniqueId();
        if (!treasureChestManager.isTreasureChestInteraction(interactionId)) {
            return;
        }

        event.setCancelled(true);

        Location location = treasureChestManager.getLocationFromInteractionId(interactionId);
        if (location == null) return;

        TreasureChestManager.TreasureChestData chestData = treasureChestManager.getChestDataAt(location);
        if (chestData == null) return;

        Player player = event.getPlayer();

        // Create a virtual barrel inventory
        Inventory barrelInventory = Bukkit.createInventory(null, InventoryType.BARREL, chestData.tier().getDisplayName());
        barrelInventory.setContents(chestData.items().toArray(new org.bukkit.inventory.ItemStack[0]));

        player.openInventory(barrelInventory);
    }
}
