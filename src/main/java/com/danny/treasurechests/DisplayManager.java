package com.danny.treasurechests;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import org.bukkit.*;
import org.bukkit.entity.Display;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;
import org.joml.Quaternionf;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DisplayManager {

    private final TreasureChests plugin;
    private final TreasureChestManager treasureChestManager;
    private final Map<Location, BukkitTask> despawnTasks = new HashMap<>();
    private final Map<Location, BukkitTask> debugTasks = new HashMap<>();

    public DisplayManager(TreasureChests plugin, TreasureChestManager treasureChestManager) {
        this.plugin = plugin;
        this.treasureChestManager = treasureChestManager;
    }

    public void spawnTreasure(Location location, LootManager.LootResult lootResult, Player player) {
        if (lootResult == null) return;

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
        ItemDisplay itemDisplay = world.spawn(location.clone().add(0.5, 0, 0.5), ItemDisplay.class);
        itemDisplay.setItemStack(headStack);

        // Set display size for interaction
        itemDisplay.setDisplayWidth(0.8f);
        itemDisplay.setDisplayHeight(0.8f);

        // Set transformation
        itemDisplay.setBillboard(Display.Billboard.FIXED);
        Transformation transformation = itemDisplay.getTransformation();
        transformation.getTranslation().set(0, 0.4f, 0);
        // Snap yaw to 90-degree increments
        float snappedYaw = Math.round(player.getYaw() / 90.0f) * 90.0f;
        transformation.getLeftRotation().set(new Quaternionf().rotateY((float) Math.toRadians(180 - snappedYaw)));
        itemDisplay.setTransformation(transformation);

        // Register it
        treasureChestManager.addTreasureChest(location, new TreasureChestManager.TreasureChestData(lootResult.getTier(), lootResult.getItems()));

        // Run spawn animation
        runScaleAnimation(itemDisplay, lootResult.getTier().getSpawnAnimation(), true);
        playSound(location, lootResult.getTier().getSpawnAnimation().getSound());
        plugin.getLogger().info("Erfolgreich einen Schatz an Position " + location.toVector() + " gespawnt");

        // Schedule despawn and debug tasks
        scheduleDespawn(location, itemDisplay);
    }

    private void scheduleDespawn(Location location, ItemDisplay itemDisplay) {
        final long despawnTime = System.currentTimeMillis() + 60000; // 1 minute from now

        // Debug task
        BukkitTask debugTask = new BukkitRunnable() {
            @Override
            public void run() {
                long remaining = (despawnTime - System.currentTimeMillis()) / 1000;
                if (remaining <= 0) {
                    this.cancel();
                    return;
                }
                String message = ChatColor.GOLD + "[TreasureDebug] " + ChatColor.YELLOW + "Kiste bei " +
                        location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() +
                        " despawned in " + remaining + " Sekunden.";
                Bukkit.getOnlinePlayers().stream().filter(p -> p.isOp() && p.hasPermission("treasurechests.debug")).forEach(op -> op.sendMessage(message));
            }
        }.runTaskTimer(plugin, 0L, 40L); // Every 2 seconds
        debugTasks.put(location, debugTask);

        // Despawn task
        BukkitTask despawnTask = new BukkitRunnable() {
            @Override
            public void run() {
                despawnTreasure(location, itemDisplay);
            }
        }.runTaskLater(plugin, 1200L); // 60 seconds
        despawnTasks.put(location, despawnTask);
    }

    public void despawnTreasure(Location location, ItemDisplay itemDisplay) {
        // Cancel tasks
        if (debugTasks.containsKey(location)) {
            debugTasks.get(location).cancel();
            debugTasks.remove(location);
        }
        if (despawnTasks.containsKey(location)) {
            despawnTasks.remove(location); // No need to cancel, it just ran
        }

        TreasureChestManager.TreasureChestData chestData = treasureChestManager.getChestDataAt(location);
        if (chestData == null) {
            // Failsafe if data is not found
            if (itemDisplay != null && itemDisplay.isValid()) itemDisplay.remove();
            return;
        }

        // Run despawn animation
        runScaleAnimation(itemDisplay, chestData.tier().getDespawnAnimation(), false);
        playSound(location, chestData.tier().getDespawnAnimation().getSound());

        new BukkitRunnable() {
            @Override
            public void run() {
                if (itemDisplay != null && itemDisplay.isValid()) {
                    itemDisplay.remove();
                }
                treasureChestManager.removeTreasureChest(location);
            }
        }.runTaskLater(plugin, chestData.tier().getDespawnAnimation().getScale().getDuration() + 5); // Delay removal
    }


    private void runScaleAnimation(ItemDisplay display, Animation.AnimationInfo animationInfo, boolean isSpawning) {
        if (animationInfo == null || animationInfo.getScale() == null) {
            // If no animation, just set the final scale
            if (isSpawning) {
                Transformation transformation = display.getTransformation();
                transformation.getScale().set(0.65f);
                display.setTransformation(transformation);
            }
            return;
        }

        Animation.ScaleEffect scaleEffect = animationInfo.getScale();
        final double from = scaleEffect.getFrom();
        final double to = scaleEffect.getTo();
        final int duration = scaleEffect.getDuration();
        final float baseSize = 0.65f;

        new BukkitRunnable() {
            private int ticks = 0;
            @Override
            public void run() {
                if (ticks > duration) {
                    this.cancel();
                    // Ensure final scale is set correctly after animation
                    if(isSpawning) {
                        Transformation transformation = display.getTransformation();
                        transformation.getScale().set((float) to * baseSize);
                        display.setTransformation(transformation);
                    }
                    return;
                }

                double progress = (double) ticks / duration;
                float currentScale = (float) (from + (to - from) * progress);

                Transformation transformation = display.getTransformation();
                transformation.getScale().set(currentScale * baseSize);
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
