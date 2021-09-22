package me.olliejw.oremarket.commands;

import me.olliejw.oremarket.OreMarket;
import me.olliejw.oremarket.menus.MainGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenMarket implements CommandExecutor {
    MainGUI mainGUI = new MainGUI();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("openmarket")) {
            if (sender.hasPermission("oremarket.open")) {
                mainGUI.createGUI((Player) sender);
            } else {
                String message = OreMarket.main().getMsgConfig().getString("messages.insufficient-permission", "&cYou do not have permission to do this!");
                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            }
        }
        return true;
    }
}
