package com.danny.treasurechests;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Skull;
import org.bukkit.inventory.Inventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.UUID;

public class HeadSpawner {

    private final TreasureChests plugin;
    private final TreasureChestManager treasureChestManager;

    public HeadSpawner(TreasureChests plugin, TreasureChestManager treasureChestManager) {
        this.plugin = plugin;
        this.treasureChestManager = treasureChestManager;
    }

    public void spawnHead(Location location, LootManager.LootResult lootResult) {
        if (lootResult == null) return;

        // Play spawn animation using the AnimationManager
        AnimationManager.playAnimation(plugin, location, lootResult.getTier().getSpawnAnimation());

        // Delay the appearance of the head and chest to sync with animation
        new BukkitRunnable() {
            @Override
            public void run() {
                // 1. Place the hidden chest one block below
                Location chestLocation = location.clone().subtract(0, 1, 0);
                Block chestBlock = chestLocation.getBlock();
                chestBlock.setType(Material.CHEST);
                Chest chest = (Chest) chestBlock.getState();
                Inventory inventory = chest.getInventory();

                java.util.List<Integer> slots = new java.util.ArrayList<>();
                for (int i = 0; i < inventory.getSize(); i++) slots.add(i);
                Collections.shuffle(slots);
                for (int i = 0; i < lootResult.getItems().size(); i++) {
                    if (i >= slots.size()) break;
                    inventory.setItem(slots.get(i), lootResult.getItems().get(i));
                }

                // 2. Place the player head
                Block headBlock = location.getBlock();
                headBlock.setType(Material.PLAYER_HEAD);
                Skull skull = (Skull) headBlock.getState();

                // 3. Apply the custom texture using Paper API
                PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
                try {
                    String textureValue = lootResult.getTier().getHeadTexture();
                    profile.setProperty(new ProfileProperty("textures", textureValue));
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to apply custom texture: " + e.getMessage());
                }
                skull.setPlayerProfile(profile);
                skull.update();

                // 4. Register the head location
                treasureChestManager.addTreasureChest(location, lootResult.getTier());
                plugin.getLogger().info("Successfully spawned a treasure head at " + location);
            }
        }.runTaskLater(plugin, 10L); // Delay of 10 ticks (0.5 seconds)
    }
}
