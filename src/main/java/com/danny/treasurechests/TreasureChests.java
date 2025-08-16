package com.danny.treasurechests;

import org.bukkit.plugin.java.JavaPlugin;

public class TreasureChests extends JavaPlugin {

    private LootManager lootManager;
    private ChestSpawner chestSpawner;
    private TreasureChestManager treasureChestManager;

    @Override
    public void onEnable() {
        // Save a copy of the default config.yml if one is not present
        saveDefaultConfig();

        // Initialize managers and handlers
        this.lootManager = new LootManager(this);
        this.treasureChestManager = new TreasureChestManager();
        this.chestSpawner = new ChestSpawner(this, treasureChestManager);

        // Load loot tables from config
        this.lootManager.loadLootTables();

        // Register event listeners
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this, lootManager, chestSpawner), this);
        getServer().getPluginManager().registerEvents(new ChestOpenListener(this, treasureChestManager), this);

        getLogger().info("TreasureChests has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("TreasureChests has been disabled!");
    }
}
