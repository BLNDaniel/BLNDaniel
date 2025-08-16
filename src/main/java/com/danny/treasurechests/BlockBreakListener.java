package com.danny.treasurechests;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakListener implements Listener {

    private final TreasureChests plugin;
    private final LootManager lootManager;
    private final ChestSpawner chestSpawner;

    public BlockBreakListener(TreasureChests plugin, LootManager lootManager, ChestSpawner chestSpawner) {
        this.plugin = plugin;
        this.lootManager = lootManager;
        this.chestSpawner = chestSpawner;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        // Check if the block is in the allowed list
        java.util.List<String> allowedBlocks = plugin.getConfig().getStringList("allowed-blocks");
        if (!allowedBlocks.contains(event.getBlock().getType().name())) {
            return;
        }

        // Check the drop chance
        double dropChance = plugin.getConfig().getDouble("drop-chance");
        if (new java.util.Random().nextDouble() >= dropChance) {
            return;
        }

        final org.bukkit.entity.Player player = event.getPlayer();
        final org.bukkit.Location location = event.getBlock().getLocation();

        // Prevent the block from dropping its normal items
        event.setDropItems(false);

        // Asynchronously calculate the loot
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            final LootManager.LootResult lootResult = lootManager.calculateRandomLoot();

            // If loot was successfully calculated, schedule the spawning back on the main thread
            if (lootResult != null && !lootResult.getItems().isEmpty()) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    // Spawn the treasure chest and custom model
                    chestSpawner.spawnTreasure(location, lootResult);

                    // Send feedback to the player
                    String message = plugin.getConfig().getString("message", "&aYou found a treasure!");
                    message = message.replace("%player%", player.getName());
                    player.sendMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

                    try {
                        String soundName = plugin.getConfig().getString("sound", "ENTITY_PLAYER_LEVELUP");
                        org.bukkit.Sound sound = org.bukkit.Sound.valueOf(soundName.toUpperCase());
                        player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Invalid sound name in config.yml: " + plugin.getConfig().getString("sound"));
                    }
                });
            }
        });
    }
}
