package com.danny.treasurechests;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreasureChestManager {

    public record TreasureChestData(LootTier tier, List<ItemStack> items) {}

    private final Map<Location, TreasureChestData> treasureChests = new HashMap<>();

    public void addTreasureChest(Location location, TreasureChestData data) {
        treasureChests.put(location, data);
    }

    public void removeTreasureChest(Location location) {
        treasureChests.remove(location);
    }

    public TreasureChestData getChestDataAt(Location location) {
        return treasureChests.get(location);
    }

    public boolean isTreasureChest(Location location) {
        return treasureChests.containsKey(location);
    }
}
