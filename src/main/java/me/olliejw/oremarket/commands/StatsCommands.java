package me.olliejw.oremarket.commands;

import me.olliejw.oremarket.OreMarket;
import me.olliejw.oremarket.utils.Stats;
import org.bukkit.Bukkit;
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
                sender.sendMessage("== Your server's stats ==");
                sender.sendMessage("Total Items: " + stats.totalItems());
                sender.sendMessage("Total Market Value: " + stats.totalValues());
                sender.sendMessage("Online Players: " + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());
                sender.sendMessage("Global Stats: https://bstats.org/plugin/bukkit/OreMarket/10961");
            } else {
                String message = OreMarket.main().getMsgConfig().getString("messages.insufficient-permission", "&cYou do not have permission to do this!");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        }
        return true;
    }
}
