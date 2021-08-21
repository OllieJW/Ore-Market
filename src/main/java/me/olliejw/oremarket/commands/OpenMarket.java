package me.olliejw.oremarket.commands;

import me.olliejw.oremarket.menus.MainGUI;
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
            }
        }
        return true;
    }
}
