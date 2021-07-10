package com.olliejw.oremarket.Utils;

import com.olliejw.oremarket.OreMarket;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MarketGUI implements Listener, CommandExecutor {

    Inventory inv = Bukkit.createInventory(null, 27, "OreMarket Coming soon!");

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("openmarket")) {
            Player player = (Player) sender;

            for (String key : OreMarket.main().getConfig().getConfigurationSection("items").getKeys(false)) {
                ConfigurationSection keySection = OreMarket.main().getConfig().getConfigurationSection("items").getConfigurationSection(key);
                assert keySection != null;

                ItemStack item = new ItemStack(Material.matchMaterial(keySection.getString("item")));
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', keySection.getString("name")));
                item.setItemMeta(meta);

                inv.setItem(Integer.parseInt(key), item);
                player.openInventory(inv);
            }
        }
        return true;
    }
}

