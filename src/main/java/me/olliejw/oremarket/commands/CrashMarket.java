package me.olliejw.oremarket.commands;

import me.olliejw.oremarket.events.MarketCrash;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CrashMarket implements CommandExecutor {
    MarketCrash mkCrash = new MarketCrash();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("om-crash")) {
            if (sender.hasPermission("oremarket.crashmarket")) {
                mkCrash.forceCrash();
            }
        }
        return true;
    }
}
