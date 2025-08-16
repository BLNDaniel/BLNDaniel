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
        org.bukkit.inventory.InventoryHolder holder = event.getInventory().getHolder();

        // Check if the closed inventory was one of our custom treasure chests
        if (holder instanceof TreasureChestInventoryHolder) {
            TreasureChestInventoryHolder customHolder = (TreasureChestInventoryHolder) holder;
            org.bukkit.Location location = customHolder.getChestLocation();
            org.bukkit.inventory.ItemStack[] finalContents = event.getInventory().getContents();

            // Get the real chest block and update its contents
            if (location.getBlock().getType() == org.bukkit.Material.CHEST) {
                org.bukkit.block.Chest chest = (org.bukkit.block.Chest) location.getBlock().getState();
                chest.getInventory().setContents(finalContents);

                // Check if the chest is now empty and unregister it if so
                boolean isEmpty = true;
                for (org.bukkit.inventory.ItemStack item : finalContents) {
                    if (item != null) {
                        isEmpty = false;
                        break;
                    }
                }

                if (isEmpty) {
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

            // Create a new inventory with the custom title and our custom holder
            String title = org.bukkit.ChatColor.translateAlternateColorCodes('&', tier.getDisplayName());
            TreasureChestInventoryHolder holder = new TreasureChestInventoryHolder(location);
            org.bukkit.inventory.Inventory customInventory = org.bukkit.Bukkit.createInventory(holder, chest.getInventory().getSize(), title);

            // Copy contents from the real chest to our custom inventory
            customInventory.setContents(chest.getInventory().getContents());

            event.getPlayer().openInventory(customInventory);
        }
    }
}
