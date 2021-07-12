package com.olliejw.oremarket.Commands;

import com.olliejw.oremarket.OreMarket;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Reload implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        OreMarket.main().reloadConfig();
        sender.sendMessage(ChatColor.GREEN + "Reloaded successfully");
        return true;
    }
}
