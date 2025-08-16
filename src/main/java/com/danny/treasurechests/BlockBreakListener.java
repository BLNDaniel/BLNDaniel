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

                    // Send feedback to the server
                    String message = plugin.getConfig().getString("broadcast-message", "&e%player% found a %tier% treasure chest!");
                    String tierName = lootResult.getTier().getName();
                    // Capitalize the first letter of the tier name for better display
                    tierName = tierName.substring(0, 1).toUpperCase() + tierName.substring(1);

                    message = message.replace("%player%", player.getName()).replace("%tier%", tierName);
                    org.bukkit.Bukkit.broadcastMessage(org.bukkit.ChatColor.translateAlternateColorCodes('&', message));

                    // Play sound based on tier settings
                    SoundInfo soundInfo = lootResult.getTier().getSoundInfo();
                    String soundName = soundInfo.getName();
                    try {
                        if (soundInfo.shouldBroadcast()) {
                            // Play for everyone
                            for (org.bukkit.entity.Player onlinePlayer : org.bukkit.Bukkit.getOnlinePlayers()) {
                                onlinePlayer.playSound(onlinePlayer.getLocation(), soundName, 1.0f, 1.0f);
                            }
                        } else {
                            // Play for just the finder
                            player.playSound(player.getLocation(), soundName, 1.0f, 1.0f);
                        }
                    } catch (Exception e) {
                        plugin.getLogger().warning("An error occurred while trying to play sound '" + soundName + "'. Is it a valid sound name?");
                    }
                });
            }
        });
    }
}
