package com.danny.treasurechests;

import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class TreasureChestInventoryHolder implements InventoryHolder {

    private final Location chestLocation;

    public TreasureChestInventoryHolder(Location chestLocation) {
        this.chestLocation = chestLocation;
    }

    public Location getChestLocation() {
        return chestLocation;
    }

    @Override
    public Inventory getInventory() {
        // This is required by the interface but not used in this implementation.
        return null;
    }
}
