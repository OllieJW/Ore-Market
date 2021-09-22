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
            String message = OreMarket.main().getMsgConfig().getString("messages.reload", "&aReloaded successfully. Some options may require a restart to take place.");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        } else {
            String message = OreMarket.main().getMsgConfig().getString("messages.insufficient-permission", "&cYou do not have permission to do this!");
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
        return true;
    }
}
