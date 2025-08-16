package com.danny.treasurechests;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LootManager {

    private final TreasureChests plugin;
    private final Map<String, LootTier> lootTiers = new HashMap<>();
    private double totalTierChance = 0.0;

    public LootManager(TreasureChests plugin) {
        this.plugin = plugin;
    }

    public void loadLootTables() {
        plugin.getLogger().info("Loading loot tables...");
        lootTiers.clear();
        totalTierChance = 0.0;

        ConfigurationSection tiersSection = plugin.getConfig().getConfigurationSection("loot-tables.tiers");
        if (tiersSection == null) {
            plugin.getLogger().warning("'loot-tables.tiers' section not found in config.yml! No loot will be available.");
            return;
        }

        for (String tierName : tiersSection.getKeys(false)) {
            ConfigurationSection tierSection = tiersSection.getConfigurationSection(tierName);
            if (tierSection == null) continue;

            double tierChance = tierSection.getDouble("chance");
            String displayName = tierSection.getString("display-name", tierName);

            List<LootItem> items = new ArrayList<>();
            List<Map<?, ?>> itemMaps = tierSection.getMapList("items");

            for (Map<?, ?> itemMap : itemMaps) {
                try {
                    String materialName = (String) itemMap.get("material");
                    org.bukkit.Material material = org.bukkit.Material.getMaterial(materialName.toUpperCase());
                    if (material == null) {
                        plugin.getLogger().warning("Invalid material '" + materialName + "' in tier '" + tierName + "'. Skipping item.");
                        continue;
                    }

                    String amount = "1";
                    Object amountObj = itemMap.get("amount");
                    if (amountObj != null) {
                        amount = amountObj.toString();
                    }

                    double chance = 1.0;
                    Object chanceObj = itemMap.get("chance");
                    if (chanceObj instanceof Number) {
                        chance = ((Number) chanceObj).doubleValue();
                    }

                    items.add(new LootItem(material, amount, chance));
                } catch (Exception e) {
                    plugin.getLogger().severe("Failed to parse an item in tier '" + tierName + "'. Error: " + e.getMessage());
                }
            }

            if (items.isEmpty()) {
                plugin.getLogger().warning("No valid items found for tier '" + tierName + "'. This tier will be skipped.");
                continue;
            }

            LootTier lootTier = new LootTier(tierName, displayName, tierChance, items);
            lootTiers.put(tierName, lootTier);
            totalTierChance += tierChance;
            plugin.getLogger().info("Loaded tier '" + tierName + "' with " + items.size() + " items.");
        }

        if (lootTiers.isEmpty()) {
            plugin.getLogger().severe("No valid loot tiers were loaded! The plugin will not function correctly.");
        } else {
            plugin.getLogger().info("Successfully loaded " + lootTiers.size() + " loot tiers.");
        }
    }

    public LootResult calculateRandomLoot() {
        // 1. Choose a Tier
        LootTier chosenTier = chooseRandomTier();
        if (chosenTier == null) {
            return null; // No tier could be chosen
        }

        // 2. Choose Items from the Tier
        List<ItemStack> items = new ArrayList<>();
        int rolls = parseAmount(plugin.getConfig().getString("loot-tables.rolls", "1"));

        for (int i = 0; i < rolls; i++) {
            LootItem chosenItem = chooseRandomItem(chosenTier);
            if (chosenItem != null) {
                items.add(createItemStack(chosenItem));
            }
        }

        return new LootResult(chosenTier, items);
    }

    private LootTier chooseRandomTier() {
        if (lootTiers.isEmpty()) return null;

        double randomValue = new java.util.Random().nextDouble() * totalTierChance;
        double cumulativeWeight = 0.0;
        for (LootTier tier : lootTiers.values()) {
            cumulativeWeight += tier.getChance();
            if (randomValue < cumulativeWeight) {
                return tier;
            }
        }
        return null; // Should not happen if totalTierChance > 0
    }

    private LootItem chooseRandomItem(LootTier tier) {
        double totalItemChance = 0.0;
        for (LootItem item : tier.getItems()) {
            totalItemChance += item.getChance();
        }

        double randomValue = new java.util.Random().nextDouble() * totalItemChance;
        double cumulativeWeight = 0.0;
        for (LootItem item : tier.getItems()) {
            cumulativeWeight += item.getChance();
            if (randomValue < cumulativeWeight) {
                return item;
            }
        }
        return null;
    }

    private ItemStack createItemStack(LootItem item) {
        return new ItemStack(item.getMaterial(), parseAmount(item.getAmount()));
    }

    private int parseAmount(String amountString) {
        if (amountString.contains("-")) {
            try {
                String[] parts = amountString.split("-");
                int min = Integer.parseInt(parts[0]);
                int max = Integer.parseInt(parts[1]);
                return new java.util.Random().nextInt((max - min) + 1) + min;
            } catch (NumberFormatException e) {
                return 1; // Default to 1 on parse error
            }
        }
        try {
            return Integer.parseInt(amountString);
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    // A simple inner class to hold the result of a loot calculation.
    public static class LootResult {
        private final LootTier tier;
        private final List<ItemStack> items;

        public LootResult(LootTier tier, List<ItemStack> items) {
            this.tier = tier;
            this.items = items;
        }

        public LootTier getTier() {
            return tier;
        }

        public List<ItemStack> getItems() {
            return items;
        }
    }
}
