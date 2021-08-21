package me.olliejw.oremarket.commands;

import me.olliejw.oremarket.OreMarket;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Reload implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("oremarket.reload")) {
            OreMarket.main().reloadConfig();
            OreMarket.main().reloadGuiConfig();
            sender.sendMessage(ChatColor.GREEN + "Reloaded successfully. Some options may require a restart to take place.");
        } else {
            sender.sendMessage(ChatColor.RED + "You do not have permission to do this!");
        }
        return true;
    }
}
