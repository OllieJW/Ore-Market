package com.olliejw.oremarket.Utils;

import com.olliejw.oremarket.OreMarket;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Objects;

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

    @EventHandler
    public void dragEvent (InventoryDragEvent event) {
        if (event.getInventory() == inv) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void clickEvent (InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        int pay = OreMarket.main().getConfig().getInt("items." + event.getSlot() + ".value");
        EconomyResponse r = OreMarket.getEconomy().depositPlayer(event.getWhoClicked().getName(), pay);

        if(r.transactionSuccess()) {
            event.getWhoClicked().sendMessage(String.format("You were given %s and now have %s", OreMarket.getEconomy().format(r.amount), OreMarket.getEconomy().format(r.balance)));
        } else {
            event.getWhoClicked().sendMessage(String.format("An error occured: %s", r.errorMessage));
        }

        event.setCancelled(true);

    }
}

