package com.danny.treasurechests;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class YarrackCommand implements CommandExecutor {

    private final TreasureChests plugin;
    private final TiersGui tiersGui;

    public YarrackCommand(TreasureChests plugin, TiersGui tiersGui) {
        this.plugin = plugin;
        this.tiersGui = tiersGui;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be run by a player.");
            return true;
        }

        Player player = (Player) sender;
        if (!player.isOp()) {
            player.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            return true;
        }

        tiersGui.open(player);
        return true;
    }
}
