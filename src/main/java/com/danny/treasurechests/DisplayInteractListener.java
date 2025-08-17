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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

        // Place items in random slots
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < barrelInventory.getSize(); i++) {
            slots.add(i);
        }
        Collections.shuffle(slots);

        for (int i = 0; i < chestData.items().size(); i++) {
            if (i >= slots.size()) break; // Should not happen if loot size <= inventory size
            barrelInventory.setItem(slots.get(i), chestData.items().get(i));
        }

        player.openInventory(barrelInventory);
    }
}
