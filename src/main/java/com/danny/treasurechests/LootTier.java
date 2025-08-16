package com.danny.treasurechests;

import java.util.List;

public class LootTier {

    private final String name;
    private final String displayName;
    private final double chance;
    private final SoundInfo soundInfo;
    private final List<LootItem> items;

    public LootTier(String name, String displayName, double chance, SoundInfo soundInfo, List<LootItem> items) {
        this.name = name;
        this.displayName = displayName;
        this.chance = chance;
        this.soundInfo = soundInfo;
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

    public SoundInfo getSoundInfo() {
        return soundInfo;
    }

    public List<LootItem> getItems() {
        return items;
    }
}
