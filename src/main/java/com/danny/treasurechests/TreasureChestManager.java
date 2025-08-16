package com.danny.treasurechests;

import org.bukkit.Location;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class TreasureChestManager {

    private final Map<Location, LootTier> treasureChests = new HashMap<>();

    public void addTreasureChest(Location location, LootTier tier) {
        treasureChests.put(location, tier);
    }

    public void removeTreasureChest(Location location) {
        treasureChests.remove(location);
    }

    public LootTier getTierAt(Location location) {
        return treasureChests.get(location);
    }

    public boolean isTreasureChest(Location location) {
        return treasureChests.containsKey(location);
    }
}
