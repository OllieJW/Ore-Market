package com.olliejw.oremarket.Commands;

import com.olliejw.oremarket.Utils.Stats;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class StatsCommands implements CommandExecutor {
    Stats stats = new Stats();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("om-stats")) {
            if (sender.hasPermission("oremarket.stats")) {
                sender.sendMessage("Total Items: " + stats.totalItems());
                sender.sendMessage("Total Value: " + stats.totalValues());
                sender.sendMessage("https://bstats.org/plugin/bukkit/OreMarket/10961");
            }
        }
        return true;
    }
}
