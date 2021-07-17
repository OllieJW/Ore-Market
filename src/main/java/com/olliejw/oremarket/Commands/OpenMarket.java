package com.olliejw.oremarket.Commands;

import com.olliejw.oremarket.Inventory.CreateGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpenMarket implements CommandExecutor {
    CreateGUI createGUI = new CreateGUI();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("openmarket")) {
            if (sender.hasPermission("oremarket.open")) {
                createGUI.createGUI((Player) sender);
            }
        }
        return true;
    }
}
