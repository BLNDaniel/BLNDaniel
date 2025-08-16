package com.danny.treasurechests;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class HeadInteractionListener implements Listener {

    private final TreasureChestManager treasureChestManager;
    private final TreasureChests plugin;

    public HeadInteractionListener(TreasureChests plugin, TreasureChestManager treasureChestManager) {
        this.plugin = plugin;
        this.treasureChestManager = treasureChestManager;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null || clickedBlock.getType() != Material.PLAYER_HEAD) {
            return;
        }

        Location location = clickedBlock.getLocation();
        if (!treasureChestManager.isTreasureChest(location)) {
            return;
        }

        event.setCancelled(true);

        Location chestLocation = location.clone().subtract(0, 1, 0);
        Block chestBlock = chestLocation.getBlock();
        if (chestBlock.getState() instanceof Chest) {
            Chest chest = (Chest) chestBlock.getState();
            Player player = event.getPlayer();
            player.openInventory(chest.getBlockInventory());
        }
    }
}
