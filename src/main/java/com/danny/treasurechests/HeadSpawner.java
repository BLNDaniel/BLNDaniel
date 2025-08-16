package com.danny.treasurechests;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Skull;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class HeadSpawner {

    private final TreasureChests plugin;
    private final TreasureChestManager treasureChestManager;

    public HeadSpawner(TreasureChests plugin, TreasureChestManager treasureChestManager) {
        this.plugin = plugin;
        this.treasureChestManager = treasureChestManager;
    }

    public void spawnHead(Location location, LootManager.LootResult lootResult) {
        // This method must be run on the main server thread.
        if (lootResult == null) return;

        // 1. Place the hidden chest one block below
        Location chestLocation = location.clone().subtract(0, 1, 0);
        Block chestBlock = chestLocation.getBlock();
        chestBlock.setType(Material.CHEST);
        Chest chest = (Chest) chestBlock.getState();
        Inventory inventory = chest.getInventory();

        // Fill it with items (randomly)
        java.util.List<Integer> slots = new java.util.ArrayList<>();
        for (int i = 0; i < inventory.getSize(); i++) slots.add(i);
        java.util.Collections.shuffle(slots);
        for (int i = 0; i < lootResult.getItems().size(); i++) {
            if (i >= slots.size()) break;
            inventory.setItem(slots.get(i), lootResult.getItems().get(i));
        }

        // 2. Place the player head
        Block headBlock = location.getBlock();
        headBlock.setType(Material.PLAYER_HEAD);
        Skull skull = (Skull) headBlock.getState();

        // 3. Apply the custom texture using the Paper API
        PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());
        PlayerTextures textures = profile.getTextures();
        try {
            // The texture value from the config is a Base64 string, which contains the URL.
            // We need to decode it to get the URL.
            byte[] decodedBytes = java.util.Base64.getDecoder().decode(lootResult.getTier().getHeadTexture());
            String decoded = new String(decodedBytes);
            // This is a bit of a hack to parse the URL from the JSON structure.
            String urlString = decoded.split("\"url\"\\s*:\\s*\"")[1].split("\"")[0];
            URL url = new URL(urlString);
            textures.setSkin(url);
        } catch (Exception e) {
            e.printStackTrace();
            plugin.getLogger().warning("Failed to parse or apply custom texture URL.");
            return;
        }
        profile.setTextures(textures);
        skull.setPlayerProfile(profile);
        skull.update();

        // 4. Register the head location (the visible block)
        treasureChestManager.addTreasureChest(location, lootResult.getTier());

        // Animation logic will be added here later
        plugin.getLogger().info("Successfully spawned a treasure head at " + location);
    }
}
