package com.danny.treasurechests;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Barrel;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

import java.util.Collections;
import java.util.UUID;

public class DisplayManager {

    private final TreasureChests plugin;
    private final TreasureChestManager treasureChestManager;

    public DisplayManager(TreasureChests plugin, TreasureChestManager treasureChestManager) {
        this.plugin = plugin;
        this.treasureChestManager = treasureChestManager;
    }

    public void spawnTreasure(Location location, LootManager.LootResult lootResult) {
        if (lootResult == null) return;

        // Place the barrel
        location.getBlock().setType(Material.BARREL);
        Barrel barrel = (Barrel) location.getBlock().getState();
        java.util.List<Integer> slots = new java.util.ArrayList<>();
        for (int i = 0; i < barrel.getInventory().getSize(); i++) slots.add(i);
        Collections.shuffle(slots);
        for (int i = 0; i < lootResult.getItems().size(); i++) {
            if (i >= slots.size()) break;
            barrel.getInventory().setItem(slots.get(i), lootResult.getItems().get(i));
        }

        // Create the head item stack
        ItemStack headStack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) headStack.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID());
        profile.setProperty(new ProfileProperty("textures", lootResult.getTier().getHeadTexture()));
        meta.setPlayerProfile(profile);
        headStack.setItemMeta(meta);

        // Spawn the Item Display
        World world = location.getWorld();
        if (world == null) return;
        ItemDisplay itemDisplay = world.spawn(location.clone().add(0.5, 0.25, 0.5), ItemDisplay.class);
        itemDisplay.setItemStack(headStack);
        itemDisplay.setBillboard(Display.Billboard.CENTER);

        // Register it
        treasureChestManager.addTreasureChest(location, lootResult.getTier());

        // Run spawn animation
        runScaleAnimation(itemDisplay, lootResult.getTier().getSpawnAnimation());
        playSound(location, lootResult.getTier().getSpawnAnimation().getSound());
        plugin.getLogger().info("Erfolgreich einen Schatz an Position " + location.toVector() + " gespawnt");
    }

    public void despawnTreasure(Location location, ItemDisplay itemDisplay) {
        LootTier tier = treasureChestManager.getTierAt(location);
        if (tier == null) {
            // Failsafe if tier is not found
            itemDisplay.remove();
            location.getBlock().setType(Material.AIR);
            return;
        }

        // Run despawn animation
        runScaleAnimation(itemDisplay, tier.getDespawnAnimation());
        playSound(location, tier.getDespawnAnimation().getSound());

        new BukkitRunnable() {
            @Override
            public void run() {
                itemDisplay.remove();
                location.getBlock().setType(Material.AIR);
                treasureChestManager.removeTreasureChest(location);
            }
        }.runTaskLater(plugin, tier.getDespawnAnimation().getScale().getDuration() + 5); // Delay removal
    }

    private void runScaleAnimation(ItemDisplay display, Animation.AnimationInfo animationInfo) {
        if (animationInfo == null || animationInfo.getScale() == null) return;

        Animation.ScaleEffect scaleEffect = animationInfo.getScale();
        final double from = scaleEffect.getFrom();
        final double to = scaleEffect.getTo();
        final int duration = scaleEffect.getDuration();

        new BukkitRunnable() {
            private int ticks = 0;
            @Override
            public void run() {
                if (ticks > duration) {
                    this.cancel();
                    return;
                }

                double progress = (double) ticks / duration;
                float currentScale = (float) (from + (to - from) * progress);

                Transformation transformation = display.getTransformation();
                transformation.getScale().set(currentScale);
                display.setTransformation(transformation);

                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void playSound(Location location, Animation.SoundEffect sound) {
        if (sound == null) return;
        try {
            location.getWorld().playSound(location, Sound.valueOf(sound.getName().toUpperCase()), sound.getVolume(), sound.getPitch());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Ungültiger Sound-Name in der Konfiguration: " + sound.getName());
        }
    }
}
