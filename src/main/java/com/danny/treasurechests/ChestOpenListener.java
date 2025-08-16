package com.danny.treasurechests;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class ChestOpenListener implements Listener {

    private final TreasureChests plugin;
    private final TreasureChestManager treasureChestManager;

    public ChestOpenListener(TreasureChests plugin, TreasureChestManager treasureChestManager) {
        this.plugin = plugin;
        this.treasureChestManager = treasureChestManager;
    }

    @EventHandler
    public void onInventoryClose(org.bukkit.event.inventory.InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof org.bukkit.entity.Player)) return;
        org.bukkit.entity.Player player = (org.bukkit.entity.Player) event.getPlayer();

        // Check if this player was viewing one of our treasure chests
        org.bukkit.Location location = treasureChestManager.getViewingLocation(player);
        if (location != null) {
            // This player was viewing a treasure chest. Now we need to clean up.
            // First, always remove them from the viewing map
            treasureChestManager.removePlayerViewing(player);

            // Next, check if the chest is now empty
            if (location.getBlock().getType() == org.bukkit.Material.CHEST) {
                org.bukkit.block.Chest chest = (org.bukkit.block.Chest) location.getBlock().getState();
                boolean isEmpty = true;
                for (org.bukkit.inventory.ItemStack item : chest.getInventory().getContents()) {
                    if (item != null) {
                        isEmpty = false;
                        break;
                    }
                }

                if (isEmpty) {
                    // The chest is empty, so we can unregister it.
                    treasureChestManager.removeTreasureChest(location);
                    plugin.getLogger().info("Treasure chest at " + location + " is empty and has been unregistered.");
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK) return;
        if (event.getClickedBlock() == null || event.getClickedBlock().getType() != org.bukkit.Material.CHEST) return;

        org.bukkit.Location location = event.getClickedBlock().getLocation();
        if (treasureChestManager.isTreasureChest(location)) {
            event.setCancelled(true);

            LootTier tier = treasureChestManager.getTierAt(location);
            org.bukkit.block.Chest chest = (org.bukkit.block.Chest) location.getBlock().getState();

            // Create a new inventory with the custom title
            String title = org.bukkit.ChatColor.translateAlternateColorCodes('&', tier.getDisplayName());
            org.bukkit.inventory.Inventory customInventory = org.bukkit.Bukkit.createInventory(null, chest.getInventory().getSize(), title);

            // Copy contents from the real chest to our custom inventory
            customInventory.setContents(chest.getInventory().getContents());

            // Track that the player is viewing this chest
            treasureChestManager.setPlayerViewing(event.getPlayer(), location);

            event.getPlayer().openInventory(customInventory);
        }
    }
}
