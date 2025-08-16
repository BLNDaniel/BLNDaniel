package com.danny.treasurechests;

import com.danny.treasurechests.Animation.AnimationInfo;
import com.danny.treasurechests.Animation.ParticleEffect;
import com.danny.treasurechests.Animation.SoundEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class AnimationManager {

    public static void playAnimation(JavaPlugin plugin, Location location, AnimationInfo animationInfo) {
        if (animationInfo == null) return;

        World world = location.getWorld();
        if (world == null) return;

        // Play Sound
        SoundEffect sound = animationInfo.getSound();
        if (sound != null) {
            try {
                world.playSound(location, Sound.valueOf(sound.getName().toUpperCase()), sound.getVolume(), sound.getPitch());
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Ungültiger Sound-Name in der Konfiguration: " + sound.getName());
            }
        }

        // Play Particles
        if (!animationInfo.getParticles().isEmpty()) {
            new BukkitRunnable() {
                private int ticks = 0;
                @Override
                public void run() {
                    if (ticks > 10) { // Animation duration: 10 ticks
                        this.cancel();
                        return;
                    }
                    for (ParticleEffect particle : animationInfo.getParticles()) {
                        world.spawnParticle(
                                particle.getType(),
                                location.clone().add(0.5, 0.5, 0.5), // Center of the block
                                particle.getCount(),
                                0.3, 0.3, 0.3, // spread
                                particle.getSpeed()
                        );
                    }
                    ticks++;
                }
            }.runTaskTimer(plugin, 0L, 1L); // Run every tick
        }
    }
}
