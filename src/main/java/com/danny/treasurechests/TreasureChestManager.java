package com.danny.treasurechests;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TreasureChestManager {

    public record TreasureChestData(LootTier tier, List<ItemStack> items, UUID displayId, UUID interactionId) {}

    private final Map<Location, TreasureChestData> treasureChests = new HashMap<>();
    private final Map<UUID, Location> interactionIdToLocation = new HashMap<>();

    public void addTreasureChest(Location location, TreasureChestData data) {
        treasureChests.put(location, data);
        interactionIdToLocation.put(data.interactionId(), location);
    }

    public void removeTreasureChest(Location location) {
        TreasureChestData data = treasureChests.remove(location);
        if (data != null) {
            interactionIdToLocation.remove(data.interactionId());
        }
    }

    public TreasureChestData getChestDataAt(Location location) {
        return treasureChests.get(location);
    }

    public Location getLocationFromInteractionId(UUID interactionId) {
        return interactionIdToLocation.get(interactionId);
    }

    public boolean isTreasureChest(Location location) {
        return treasureChests.containsKey(location);
    }

    public boolean isTreasureChestInteraction(UUID interactionId) {
        return interactionIdToLocation.containsKey(interactionId);
    }
}
