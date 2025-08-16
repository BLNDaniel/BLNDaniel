package com.danny.treasurechests;

import org.bukkit.plugin.java.JavaPlugin;

public class TreasureChests extends JavaPlugin {

    private LootManager lootManager;
    private ChestSpawner chestSpawner;
    private TiersGui tiersGui;
    private TierEditorGui tierEditorGui;

    @Override
    public void onEnable() {
        // Save a copy of the default config.yml if one is not present
        saveDefaultConfig();

        // Initialize managers and handlers
        this.lootManager = new LootManager(this);
        this.chestSpawner = new ChestSpawner(this);
        this.tiersGui = new TiersGui(this);
        this.tierEditorGui = new TierEditorGui(this);

        // Load loot tables from config
        this.lootManager.loadLootTables();

        // Register the event listener
        getServer().getPluginManager().registerEvents(new BlockBreakListener(this, lootManager, chestSpawner), this);

        // Register the command executor
        getCommand("yarrack").setExecutor(new YarrackCommand(this, tiersGui));

        // Register the GUI Manager
        getServer().getPluginManager().registerEvents(new GuiManager(this), this);

        getLogger().info("TreasureChests has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("TreasureChests has been disabled!");
    }

    public LootManager getLootManager() {
        return lootManager;
    }

    public TierEditorGui getTierEditorGui() {
        return tierEditorGui;
    }
}
