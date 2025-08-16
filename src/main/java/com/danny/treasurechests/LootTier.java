package com.danny.treasurechests;

import java.util.List;

public class LootTier {

    private final String name;
    private final String displayName;
    private final double chance;
    private final List<LootItem> items;

    public LootTier(String name, String displayName, double chance, List<LootItem> items) {
        this.name = name;
        this.displayName = displayName;
        this.chance = chance;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getChance() {
        return chance;
    }

    public List<LootItem> getItems() {
        return items;
    }
}
